package com.adventure;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameWindow extends JFrame {
    private JTextArea textArea;
    private JPanel buttonPanel;
    private JScrollPane buttonScrollPane;
    private JPanel statsPanel;
    private JPanel itemsPanel;
    private JLabel skillLabel;
    private JLabel staminaLabel;
    private JLabel luckLabel;
    private JLabel goldLabel;
    private JButton provisionsButton;
    private GameController controller;
    private JWindow notificationWindow;
    private com.adventure.ui.BattleUI battleUI;
    private com.adventure.ui.LuckUI luckUI;
    private com.adventure.ui.RandomModifyUI randomModifyUI;
    private com.adventure.ui.RandomGotoUI randomGotoUI;
    private com.adventure.ui.AttributeTestUI attributeTestUI;
    private JScrollPane textScrollPane;
    private JPanel currentCenterPanel;
    private JPanel currentDicePanel;
    private int prevSkill, prevStamina, prevLuck, prevGold, prevProvisions;
    private java.util.Set<Integer> chaptersWithExecutedRandomModify = new java.util.HashSet<>();
    private java.util.Set<Integer> chaptersWithExecutedRandomGoto = new java.util.HashSet<>();
    private int lastDisplayedChapter = -1;

    public GameWindow(Adventure adventure) {
        this(adventure, null);
    }
    
    public GameWindow(Adventure adventure, String gameYamlPath) {
        this.controller = new GameController(adventure, gameYamlPath);
        setTitle(adventure.title);
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create menu bar
        createMenuBar();

        try {
            InputStream bgStream = getClass().getClassLoader().getResourceAsStream("pergament.jpg");
            BufferedImage bgImage = ImageIO.read(bgStream);
            textArea = new JTextArea() {
                @Override
                protected void paintComponent(Graphics g) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                    super.paintComponent(g);
                }
            };
            textArea.setOpaque(false);
        } catch (Exception e) {
            textArea = new JTextArea();
        }
        
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.BOLD, 24));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textScrollPane = new JScrollPane(textArea);
        add(textScrollPane, BorderLayout.CENTER);

        try {
            java.io.InputStream imgStream = getClass().getClassLoader().getResourceAsStream("wall.jpg");
            java.awt.image.BufferedImage wallImage = javax.imageio.ImageIO.read(imgStream);
            statsPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(wallImage, 0, 0, getWidth(), getHeight(), this);
                }
            };
        } catch (Exception ex) {
            statsPanel = new JPanel();
        }
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        TitledBorder border = BorderFactory.createTitledBorder(Messages.get(Messages.Key.HERO_STATS_TITLE));
        border.setTitleFont(new Font("Arial", Font.BOLD, 24));
        border.setTitleColor(Color.WHITE);
        statsPanel.setBorder(border);
        statsPanel.setPreferredSize(new Dimension(300, 0));
        
        skillLabel = createStyledLabel();
        staminaLabel = createStyledLabel();
        luckLabel = createStyledLabel();
        goldLabel = createStyledLabel();
        statsPanel.add(skillLabel);
        statsPanel.add(staminaLabel);
        statsPanel.add(luckLabel);
        statsPanel.add(goldLabel);
        
        // Provisions button
        JButton provisionsButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        provisionsButton.setFont(new Font("Arial", Font.BOLD, 18));
        provisionsButton.setForeground(Color.WHITE);
        provisionsButton.setOpaque(false);
        provisionsButton.setContentAreaFilled(false);
        provisionsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        provisionsButton.addActionListener(e -> consumeProvision());
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(provisionsButton);
        
        // Store reference for updates
        this.provisionsButton = provisionsButton;
        
        // Items section
        JLabel itemsTitle = new JLabel(Messages.get(Messages.Key.ITEMS_TITLE) + ":") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        itemsTitle.setFont(new Font("Arial", Font.BOLD, 20));
        itemsTitle.setForeground(Color.WHITE);
        itemsTitle.setOpaque(false);
        statsPanel.add(Box.createVerticalStrut(20));
        statsPanel.add(itemsTitle);
        
        itemsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);
        statsPanel.add(itemsPanel);
        
        add(statsPanel, BorderLayout.EAST);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonScrollPane = new JScrollPane(buttonPanel);
        buttonScrollPane.setPreferredSize(new Dimension(0, 80));
        buttonScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        buttonScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Add listener to recalculate button panel height when viewport size changes
        buttonScrollPane.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                recalculateButtonPanelHeight();
            }
        });
        
        add(buttonScrollPane, BorderLayout.SOUTH);

        // Initialize previous values
        Hero hero = controller.getHero();
        prevSkill = hero.getSkill();
        prevStamina = hero.getStamina();
        prevLuck = hero.getLuck();
        prevGold = hero.getGold();
        prevProvisions = hero.getProvisions();

        updateDisplay();
        setVisible(true);
    }

    public void updateDisplay() {
        updateHeroStats();
        
        Hero hero = controller.getHero();
        updateItemButtons();
        
        List<String> mods = hero.getLastModifications();
        if (!mods.isEmpty()) {
            showNotification(String.join("\n", mods));
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
                BufferedImage skullImage = ImageIO.read(new File("src/resources/skull.jpg"));
                JLabel skullLabel = new JLabel(new ImageIcon(skullImage));
                remove(statsPanel);
                add(skullLabel, BorderLayout.EAST);
                revalidate();
                repaint();
            } catch (Exception ex) {
                // Keep stats panel if image fails to load
            }
        } else if (battleUI != null && battleUI.isActive()) {
            battleUI.updateDisplay();
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
            if (currentChapter != lastDisplayedChapter && currentDicePanel != null) {
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
            lastDisplayedChapter = currentChapter;
            
            // Show buttons for all actions in the chapter
            
            // Check if this chapter has an unexecuted randomModify or randomGoto
            boolean hasUnexecutedRandomModify = false;
            boolean hasUnexecutedRandomGoto = false;
            for (Map<String, Object> actionData : controller.getCurrentChapter().actions) {
                if (actionData.containsKey("randomModify") && 
                    !chaptersWithExecutedRandomModify.contains(controller.getCurrentChapter().index)) {
                    hasUnexecutedRandomModify = true;
                    break;
                }
                if (actionData.containsKey("randomGoto") && 
                    !chaptersWithExecutedRandomGoto.contains(controller.getCurrentChapter().index)) {
                    hasUnexecutedRandomGoto = true;
                    break;
                }
            }
            
            for (Map<String, Object> actionData : controller.getCurrentChapter().actions) {
                com.adventure.actions.Action action = controller.getActionForData(actionData);
                
                // Skip randomModify if already executed in this chapter
                if (actionData.containsKey("randomModify") && 
                    chaptersWithExecutedRandomModify.contains(controller.getCurrentChapter().index)) {
                    continue;
                }
                
                // Skip randomGoto if already executed in this chapter
                if (actionData.containsKey("randomGoto") && 
                    chaptersWithExecutedRandomGoto.contains(controller.getCurrentChapter().index)) {
                    continue;
                }
                
                // Skip other buttons if randomModify or randomGoto hasn't been executed yet
                if ((hasUnexecutedRandomModify && !actionData.containsKey("randomModify")) ||
                    (hasUnexecutedRandomGoto && !actionData.containsKey("randomGoto"))) {
                    continue;
                }
                
                if (action != null) {
                    if (action.getActionType() == com.adventure.actions.ActionType.MULTIPLE_BUTTONS) {
                        if (action instanceof com.adventure.actions.AddItemAction) {
                            // Handle AddItemAction
                            com.adventure.actions.AddItemAction addItemAction = (com.adventure.actions.AddItemAction) action;
                            List<com.adventure.actions.Action.Choice> choices = action.getChoices(actionData);
                            for (int i = 0; i < choices.size(); i++) {
                                JButton btn = new JButton(choices.get(i).text);
                                btn.setBackground(new Color(0, 100, 0)); // Dark green
                                btn.setForeground(Color.WHITE);
                                btn.setOpaque(true);
                                int itemIndex = i;
                                String itemName = choices.get(i).text.replace("Take ", "");
                                btn.addActionListener(e -> {
                                    addItemAction.addItem(controller, actionData, itemIndex);
                                    controller.getAdventureLog().log(String.format(Messages.get(Messages.Key.LOG_TOOK_ITEM), itemName));
                                    btn.setEnabled(false);
                                    updateItemButtons(); // Only update items panel, not full display
                                });
                                buttonPanel.add(btn);
                            }
                        } else if (action instanceof com.adventure.actions.CheckEventAction) {
                            // Handle CheckEventAction
                            com.adventure.actions.CheckEventAction checkAction = (com.adventure.actions.CheckEventAction) action;
                            List<com.adventure.actions.Action.Choice> choices = action.getChoices(actionData);
                            
                            if (checkAction.hasEventOrItem(controller, actionData)) {
                                // Show existing button
                                JButton btn = new JButton(choices.get(0).text);
                                btn.addActionListener(e -> {
                                    controller.getAdventureLog().log(String.format(Messages.get(Messages.Key.LOG_CHECK_EVENT_FOUND), checkAction.getExistingChapter(actionData)));
                                    controller.goToChapter(checkAction.getExistingChapter(actionData));
                                    updateDisplay();
                                });
                                buttonPanel.add(btn);
                            } else {
                                // Show missing button
                                JButton btn = new JButton(choices.get(1).text);
                                btn.addActionListener(e -> {
                                    controller.getAdventureLog().log(String.format(Messages.get(Messages.Key.LOG_CHECK_EVENT_NOT_FOUND), checkAction.getMissingChapter(actionData)));
                                    controller.goToChapter(checkAction.getMissingChapter(actionData));
                                    updateDisplay();
                                });
                                buttonPanel.add(btn);
                            }
                        } else if (action instanceof com.adventure.actions.CheckParameterAction) {
                            // Handle CheckParameterAction
                            com.adventure.actions.CheckParameterAction checkAction = (com.adventure.actions.CheckParameterAction) action;
                            List<com.adventure.actions.Action.Choice> choices = action.getChoices(actionData);
                            
                            if (checkAction.meetsThreshold(controller, actionData)) {
                                // Show greaterThanOrEquals button
                                JButton btn = new JButton(choices.get(0).text);
                                btn.addActionListener(e -> {
                                    controller.getAdventureLog().log(String.format(Messages.get(Messages.Key.LOG_CHECK_PARAMETER_PASSED), checkAction.getGreaterThanOrEqualsChapter(actionData)));
                                    controller.goToChapter(checkAction.getGreaterThanOrEqualsChapter(actionData));
                                    updateDisplay();
                                });
                                buttonPanel.add(btn);
                            } else {
                                // Show lessThan button
                                JButton btn = new JButton(choices.get(1).text);
                                btn.addActionListener(e -> {
                                    controller.getAdventureLog().log(String.format(Messages.get(Messages.Key.LOG_CHECK_PARAMETER_FAILED), checkAction.getLessThanChapter(actionData)));
                                    controller.goToChapter(checkAction.getLessThanChapter(actionData));
                                    updateDisplay();
                                });
                                buttonPanel.add(btn);
                            }
                        } else {
                            // Handle GotoAction
                            com.adventure.actions.GotoAction gotoAction = (com.adventure.actions.GotoAction) action;
                            List<com.adventure.actions.Action.Choice> choices = gotoAction.getChoices(controller, actionData);
                            for (int i = 0; i < choices.size(); i++) {
                                com.adventure.actions.Action.Choice choice = choices.get(i);
                                JButton btn = new JButton(choice.text);
                                btn.setEnabled(choice.enabled);
                                int choiceIndex = choice.index; // Use original index from Choice
                                Map<String, Object> gotoActionData = actionData;
                                btn.addActionListener(e -> {
                                    controller.selectChoice(choiceIndex, gotoActionData);
                                    updateDisplay();
                                });
                                buttonPanel.add(btn);
                            }
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

    private JLabel createStyledLabel() {
        JLabel label = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                super.paintComponent(g);
            }
        };
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setForeground(Color.WHITE);
        label.setOpaque(false);
        return label;
    }

    public void updateHeroStats() {
        Hero hero = controller.getHero();
        
        // Check for changes and animate
        if (hero.getSkill() != prevSkill) {
            animateLabel(skillLabel);
            prevSkill = hero.getSkill();
        }
        if (hero.getStamina() != prevStamina) {
            animateLabel(staminaLabel);
            prevStamina = hero.getStamina();
        }
        if (hero.getLuck() != prevLuck) {
            animateLabel(luckLabel);
            prevLuck = hero.getLuck();
        }
        if (hero.getGold() != prevGold) {
            animateLabel(goldLabel);
            prevGold = hero.getGold();
        }
        if (hero.getProvisions() != prevProvisions) {
            animateButton(provisionsButton);
            prevProvisions = hero.getProvisions();
        }
        
        skillLabel.setText(String.format("<html><div style='text-shadow: 2px 2px 4px black;'>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></div></html>", 
            Messages.get(Messages.Key.SKILL), hero.getSkill(), hero.getInitialSkill()));
        staminaLabel.setText(String.format("<html><div style='text-shadow: 2px 2px 4px black;'>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></div></html>", 
            Messages.get(Messages.Key.STAMINA), hero.getStamina(), hero.getInitialStamina()));
        luckLabel.setText(String.format("<html><div style='text-shadow: 2px 2px 4px black;'>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></div></html>", 
            Messages.get(Messages.Key.LUCK), hero.getLuck(), hero.getInitialLuck()));
        goldLabel.setText(String.format("<html><div style='text-shadow: 2px 2px 4px black;'>%s: <b><font color='red'>%d</font></b></div></html>", 
            Messages.get(Messages.Key.GOLD), hero.getGold()));
        
        // Update provisions button
        provisionsButton.setText(Messages.get(Messages.Key.PROVISIONS) + ": " + hero.getProvisions());
        provisionsButton.setEnabled(hero.getProvisions() > 0 && battleUI == null);
    }
    
    private void animateLabel(JLabel label) {
        Timer timer = new Timer(50, null);
        final int[] step = {0};
        final float[] sizes = {24f, 25f, 26f, 27f, 26f, 25f, 24f};
        timer.addActionListener(e -> {
            if (step[0] < sizes.length) {
                label.setFont(label.getFont().deriveFont(sizes[step[0]]));
                step[0]++;
            } else {
                label.setFont(label.getFont().deriveFont(24f));
                timer.stop();
            }
        });
        timer.start();
    }
    
    private void animateButton(JButton button) {
        Timer timer = new Timer(50, null);
        final int[] step = {0};
        final float[] sizes = {18f, 19f, 20f, 21f, 20f, 19f, 18f};
        timer.addActionListener(e -> {
            if (step[0] < sizes.length) {
                button.setFont(button.getFont().deriveFont(sizes[step[0]]));
                step[0]++;
            } else {
                button.setFont(button.getFont().deriveFont(18f));
                timer.stop();
            }
        });
        timer.start();
    }

    private void handleSingleButtonAction(com.adventure.actions.Action action, Map<String, Object> actionData) {
        if (actionData.containsKey("battle")) {
            controller.getAdventureLog().log("  Battle started");
            battleUI = new com.adventure.ui.BattleUI(textArea, buttonPanel, controller, this, () -> {
                battleUI = null;
                if (currentCenterPanel != null) {
                    remove(currentCenterPanel);
                    currentCenterPanel = null;
                }
                textScrollPane = new JScrollPane(textArea);
                add(textScrollPane, BorderLayout.CENTER);
                revalidate();
                repaint();
                updateDisplay();
            });
            JPanel battlePanel = battleUI.start(actionData);
            remove(textScrollPane);
            add(battlePanel, BorderLayout.CENTER);
            currentCenterPanel = battlePanel;
            revalidate();
            repaint();
        } else if (actionData.containsKey("randomModify")) {
            randomModifyUI = new com.adventure.ui.RandomModifyUI(controller, this, () -> {
                randomModifyUI = null;
                // Mark this chapter as having executed randomModify
                chaptersWithExecutedRandomModify.add(controller.getCurrentChapter().index);
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
                textScrollPane = new JScrollPane(textArea);
                add(textScrollPane, BorderLayout.CENTER);
                revalidate();
                repaint();
                updateDisplay();
            });
            JPanel luckPanel = luckUI.start(actionData);
            remove(textScrollPane);
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
                textScrollPane = new JScrollPane(textArea);
                add(textScrollPane, BorderLayout.CENTER);
                revalidate();
                repaint();
                updateDisplay();
            });
            JPanel testPanel = attributeTestUI.start(actionData);
            remove(textScrollPane);
            add(testPanel, BorderLayout.CENTER);
            currentCenterPanel = testPanel;
            revalidate();
            repaint();
        }
    }

    private void showNotification(String message) {
        if (notificationWindow != null) {
            notificationWindow.dispose();
        }
        
        notificationWindow = new JWindow(this);
        JLabel label = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.BLACK);
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        label.setBackground(new Color(255, 255, 200));
        label.setOpaque(true);
        notificationWindow.add(label);
        notificationWindow.pack();
        
        Point location = getLocation();
        Dimension size = getSize();
        Dimension notifSize = notificationWindow.getSize();
        notificationWindow.setLocation(
            location.x + 10,
            location.y + size.height - notifSize.height - 50
        );
        
        notificationWindow.setVisible(true);
        
        Timer timer = new Timer(3000, e -> {
            notificationWindow.dispose();
            notificationWindow = null;
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void updateItemButtons() {
        itemsPanel.removeAll();
        
        Map<String, Integer> useItemMap = getUseItemMap();
        
        for (String item : controller.getHero().getInventory()) {
            JButton itemButton = new JButton(item);
            itemButton.setFont(new Font("Arial", Font.BOLD, 16));
            itemButton.setForeground(Color.WHITE);
            itemButton.setBackground(new Color(0, 0, 0, 100));
            itemButton.setOpaque(false);
            itemButton.setContentAreaFilled(false);
            itemButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            if (useItemMap.containsKey(item)) {
                int targetChapter = useItemMap.get(item);
                itemButton.addActionListener(e -> {
                    controller.getAdventureLog().log(String.format(Messages.get(Messages.Key.LOG_USED_ITEM), item, targetChapter));
                    controller.goToChapter(targetChapter);
                    updateDisplay();
                });
            } else {
                itemButton.addActionListener(e -> showItemCantUsePopup());
            }
            
            itemsPanel.add(itemButton);
        }
        
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }
    
    private Map<String, Integer> getUseItemMap() {
        Map<String, Integer> map = new java.util.HashMap<>();
        for (Map<String, Object> actionData : controller.getCurrentChapter().actions) {
            if (actionData.containsKey("useItem")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) actionData.get("useItem");
                for (Map<String, Object> itemData : items) {
                    String itemName = (String) itemData.get("item");
                    int chapter = (Integer) itemData.get("chapter");
                    map.put(itemName, chapter);
                }
            }
        }
        return map;
    }

    private void showItemCantUsePopup() {
        JOptionPane.showMessageDialog(this, 
            Messages.get(Messages.Key.ITEM_CANT_USE), 
            Messages.get(Messages.Key.ITEMS_TITLE), 
            JOptionPane.INFORMATION_MESSAGE);
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
            updateHeroStats();
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
                chaptersWithExecutedRandomGoto.add(controller.getCurrentChapter().index);
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


