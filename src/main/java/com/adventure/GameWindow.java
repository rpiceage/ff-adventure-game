package com.adventure;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class GameWindow extends JFrame {
    private JTextArea textArea;
    private JPanel buttonPanel;
    private JScrollPane buttonScrollPane;
    private HeroStatsPanel heroStatsPanel;
    private InventoryPanel inventoryPanel;
    private GameController controller;
    private NotificationManager notificationManager;
    private ChapterStateManager chapterState;
    private IllustrationManager illustrationManager;
    private ActionButtonFactory actionButtonFactory;
    private com.adventure.ui.BattleUI battleUI;
    private com.adventure.ui.LuckUI luckUI;
    private com.adventure.ui.RandomModifyUI randomModifyUI;
    private com.adventure.ui.RandomGotoUI randomGotoUI;
    private com.adventure.ui.AttributeTestUI attributeTestUI;
    private JScrollPane textScrollPane;
    private JScrollPane textWithIllustrationPanel;
    private JPanel currentCenterPanel;
    private JPanel currentDicePanel;

    public GameWindow(Adventure adventure) {
        this(adventure, null);
    }
    
    public GameWindow(Adventure adventure, String gameYamlPath) {
        this.controller = new GameController(adventure, gameYamlPath);
        this.notificationManager = new NotificationManager(this);
        this.chapterState = new ChapterStateManager();
        this.illustrationManager = new IllustrationManager(gameYamlPath);
        setTitle(adventure.title);
        setSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create menu bar
        createMenuBar();

        try {
            InputStream bgStream = getClass().getClassLoader().getResourceAsStream("pergament.jpg");
            BufferedImage bgImage = ImageIO.read(bgStream);
            
            textArea = new JTextArea() {
                private BufferedImage illustration = null;
                
                public void setIllustration(BufferedImage img) {
                    this.illustration = img;
                    repaint();
                }
                
                @Override
                protected void paintComponent(Graphics g) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                    super.paintComponent(g);
                    
                    // Draw illustration on top right if available
                    if (illustration != null) {
                        int x = getWidth() - illustration.getWidth() - 10;
                        int y = 10;
                        g.drawImage(illustration, x, y, this);
                    }
                }
            };
            textArea.setOpaque(false);
        } catch (Exception e) {
            textArea = new JTextArea();
        }
        
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(UIConstants.FONT_TITLE);
        // Add right margin for illustration (310px = 300px image + 10px padding)
        textArea.setMargin(UIConstants.TEXT_MARGINS);
        
        textScrollPane = new JScrollPane(textArea);
        textWithIllustrationPanel = textScrollPane;
        add(textScrollPane, BorderLayout.CENTER);

        // Create hero stats panel
        heroStatsPanel = new HeroStatsPanel(controller.getHero(), this::consumeProvision);
        
        // Items section
        JLabel itemsTitle = new JLabel(Messages.get(Messages.Key.ITEMS_TITLE) + ":") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(UIConstants.SEMI_TRANSPARENT_BLACK);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        itemsTitle.setFont(UIConstants.FONT_LARGE);
        itemsTitle.setForeground(Color.WHITE);
        itemsTitle.setOpaque(false);
        heroStatsPanel.add(Box.createVerticalStrut(20));
        heroStatsPanel.add(itemsTitle);
        
        inventoryPanel = new InventoryPanel(controller.getHero(), controller, chapterState, 
            this::updateDisplay, () -> heroStatsPanel.updateStats(battleUI != null));
        heroStatsPanel.add(inventoryPanel);
        
        add(heroStatsPanel, BorderLayout.EAST);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonScrollPane = new JScrollPane(buttonPanel);
        buttonScrollPane.setPreferredSize(new Dimension(0, UIConstants.BUTTON_PANEL_HEIGHT));
        buttonScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        buttonScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Initialize action button factory after buttonPanel is created
        this.actionButtonFactory = new ActionButtonFactory(controller, chapterState, 
            this::updateDisplay, () -> inventoryPanel.updateItems(), buttonPanel);
        
        // Add listener to recalculate button panel height when viewport size changes
        buttonScrollPane.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                recalculateButtonPanelHeight();
            }
        });
        
        add(buttonScrollPane, BorderLayout.SOUTH);

        updateDisplay();
        setVisible(true);
    }

    public void updateDisplay() {
        heroStatsPanel.updateStats(battleUI != null);
        
        Hero hero = controller.getHero();
        inventoryPanel.updateItems();
        
        List<String> mods = hero.getLastModifications();
        if (!mods.isEmpty()) {
            notificationManager.show(String.join("\n", mods));
            hero.clearModifications();
        }
        
        if (controller.isGameOver()) {
            // Check if there's a death action with custom text
            String deathText = null;
            for (Map<String, Object> actionData : controller.getCurrentChapter().actions) {
                if (actionData.containsKey("death")) {
                    deathText = (String) actionData.get("death");
                    break;
                }
            }
            
            if (deathText != null) {
                textArea.setText(deathText);
            } else {
                textArea.setText(Messages.get(Messages.Key.GAME_OVER));
            }
            textArea.setCaretPosition(0);
            
            buttonPanel.removeAll();
            battleUI = null;
            luckUI = null;
            attributeTestUI = null;
            
            try {
                InputStream skullStream = getClass().getClassLoader().getResourceAsStream("skull.jpg");
                BufferedImage skullImage = ImageIO.read(skullStream);
                JLabel skullLabel = new JLabel(new ImageIcon(skullImage));
                remove(heroStatsPanel);
                add(skullLabel, BorderLayout.EAST);
                revalidate();
                repaint();
            } catch (Exception ex) {
                // Keep stats panel if image fails to load
            }
        } else if (battleUI != null && battleUI.isActive()) {
            battleUI.updateDisplay();
        } else if (controller.getSavedBattle() != null && 
                   controller.getReturnChapter() != null &&
                   controller.getCurrentChapter().index == controller.getReturnChapter()) {
            // Auto-resume saved battle only if we're at the return chapter
            Battle savedBattle = controller.getSavedBattle();
            Map<String, Object> savedActionData = controller.getSavedBattleActionData();
            controller.clearSavedBattle();
            controller.clearReturnChapter();
            
            controller.getAdventureLog().log("  Battle resumed");
            battleUI = new com.adventure.ui.BattleUI(textArea, buttonPanel, controller, this, () -> {
                battleUI = null;
                if (currentCenterPanel != null) {
                    remove(currentCenterPanel);
                    currentCenterPanel = null;
                }
                // Recreate scroll pane with textArea
                textScrollPane = new JScrollPane(textArea);
                textWithIllustrationPanel = textScrollPane;
                add(textScrollPane, BorderLayout.CENTER);
                revalidate();
                repaint();
                updateDisplay();
            });
            JPanel battlePanel = battleUI.resume(savedBattle, savedActionData);
            remove(textWithIllustrationPanel);
            add(battlePanel, BorderLayout.CENTER);
            currentCenterPanel = battlePanel;
            revalidate();
            repaint();
        } else if (randomModifyUI != null) {
            // Random modify UI is already shown
        } else if (randomGotoUI != null) {
            // Random goto UI is already shown
        } else if (luckUI != null) {
            // Luck test UI is already shown
        } else if (attributeTestUI != null) {
            // Attribute test UI is already shown
        } else {
            textArea.setText(controller.getDisplayText());
            textArea.setCaretPosition(0);
            buttonPanel.removeAll();
            
            // Remove dice panel only if chapter changed
            int currentChapter = controller.getCurrentChapter().index;
            if (currentChapter != chapterState.getLastDisplayedChapter()) {
                chapterState.resetForNewChapter();
                
                // Update illustration for new chapter
                java.awt.image.BufferedImage illustration = illustrationManager.loadIllustration(currentChapter);
                if (illustration != null) {
                    try {
                        java.lang.reflect.Method setIllustration = textArea.getClass().getMethod("setIllustration", java.awt.image.BufferedImage.class);
                        setIllustration.invoke(textArea, illustration);
                    } catch (Exception e) {
                        // Method not available
                    }
                }
                
                if (currentDicePanel != null) {
                    // Extract text scroll pane from wrapper before removing
                    Component[] components = currentDicePanel.getComponents();
                    for (Component comp : components) {
                        if (comp instanceof JScrollPane) {
                            currentDicePanel.remove(comp);
                            getContentPane().add(comp, BorderLayout.CENTER);
                            break;
                        }
                    }
                    getContentPane().remove(currentDicePanel);
                    currentDicePanel = null;
                    getContentPane().revalidate();
                    getContentPane().repaint();
                }
            }
            chapterState.setLastDisplayedChapter(currentChapter);
            
            // Show buttons for all actions in the chapter
            
            // Check if this chapter has an unexecuted randomModify or randomGoto
            boolean hasUnexecutedRandomModify = false;
            boolean hasUnexecutedRandomGoto = false;
            for (Map<String, Object> actionData : controller.getCurrentChapter().actions) {
                if (actionData.containsKey("randomModify") && 
                    !chapterState.hasExecutedRandomModify(controller.getCurrentChapter().index)) {
                    hasUnexecutedRandomModify = true;
                    break;
                }
                if (actionData.containsKey("randomGoto") && 
                    !chapterState.hasExecutedRandomGoto(controller.getCurrentChapter().index)) {
                    hasUnexecutedRandomGoto = true;
                    break;
                }
            }
            
            for (Map<String, Object> actionData : controller.getCurrentChapter().actions) {
                com.adventure.actions.Action action = controller.getActionForData(actionData);
                
                // Skip randomModify if already executed in this chapter
                if (actionData.containsKey("randomModify") && 
                    chapterState.hasExecutedRandomModify(controller.getCurrentChapter().index)) {
                    continue;
                }
                
                // Skip randomGoto if already executed in this chapter
                if (actionData.containsKey("randomGoto") && 
                    chapterState.hasExecutedRandomGoto(controller.getCurrentChapter().index)) {
                    continue;
                }
                
                // Skip other buttons if randomModify or randomGoto hasn't been executed yet
                if ((hasUnexecutedRandomModify && !actionData.containsKey("randomModify")) ||
                    (hasUnexecutedRandomGoto && !actionData.containsKey("randomGoto"))) {
                    continue;
                }
                
                if (action != null) {
                    if (action.getActionType() == com.adventure.actions.ActionType.MULTIPLE_BUTTONS) {
                        // Use factory to create all button types
                        List<JButton> buttons = actionButtonFactory.createButtons(action, actionData);
                        for (JButton btn : buttons) {
                            buttonPanel.add(btn);
                        }
                    } else if (action.getActionType() == com.adventure.actions.ActionType.SINGLE_BUTTON) {
                        JButton actionButton = new JButton(action.getButtonText());
                        actionButton.addActionListener(e -> {
                            actionButton.setEnabled(false);
                            handleSingleButtonAction(action, actionData);
                        });
                        buttonPanel.add(actionButton);
                    }
                }
            }
        }
        
        recalculateButtonPanelHeight();
        buttonPanel.revalidate();
        buttonPanel.repaint();
        buttonScrollPane.revalidate();
    }
    
    private void recalculateButtonPanelHeight() {
        // Manually calculate wrapped height for FlowLayout
        int panelWidth = buttonScrollPane.getViewport().getWidth();
        if (panelWidth > 0 && buttonPanel.getComponentCount() > 0) {
            FlowLayout layout = (FlowLayout) buttonPanel.getLayout();
            int hgap = layout.getHgap();
            int vgap = layout.getVgap();
            
            int rowHeight = 0;
            int totalHeight = vgap;
            int currentWidth = hgap;
            
            for (Component comp : buttonPanel.getComponents()) {
                Dimension d = comp.getPreferredSize();
                if (currentWidth + d.width + hgap > panelWidth && currentWidth > hgap) {
                    totalHeight += rowHeight + vgap;
                    currentWidth = hgap;
                    rowHeight = 0;
                }
                currentWidth += d.width + hgap;
                rowHeight = Math.max(rowHeight, d.height);
            }
            totalHeight += rowHeight + vgap;
            
            buttonPanel.setPreferredSize(new Dimension(panelWidth, totalHeight));
            buttonPanel.revalidate();
        }
    }

    public void updateHeroStats() {
        heroStatsPanel.updateStats(battleUI != null);
    }
    
    // Getters for testing
    public HeroStatsPanel getStatsPanel() { return heroStatsPanel; }
    public JLabel getStaminaLabel() { return heroStatsPanel.getStaminaLabel(); }
    public JButton getProvisionsButton() { return heroStatsPanel.getProvisionsButton(); }
    public InventoryPanel getItemsPanel() { return inventoryPanel; }

    private void handleSingleButtonAction(com.adventure.actions.Action action, Map<String, Object> actionData) {
        if (actionData.containsKey("battle")) {
            controller.getAdventureLog().log("  Battle started");
            battleUI = new com.adventure.ui.BattleUI(textArea, buttonPanel, controller, this, () -> {
                battleUI = null;
                if (currentCenterPanel != null) {
                    remove(currentCenterPanel);
                    currentCenterPanel = null;
                }
                // Recreate scroll pane with textArea
                textScrollPane = new JScrollPane(textArea);
                textWithIllustrationPanel = textScrollPane;
                add(textScrollPane, BorderLayout.CENTER);
                revalidate();
                repaint();
                updateDisplay();
            });
            JPanel battlePanel = battleUI.start(actionData);
            remove(textWithIllustrationPanel);
            add(battlePanel, BorderLayout.CENTER);
            currentCenterPanel = battlePanel;
            revalidate();
            repaint();
        } else if (actionData.containsKey("randomModify")) {
            randomModifyUI = new com.adventure.ui.RandomModifyUI(controller, this, () -> {
                randomModifyUI = null;
                // Mark this chapter as having executed randomModify
                chapterState.markRandomModifyExecuted(controller.getCurrentChapter().index);
                // Log the modification
                List<String> mods = controller.getHero().getLastModifications();
                if (!mods.isEmpty()) {
                    for (String mod : mods) {
                        controller.getAdventureLog().log("  Random modify: " + mod);
                    }
                    controller.getHero().clearModifications();
                }
                updateDisplay();
            });
            randomModifyUI.rollDice(actionData);
        } else if (actionData.containsKey("randomGoto")) {
            randomGotoUI = new com.adventure.ui.RandomGotoUI(controller, this);
            randomGotoUI.rollDice(actionData, () -> {
                controller.getAdventureLog().log("  Random goto executed");
                randomGotoUI = null;
            });
        } else if (actionData.containsKey("luck")) {
            controller.getAdventureLog().log("  Luck test");
            luckUI = new com.adventure.ui.LuckUI(textArea, buttonPanel, controller, this, () -> {
                controller.getAdventureLog().log("  Luck test completed - LUCK: " + controller.getHero().getLuck());
                luckUI = null;
                if (currentCenterPanel != null) {
                    remove(currentCenterPanel);
                    currentCenterPanel = null;
                }
                // Recreate scroll pane with textArea
                textScrollPane = new JScrollPane(textArea);
                textWithIllustrationPanel = textScrollPane;
                add(textScrollPane, BorderLayout.CENTER);
                revalidate();
                repaint();
                updateDisplay();
            });
            JPanel luckPanel = luckUI.start(actionData);
            remove(textWithIllustrationPanel);
            add(luckPanel, BorderLayout.CENTER);
            currentCenterPanel = luckPanel;
            revalidate();
            repaint();
        } else if (actionData.containsKey("attributeTest")) {
            attributeTestUI = new com.adventure.ui.AttributeTestUI(textArea, buttonPanel, controller, () -> {
                attributeTestUI = null;
                if (currentCenterPanel != null) {
                    remove(currentCenterPanel);
                    currentCenterPanel = null;
                }
                // Recreate scroll pane with textArea
                textScrollPane = new JScrollPane(textArea);
                textWithIllustrationPanel = textScrollPane;
                add(textScrollPane, BorderLayout.CENTER);
                revalidate();
                repaint();
                updateDisplay();
            });
            JPanel testPanel = attributeTestUI.start(actionData);
            remove(textWithIllustrationPanel);
            add(testPanel, BorderLayout.CENTER);
            currentCenterPanel = testPanel;
            revalidate();
            repaint();
        } else if (actionData.containsKey("interrupt")) {
            // Execute interrupt action (returns to previous chapter)
            action.execute(controller, actionData);
            updateDisplay();
        }
    }

    private void consumeProvision() {
        Hero hero = controller.getHero();
        
        if (hero.getStamina() >= hero.getInitialStamina()) {
            JOptionPane.showMessageDialog(this, 
                Messages.get(Messages.Key.PROVISIONS_NOT_HUNGRY), 
                Messages.get(Messages.Key.PROVISIONS), 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (hero.consumeProvision()) {
            controller.getAdventureLog().log(Messages.get(Messages.Key.LOG_CONSUMED_PROVISION));
            heroStatsPanel.updateStats(battleUI != null);
            updateDisplay();
        }
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem saveItem = new JMenuItem("Save Game");
        saveItem.addActionListener(e -> saveGame());
        fileMenu.add(saveItem);
        
        JMenuItem loadItem = new JMenuItem("Load Game");
        loadItem.addActionListener(e -> loadGame());
        fileMenu.add(loadItem);
        
        fileMenu.addSeparator();
        
        JMenuItem logItem = new JMenuItem("Adventure Log");
        logItem.addActionListener(e -> showAdventureLog());
        fileMenu.add(logItem);
        
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }
    
    private void showAdventureLog() {
        new AdventureLogWindow(controller.getAdventureLog());
    }
    
    public GameController getController() {
        return controller;
    }
    
    public void setCurrentDicePanel(JPanel dicePanel) {
        this.currentDicePanel = dicePanel;
    }
    
    public void showRandomGotoButton(int targetChapter, String buttonText) {
        SwingUtilities.invokeLater(() -> {
            buttonPanel.removeAll();
            JButton gotoButton = new JButton(buttonText);
            gotoButton.addActionListener(e -> {
                chapterState.markRandomGotoExecuted(controller.getCurrentChapter().index);
                controller.goToChapter(targetChapter);
                updateDisplay();
            });
            buttonPanel.add(gotoButton);
            buttonPanel.revalidate();
            buttonPanel.repaint();
        });
    }
    
    private void saveGame() {
        JFileChooser fileChooser = new JFileChooser(SaveGameManager.getDefaultSaveDirectory());
        fileChooser.setDialogTitle("Save Game");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("FF Save Files (*.ffsave)", "ffsave"));
        
        // Set default filename: {game-title}_{timestamp}.ffsave
        String gameTitle = controller.getAdventure().title.replaceAll("[^a-zA-Z0-9]", "_");
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String defaultFilename = gameTitle + "_" + timestamp + ".ffsave";
        fileChooser.setSelectedFile(new File(SaveGameManager.getDefaultSaveDirectory(), defaultFilename));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".ffsave")) {
                file = new File(file.getAbsolutePath() + ".ffsave");
            }
            
            try {
                SaveGame saveGame = controller.createSaveGame();
                SaveGameManager.save(saveGame, file);
                JOptionPane.showMessageDialog(this, "Game saved successfully!", "Save Game", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving game: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser(SaveGameManager.getDefaultSaveDirectory());
        fileChooser.setDialogTitle("Load Game");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("FF Save Files (*.ffsave)", "ffsave"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                SaveGame saveGame = SaveGameManager.load(fileChooser.getSelectedFile());
                
                // Check if it's the same game
                if (!saveGame.getGameYamlPath().equals(controller.getGameYamlPath())) {
                    // Different game - create new window and close this one
                    SaveGameManager.loadAndStartGame(fileChooser.getSelectedFile());
                    dispose();
                } else {
                    // Same game - just load the save
                    controller.loadSaveGame(saveGame);
                    updateDisplay();
                    JOptionPane.showMessageDialog(this, "Game loaded successfully!", "Load Game", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading game: " + ex.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}


