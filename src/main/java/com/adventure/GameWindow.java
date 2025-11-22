package com.adventure;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameWindow extends JFrame {
    private JTextArea textArea;
    private JPanel buttonPanel;
    private JPanel statsPanel;
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
            BufferedImage bgImage = ImageIO.read(new File("src/resources/pergament.jpg"));
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

        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        TitledBorder border = BorderFactory.createTitledBorder(Messages.get(Messages.Key.HERO_STATS_TITLE));
        border.setTitleFont(new Font("Arial", Font.BOLD, 24));
        statsPanel.setBorder(border);
        statsPanel.setPreferredSize(new Dimension(300, 0));
        skillLabel = new JLabel();
        skillLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        staminaLabel = new JLabel();
        staminaLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        luckLabel = new JLabel();
        luckLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        goldLabel = new JLabel();
        goldLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        statsPanel.add(skillLabel);
        statsPanel.add(staminaLabel);
        statsPanel.add(luckLabel);
        statsPanel.add(goldLabel);
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
            
            com.adventure.actions.Action action = controller.getCurrentAction();
            Map<String, Object> actionData = controller.getCurrentActionData();
            
            if (action != null && actionData != null) {
                if (action.getActionType() == com.adventure.actions.ActionType.MULTIPLE_BUTTONS) {
                    List<Map<String, Object>> choices = action.getChoices(actionData);
                    for (int i = 0; i < choices.size(); i++) {
                        JButton btn = new JButton(choices.get(i).get("text").toString());
                        int choiceIndex = i;
                        btn.addActionListener(e -> {
                            controller.selectChoice(choiceIndex);
                            updateDisplay();
                        });
                        buttonPanel.add(btn);
                    }
                } else if (action.getActionType() == com.adventure.actions.ActionType.SINGLE_BUTTON) {
                    JButton actionButton = new JButton(action.getButtonText());
                    actionButton.addActionListener(e -> handleSingleButtonAction(action, actionData));
                    buttonPanel.add(actionButton);
                }
            }
        }
        
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    public void updateHeroStats() {
        Hero hero = controller.getHero();
        skillLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.SKILL), hero.getSkill(), hero.getInitialSkill()));
        staminaLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.STAMINA), hero.getStamina(), hero.getInitialStamina()));
        luckLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.LUCK), hero.getLuck(), hero.getInitialLuck()));
        goldLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b></html>", 
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
                add(new JScrollPane(textArea), BorderLayout.CENTER);
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
                add(new JScrollPane(textArea), BorderLayout.CENTER);
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
}


