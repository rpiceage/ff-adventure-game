package com.adventure.ui;

import com.adventure.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BattleUI {
    private Battle currentBattle;
    private JPanel battleStatsPanel;
    private List<JRadioButton> enemyRadioButtons;
    private ButtonGroup enemyButtonGroup;
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
        List<Map<String, Object>> enemiesData = (List<Map<String, Object>>) battleData.get("enemies");
        
        List<Enemy> enemies = new ArrayList<>();
        for (Map<String, Object> enemyData : enemiesData) {
            String name = (String) enemyData.get("enemy");
            int skill = (Integer) enemyData.get("skill");
            int stamina = (Integer) enemyData.get("stamina");
            enemies.add(new Enemy(name, skill, stamina));
        }
        
        currentBattle = new Battle(controller.getHero(), enemies);
        
        centerPanel = new JPanel(new BorderLayout());
        battleStatsPanel = new JPanel();
        battleStatsPanel.setLayout(new BoxLayout(battleStatsPanel, BoxLayout.Y_AXIS));
        TitledBorder battleBorder = BorderFactory.createTitledBorder(Messages.get(Messages.Key.BATTLE_TITLE));
        battleBorder.setTitleFont(new Font("Arial", Font.BOLD, 24));
        battleStatsPanel.setBorder(battleBorder);
        
        enemyRadioButtons = new ArrayList<>();
        enemyButtonGroup = new ButtonGroup();
        
        for (int i = 0; i < enemies.size(); i++) {
            JRadioButton radioButton = new JRadioButton();
            radioButton.setFont(new Font("Arial", Font.PLAIN, 20));
            final int index = i;
            radioButton.addActionListener(e -> currentBattle.setSelectedEnemy(index));
            enemyRadioButtons.add(radioButton);
            enemyButtonGroup.add(radioButton);
            battleStatsPanel.add(radioButton);
        }
        
        if (!enemies.isEmpty()) {
            enemyRadioButtons.get(0).setSelected(true);
        }
        
        dicePanel = DiceAnimator.createDicePanel("src/resources/table.jpg");
        int dicePanelHeight = enemies.size() * 100; // 100px per enemy
        dicePanel.setPreferredSize(new Dimension(400, dicePanelHeight));
        
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
        
        List<Enemy> enemies = currentBattle.getEnemies();
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            JRadioButton radioButton = enemyRadioButtons.get(i);
            
            String text = String.format("%s %s: %d %s: %d", 
                enemy.getName(),
                Messages.get(Messages.Key.SKILL), enemy.getSkill(),
                Messages.get(Messages.Key.STAMINA), enemy.getStamina());
            
            if (i == currentBattle.getSelectedEnemyIndex()) {
                text = "<html><b>" + text + "</b></html>";
            }
            
            radioButton.setText(text);
            radioButton.setEnabled(enemy.isAlive());
            
            if (!enemy.isAlive() && radioButton.isSelected()) {
                for (int j = 0; j < enemies.size(); j++) {
                    if (enemies.get(j).isAlive()) {
                        enemyRadioButtons.get(j).setSelected(true);
                        currentBattle.setSelectedEnemy(j);
                        break;
                    }
                }
            }
        }
        
        textArea.setText(currentBattle.getBattleLog());
        buttonPanel.removeAll();
        
        if (currentBattle.isOver()) {
            if (currentBattle.heroWon()) {
                textArea.append("\n" + Messages.get(Messages.Key.BATTLE_VICTORY_ALL));
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
                textArea.append("\n" + Messages.get(Messages.Key.BATTLE_DEFEAT_GENERAL));
                currentBattle = null;
                onComplete.run();
            }
        } else {
            JButton nextTurnButton = new JButton(Messages.get(Messages.Key.BATTLE_NEXT_TURN));
            nextTurnButton.addActionListener(e -> {
                nextTurnButton.setEnabled(false);
                
                List<Enemy> aliveBeforeTurn = new ArrayList<>(currentBattle.getAliveEnemies());
                currentBattle.executeTurn();
                
                List<DiceAnimator.DiceGroup> groups = new ArrayList<>();
                for (Enemy enemy : aliveBeforeTurn) {
                    groups.add(new DiceAnimator.DiceGroup("Hero vs " + enemy.getName() + ":", 
                        enemy.getHeroDice1(), enemy.getHeroDice2(), 
                        enemy.getEnemyDice1(), enemy.getEnemyDice2()));
                }
                
                DiceAnimator.animateDice(dicePanel, groups.toArray(new DiceAnimator.DiceGroup[0]), () -> {
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
