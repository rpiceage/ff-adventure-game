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
    private JPanel statsPanel;
    private JPanel itemsPanel;
    private JLabel skillLabel;
    private JLabel staminaLabel;
    private JLabel luckLabel;
    private JLabel goldLabel;
    private GameController controller;
    private JWindow notificationWindow;
    private com.adventure.ui.BattleUI battleUI;
    private com.adventure.ui.LuckUI luckUI;
    private JScrollPane textScrollPane;
    private JPanel currentCenterPanel;

    public GameWindow(Adventure adventure) {
        this.controller = new GameController(adventure);
        setTitle(adventure.title);
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

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

        buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        updateDisplay();
        setVisible(true);
    }

    private void updateDisplay() {
        Hero hero = controller.getHero();
        skillLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.SKILL), hero.getSkill(), hero.getInitialSkill()));
        staminaLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.STAMINA), hero.getStamina(), hero.getInitialStamina()));
        luckLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.LUCK), hero.getLuck(), hero.getInitialLuck()));
        goldLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b></html>", 
            Messages.get(Messages.Key.GOLD), hero.getGold()));
        
        updateItemButtons();
        
        List<String> mods = hero.getLastModifications();
        if (!mods.isEmpty()) {
            showNotification(String.join("\n", mods));
            hero.clearModifications();
        }
        
        if (controller.isGameOver()) {
            textArea.setText(Messages.get(Messages.Key.GAME_OVER));
            buttonPanel.removeAll();
            battleUI = null;
            luckUI = null;
            
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
        } else if (luckUI != null) {
            // Luck test UI is already shown
        } else {
            textArea.setText(controller.getDisplayText());
            buttonPanel.removeAll();
            
            // Show buttons for all actions in the chapter
            for (Map<String, Object> actionData : controller.getCurrentChapter().actions) {
                com.adventure.actions.Action action = controller.getActionForData(actionData);
                
                if (action != null) {
                    if (action.getActionType() == com.adventure.actions.ActionType.MULTIPLE_BUTTONS) {
                        if (action instanceof com.adventure.actions.AddItemAction) {
                            // Handle AddItemAction
                            com.adventure.actions.AddItemAction addItemAction = (com.adventure.actions.AddItemAction) action;
                            List<com.adventure.actions.Action.Choice> choices = action.getChoices(actionData);
                            for (int i = 0; i < choices.size(); i++) {
                                JButton btn = new JButton(choices.get(i).text);
                                int itemIndex = i;
                                btn.addActionListener(e -> {
                                    addItemAction.addItem(controller, actionData, itemIndex);
                                    btn.setEnabled(false);
                                    updateItemButtons(); // Only update items panel, not full display
                                });
                                buttonPanel.add(btn);
                            }
                        } else {
                            // Handle GotoAction
                            List<com.adventure.actions.Action.Choice> choices = action.getChoices(actionData);
                            for (int i = 0; i < choices.size(); i++) {
                                JButton btn = new JButton(choices.get(i).text);
                                int choiceIndex = i;
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
                        actionButton.addActionListener(e -> handleSingleButtonAction(action, actionData));
                        buttonPanel.add(actionButton);
                    }
                }
            }
        }
        
        buttonPanel.revalidate();
        buttonPanel.repaint();
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
        skillLabel.setText(String.format("<html><div style='text-shadow: 2px 2px 4px black;'>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></div></html>", 
            Messages.get(Messages.Key.SKILL), hero.getSkill(), hero.getInitialSkill()));
        staminaLabel.setText(String.format("<html><div style='text-shadow: 2px 2px 4px black;'>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></div></html>", 
            Messages.get(Messages.Key.STAMINA), hero.getStamina(), hero.getInitialStamina()));
        luckLabel.setText(String.format("<html><div style='text-shadow: 2px 2px 4px black;'>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></div></html>", 
            Messages.get(Messages.Key.LUCK), hero.getLuck(), hero.getInitialLuck()));
        goldLabel.setText(String.format("<html><div style='text-shadow: 2px 2px 4px black;'>%s: <b><font color='red'>%d</font></b></div></html>", 
            Messages.get(Messages.Key.GOLD), hero.getGold()));
    }

    private void handleSingleButtonAction(com.adventure.actions.Action action, Map<String, Object> actionData) {
        if (actionData.containsKey("battle")) {
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
        } else if (actionData.containsKey("luck")) {
            luckUI = new com.adventure.ui.LuckUI(textArea, buttonPanel, controller, () -> {
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
        }
    }

    private void showNotification(String message) {
        if (notificationWindow != null) {
            notificationWindow.dispose();
        }
        
        notificationWindow = new JWindow(this);
        JLabel label = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
        label.setFont(new Font("Arial", Font.BOLD, 14));
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
        
        for (String item : controller.getHero().getInventory()) {
            JButton itemButton = new JButton(item);
            itemButton.setFont(new Font("Arial", Font.BOLD, 16));
            itemButton.setForeground(Color.WHITE);
            itemButton.setBackground(new Color(0, 0, 0, 100));
            itemButton.setOpaque(false);
            itemButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            itemButton.addActionListener(e -> showItemCantUsePopup());
            itemsPanel.add(itemButton);
        }
        
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private void showItemCantUsePopup() {
        JOptionPane.showMessageDialog(this, 
            Messages.get(Messages.Key.ITEM_CANT_USE), 
            Messages.get(Messages.Key.ITEMS_TITLE), 
            JOptionPane.INFORMATION_MESSAGE);
    }
}


