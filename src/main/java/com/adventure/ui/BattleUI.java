package com.adventure.ui;

import com.adventure.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class BattleUI {
    private Battle currentBattle;
    private JPanel battleStatsPanel;
    private JLabel enemyStatsLabel;
    private JPanel centerPanel;
    private JPanel dicePanel;
    private Map<String, Object> battleActionData;
    private JTextArea textArea;
    private JPanel buttonPanel;
    private GameController controller;
    private GameWindow gameWindow;
    private Runnable onComplete;

    public BattleUI(JTextArea textArea, JPanel buttonPanel, GameController controller, GameWindow gameWindow, Runnable onComplete) {
        this.textArea = textArea;
        this.buttonPanel = buttonPanel;
        this.controller = controller;
        this.gameWindow = gameWindow;
        this.onComplete = onComplete;
    }

    public JPanel start(Map<String, Object> battleAction) {
        this.battleActionData = battleAction;
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
        
        updateDisplay();
        return centerPanel;
    }

    public void updateDisplay() {
        gameWindow.updateHeroStats();
        
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
                Map<String, Object> battleData = (Map<String, Object>) battleActionData.get("battle");
                int winChapter = (Integer) battleData.get("win");
                
                JButton continueButton = new JButton(Messages.get(Messages.Key.BATTLE_CLOSE));
                continueButton.addActionListener(e -> {
                    currentBattle = null;
                    controller.goToChapter(winChapter);
                    onComplete.run();
                });
                buttonPanel.add(continueButton);
            } else {
                textArea.append("\n" + String.format(Messages.get(Messages.Key.BATTLE_DEFEAT), currentBattle.getEnemyName()));
                currentBattle = null;
                onComplete.run();
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
                    updateDisplay();
                    nextTurnButton.setEnabled(true);
                });
            });
            buttonPanel.add(nextTurnButton);
        }
        
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    public boolean isActive() {
        return currentBattle != null;
    }
}
