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
    private GameController controller;
    private JWindow notificationWindow;
    private Battle currentBattle;
    private JPanel battleStatsPanel;
    private JLabel enemyStatsLabel;
    private JPanel centerPanel;
    private JPanel dicePanel;
    private boolean luckTestInProgress;
    private Map<String, Object> currentBattleActionData;

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
        add(new JScrollPane(textArea), BorderLayout.CENTER);

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
        skillLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.SKILL), hero.getSkill(), hero.getInitialSkill()));
        staminaLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.STAMINA), hero.getStamina(), hero.getInitialStamina()));
        luckLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.LUCK), hero.getLuck(), hero.getInitialLuck()));
        
        List<String> mods = hero.getLastModifications();
        if (!mods.isEmpty()) {
            showNotification(String.join("\n", mods));
            hero.clearModifications();
        }
        
        if (controller.isGameOver()) {
            textArea.setText(Messages.get(Messages.Key.GAME_OVER));
            buttonPanel.removeAll();
            currentBattle = null;
            
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
        } else if (currentBattle != null) {
            updateBattleDisplay();
        } else if (luckTestInProgress) {
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

    private void handleSingleButtonAction(com.adventure.actions.Action action, Map<String, Object> actionData) {
        if (actionData.containsKey("battle")) {
            startBattle(actionData);
        } else if (actionData.containsKey("luck")) {
            startLuckTest(actionData);
        }
    }

    private void startBattle(Map<String, Object> battleAction) {
        currentBattleActionData = battleAction;
        Map<String, Object> battleData = (Map<String, Object>) battleAction.get("battle");
        List<Map<String, Object>> enemies = (List<Map<String, Object>>) battleData.get("enemies");
        Map<String, Object> enemyData = enemies.get(0);
        
        String enemyName = (String) enemyData.get("enemy");
        int enemySkill = (Integer) enemyData.get("skill");
        int enemyStamina = (Integer) enemyData.get("stamina");
        
        currentBattle = new Battle(controller.getHero(), enemyName, enemySkill, enemyStamina);
        
        centerPanel = new JPanel(new BorderLayout());
        battleStatsPanel = new JPanel();
        battleStatsPanel.setLayout(new BoxLayout(battleStatsPanel, BoxLayout.Y_AXIS));
        TitledBorder battleBorder = BorderFactory.createTitledBorder(Messages.get(Messages.Key.BATTLE_TITLE));
        battleBorder.setTitleFont(new Font("Arial", Font.BOLD, 24));
        battleStatsPanel.setBorder(battleBorder);
        enemyStatsLabel = new JLabel();
        enemyStatsLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        battleStatsPanel.add(enemyStatsLabel);
        
        dicePanel = DiceAnimator.createDicePanel("src/resources/table.jpg");
        dicePanel.setPreferredSize(new Dimension(400, 150));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(battleStatsPanel, BorderLayout.NORTH);
        topPanel.add(dicePanel, BorderLayout.CENTER);
        
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        getContentPane().remove(0); // Remove old scroll pane
        add(centerPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        
        updateBattleDisplay();
    }

    private void startLuckTest(Map<String, Object> luckAction) {
        luckTestInProgress = true;
        Map<String, Object> luckData = (Map<String, Object>) luckAction.get("luck");
        int luckyChapter = (Integer) luckData.get("lucky");
        int unluckyChapter = (Integer) luckData.get("unlucky");
        
        centerPanel = new JPanel(new BorderLayout());
        
        dicePanel = DiceAnimator.createDicePanel("src/resources/table.jpg");
        dicePanel.setPreferredSize(new Dimension(400, 150));
        
        centerPanel.add(dicePanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        getContentPane().remove(0);
        add(centerPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        
        textArea.setText(Messages.get(Messages.Key.LUCK_TEST_TITLE));
        buttonPanel.removeAll();
        
        JButton testButton = new JButton(Messages.get(Messages.Key.LUCK_TEST_BUTTON));
        testButton.addActionListener(e -> {
            testButton.setEnabled(false);
            
            Random rand = new Random();
            int dice1 = rand.nextInt(6) + 1;
            int dice2 = rand.nextInt(6) + 1;
            int total = dice1 + dice2;
            int heroLuck = controller.getHero().getLuck();
            boolean lucky = total <= heroLuck;
            
            DiceAnimator.DiceGroup[] groups = {
                new DiceAnimator.DiceGroup("", dice1, dice2)
            };
            
            DiceAnimator.animateDice(dicePanel, groups, () -> {
                textArea.setText(lucky ? Messages.get(Messages.Key.LUCK_LUCKY) : Messages.get(Messages.Key.LUCK_UNLUCKY));
                
                buttonPanel.removeAll();
                JButton continueButton = new JButton(Messages.get(Messages.Key.LUCK_CONTINUE));
                continueButton.addActionListener(ev -> {
                    luckTestInProgress = false;
                    remove(centerPanel);
                    centerPanel = null;
                    add(new JScrollPane(textArea), BorderLayout.CENTER);
                    revalidate();
                    repaint();
                    controller.goToChapter(lucky ? luckyChapter : unluckyChapter);
                    updateDisplay();
                });
                buttonPanel.add(continueButton);
                buttonPanel.revalidate();
                buttonPanel.repaint();
            });
        });
        buttonPanel.add(testButton);
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void updateBattleDisplay() {
        Hero hero = controller.getHero();
        skillLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.SKILL), hero.getSkill(), hero.getInitialSkill()));
        staminaLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.STAMINA), hero.getStamina(), hero.getInitialStamina()));
        luckLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.LUCK), hero.getLuck(), hero.getInitialLuck()));
        
        int enemyStam = currentBattle.getEnemyStamina();
        enemyStatsLabel.setText(String.format("%s %s: %d %s: %d", 
            currentBattle.getEnemyName(), 
            Messages.get(Messages.Key.SKILL), currentBattle.getEnemySkill(),
            Messages.get(Messages.Key.STAMINA), enemyStam));
        
        textArea.setText(currentBattle.getBattleLog());
        buttonPanel.removeAll();
        
        if (currentBattle.isOver()) {
            if (currentBattle.heroWon()) {
                textArea.append("\n" + Messages.get(Messages.Key.BATTLE_VICTORY) + " " + currentBattle.getEnemyName() + "!");
                Map<String, Object> battleData = (Map<String, Object>) currentBattleActionData.get("battle");
                int winChapter = (Integer) battleData.get("win");
                
                JButton continueButton = new JButton(Messages.get(Messages.Key.BATTLE_CLOSE));
                continueButton.addActionListener(e -> {
                    currentBattle = null;
                    battleStatsPanel = null;
                    remove(centerPanel);
                    centerPanel = null;
                    add(new JScrollPane(textArea), BorderLayout.CENTER);
                    revalidate();
                    repaint();
                    controller.goToChapter(winChapter);
                    updateDisplay();
                });
                buttonPanel.add(continueButton);
            } else {
                textArea.append("\n" + String.format(Messages.get(Messages.Key.BATTLE_DEFEAT), currentBattle.getEnemyName()));
                currentBattle = null;
                battleStatsPanel = null;
                remove(centerPanel);
                centerPanel = null;
                add(new JScrollPane(textArea), BorderLayout.CENTER);
                revalidate();
                repaint();
                updateDisplay();
            }
        } else {
            JButton nextTurnButton = new JButton(Messages.get(Messages.Key.BATTLE_NEXT_TURN));
            nextTurnButton.addActionListener(e -> {
                nextTurnButton.setEnabled(false);
                currentBattle.executeTurn();
                
                DiceAnimator.DiceGroup[] groups = {
                    new DiceAnimator.DiceGroup("Hero:", currentBattle.getLastHeroDice1(), currentBattle.getLastHeroDice2()),
                    new DiceAnimator.DiceGroup(currentBattle.getEnemyName() + ":", currentBattle.getLastEnemyDice1(), currentBattle.getLastEnemyDice2())
                };
                
                DiceAnimator.animateDice(dicePanel, groups, () -> {
                    updateBattleDisplay();
                    nextTurnButton.setEnabled(true);
                });
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
