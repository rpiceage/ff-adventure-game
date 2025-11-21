package com.adventure;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;

public class GameWindow extends JFrame {
    private JTextArea textArea;
    private JPanel buttonPanel;
    private JPanel statsPanel;
    private JLabel skillLabel;
    private JLabel staminaLabel;
    private JLabel luckLabel;
    private GameController controller;
    private JWindow notificationWindow;
    private Battle currentBattle;
    private JPanel battleStatsPanel;
    private JLabel enemyStatsLabel;

    public GameWindow(Adventure adventure) {
        this.controller = new GameController(adventure);
        setTitle(adventure.title);
        setSize(700, 400);
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
        textArea.setFont(new Font("Arial", Font.BOLD, 16));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createTitledBorder(Messages.get(Messages.Key.HERO_STATS_TITLE)));
        skillLabel = new JLabel();
        staminaLabel = new JLabel();
        luckLabel = new JLabel();
        statsPanel.add(skillLabel);
        statsPanel.add(staminaLabel);
        statsPanel.add(luckLabel);
        add(statsPanel, BorderLayout.EAST);

        buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        updateDisplay();
        setVisible(true);
    }

    private void updateDisplay() {
        Hero hero = controller.getHero();
        skillLabel.setText(Messages.get(Messages.Key.SKILL) + ": " + hero.getSkill());
        staminaLabel.setText(Messages.get(Messages.Key.STAMINA) + ": " + hero.getStamina());
        luckLabel.setText(Messages.get(Messages.Key.LUCK) + ": " + hero.getLuck());
        
        List<String> mods = hero.getLastModifications();
        if (!mods.isEmpty()) {
            showNotification(String.join("\n", mods));
            hero.clearModifications();
        }
        
        if (controller.isGameOver()) {
            textArea.setText(Messages.get(Messages.Key.GAME_OVER));
            buttonPanel.removeAll();
            currentBattle = null;
        } else if (currentBattle != null) {
            updateBattleDisplay();
        } else {
            textArea.setText(controller.getDisplayText());
            buttonPanel.removeAll();
            
            Map<String, Object> battleAction = controller.getBattleAction();
            if (battleAction != null) {
                JButton battleButton = new JButton(Messages.get(Messages.Key.BATTLE_BEGIN));
                battleButton.addActionListener(e -> startBattle(battleAction));
                buttonPanel.add(battleButton);
            } else {
                List<Map<String, Object>> choices = controller.getChoices();
                for (int i = 0; i < choices.size(); i++) {
                    JButton btn = new JButton(choices.get(i).get("text").toString());
                    int choiceIndex = i;
                    btn.addActionListener(e -> {
                        controller.selectChoice(choiceIndex);
                        updateDisplay();
                    });
                    buttonPanel.add(btn);
                }
            }
        }
        
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void startBattle(Map<String, Object> battleAction) {
        Map<String, Object> battleData = (Map<String, Object>) battleAction.get("battle");
        List<Map<String, Object>> enemies = (List<Map<String, Object>>) battleData.get("enemies");
        Map<String, Object> enemyData = enemies.get(0);
        
        String enemyName = (String) enemyData.get("enemy");
        int enemySkill = (Integer) enemyData.get("skill");
        int enemyStamina = (Integer) enemyData.get("stamina");
        
        currentBattle = new Battle(controller.getHero(), enemyName, enemySkill, enemyStamina);
        
        battleStatsPanel = new JPanel();
        battleStatsPanel.setLayout(new BoxLayout(battleStatsPanel, BoxLayout.Y_AXIS));
        battleStatsPanel.setBorder(BorderFactory.createTitledBorder(Messages.get(Messages.Key.BATTLE_TITLE)));
        enemyStatsLabel = new JLabel();
        battleStatsPanel.add(enemyStatsLabel);
        add(battleStatsPanel, BorderLayout.NORTH);
        revalidate();
        
        updateBattleDisplay();
    }

    private void updateBattleDisplay() {
        Hero hero = controller.getHero();
        skillLabel.setText(Messages.get(Messages.Key.SKILL) + ": " + hero.getSkill());
        staminaLabel.setText(Messages.get(Messages.Key.STAMINA) + ": " + hero.getStamina());
        luckLabel.setText(Messages.get(Messages.Key.LUCK) + ": " + hero.getLuck());
        
        int enemyStam = currentBattle.getEnemyStamina();
        System.out.println("Enemy stamina: " + enemyStam);
        enemyStatsLabel.setText(String.format("%s %s: %d %s: %d", 
            currentBattle.getEnemyName(), 
            Messages.get(Messages.Key.SKILL), currentBattle.getEnemySkill(),
            Messages.get(Messages.Key.STAMINA), enemyStam));
        
        textArea.setText(currentBattle.getBattleLog());
        buttonPanel.removeAll();
        
        if (currentBattle.isOver()) {
            if (currentBattle.heroWon()) {
                textArea.append("\n" + Messages.get(Messages.Key.BATTLE_VICTORY) + " " + currentBattle.getEnemyName() + "!");
                Map<String, Object> battleAction = controller.getBattleAction();
                Map<String, Object> battleData = (Map<String, Object>) battleAction.get("battle");
                int winChapter = (Integer) battleData.get("win");
                
                JButton continueButton = new JButton(Messages.get(Messages.Key.BATTLE_CLOSE));
                continueButton.addActionListener(e -> {
                    currentBattle = null;
                    remove(battleStatsPanel);
                    battleStatsPanel = null;
                    controller.goToChapter(winChapter);
                    updateDisplay();
                });
                buttonPanel.add(continueButton);
            } else {
                textArea.append("\n" + String.format(Messages.get(Messages.Key.BATTLE_DEFEAT), currentBattle.getEnemyName()));
                currentBattle = null;
                remove(battleStatsPanel);
                battleStatsPanel = null;
                updateDisplay();
            }
        } else {
            JButton nextTurnButton = new JButton(Messages.get(Messages.Key.BATTLE_NEXT_TURN));
            nextTurnButton.addActionListener(e -> {
                currentBattle.executeTurn();
                updateBattleDisplay();
            });
            buttonPanel.add(nextTurnButton);
        }
        
        buttonPanel.revalidate();
        buttonPanel.repaint();
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
