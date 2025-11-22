package com.adventure.ui;

import com.adventure.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
                
                dicePanel.removeAll();
                dicePanel.setLayout(new BoxLayout(dicePanel, BoxLayout.Y_AXIS));
                
                List<AnimatedDicePanel> animatedPanels = new ArrayList<>();
                
                for (Enemy enemy : aliveBeforeTurn) {
                    JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
                    rowPanel.setOpaque(false);
                    
                    JLabel heroLabel = new JLabel("Hero:");
                    heroLabel.setFont(new Font("Arial", Font.BOLD, 20));
                    rowPanel.add(heroLabel);
                    
                    AnimatedDicePanel heroDicePanel = new AnimatedDicePanel(
                        enemy.getHeroDice1(), enemy.getHeroDice2());
                    animatedPanels.add(heroDicePanel);
                    rowPanel.add(heroDicePanel);
                    
                    JLabel enemyLabel = new JLabel(enemy.getName() + ":");
                    enemyLabel.setFont(new Font("Arial", Font.BOLD, 20));
                    rowPanel.add(enemyLabel);
                    
                    AnimatedDicePanel enemyDicePanel = new AnimatedDicePanel(
                        enemy.getEnemyDice1(), enemy.getEnemyDice2());
                    animatedPanels.add(enemyDicePanel);
                    rowPanel.add(enemyDicePanel);
                    
                    dicePanel.add(rowPanel);
                }
                
                dicePanel.revalidate();
                dicePanel.repaint();
                
                Timer animTimer = new Timer(50, null);
                final int[] count = {0};
                animTimer.addActionListener(evt -> {
                    if (count[0] < 20) {
                        for (AnimatedDicePanel panel : animatedPanels) {
                            panel.updateAnimation();
                        }
                        count[0]++;
                    } else {
                        animTimer.stop();
                        for (AnimatedDicePanel panel : animatedPanels) {
                            panel.stopAnimation();
                        }
                        updateDisplay();
                        nextTurnButton.setEnabled(true);
                    }
                });
                animTimer.start();
            });
            buttonPanel.add(nextTurnButton);
        }
        
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }
    
    private class AnimatedDicePanel extends JPanel {
        private int[] finalValues;
        private int[] currentValues;
        private double[] rotations;
        private Random rand = new Random();
        private boolean animating = true;
        
        public AnimatedDicePanel(int... finalValues) {
            this.finalValues = finalValues;
            this.currentValues = new int[finalValues.length];
            this.rotations = new double[finalValues.length];
            for (int i = 0; i < finalValues.length; i++) {
                currentValues[i] = 1;
                rotations[i] = 0;
            }
            setPreferredSize(new Dimension(40 + finalValues.length * 80, 80));
            setOpaque(false);
        }
        
        public void updateAnimation() {
            for (int i = 0; i < currentValues.length; i++) {
                rotations[i] += Math.PI / (4 + i);
                currentValues[i] = rand.nextInt(6) + 1;
            }
            repaint();
        }
        
        public void stopAnimation() {
            animating = false;
            for (int i = 0; i < currentValues.length; i++) {
                rotations[i] = 0;
                currentValues[i] = finalValues[i];
            }
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 0; i < currentValues.length; i++) {
                int x = 40 + (i * 80);
                drawRotatedDice(g2d, x, 40, currentValues[i], rotations[i]);
            }
        }
        
        private void drawRotatedDice(Graphics2D g2d, int x, int y, int value, double angle) {
            Graphics2D g2 = (Graphics2D) g2d.create();
            g2.translate(x, y);
            g2.rotate(angle);
            
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(-25, -25, 50, 50, 10, 10);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(-25, -25, 50, 50, 10, 10);
            
            g2.setColor(Color.BLACK);
            int d = 8;
            if (value == 1) {
                g2.fillOval(-d/2, -d/2, d, d);
            } else if (value == 2) {
                g2.fillOval(-15, -15, d, d);
                g2.fillOval(7, 7, d, d);
            } else if (value == 3) {
                g2.fillOval(-15, -15, d, d);
                g2.fillOval(-d/2, -d/2, d, d);
                g2.fillOval(7, 7, d, d);
            } else if (value == 4) {
                g2.fillOval(-15, -15, d, d);
                g2.fillOval(7, -15, d, d);
                g2.fillOval(-15, 7, d, d);
                g2.fillOval(7, 7, d, d);
            } else if (value == 5) {
                g2.fillOval(-15, -15, d, d);
                g2.fillOval(7, -15, d, d);
                g2.fillOval(-d/2, -d/2, d, d);
                g2.fillOval(-15, 7, d, d);
                g2.fillOval(7, 7, d, d);
            } else if (value == 6) {
                g2.fillOval(-15, -15, d, d);
                g2.fillOval(7, -15, d, d);
                g2.fillOval(-15, -d/2, d, d);
                g2.fillOval(7, -d/2, d, d);
                g2.fillOval(-15, 7, d, d);
                g2.fillOval(7, 7, d, d);
            }
            g2.dispose();
        }
    }

    public boolean isActive() {
        return currentBattle != null;
    }
}
