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
            Enemy enemy = new Enemy(name, skill, stamina);
            
            // Set retreat threshold if present
            if (enemyData.containsKey("retreat")) {
                Map<String, Object> retreatData = (Map<String, Object>) enemyData.get("retreat");
                if (retreatData.containsKey("stamina")) {
                    enemy.setRetreatThreshold((Integer) retreatData.get("stamina"));
                }
            }
            
            enemies.add(enemy);
        }
        
        int mode = battleData.containsKey("mode") ? (Integer) battleData.get("mode") : 0;
        currentBattle = new Battle(controller.getHero(), enemies, new java.util.Random(), mode);
        
        // Log battle start
        StringBuilder enemyNames = new StringBuilder();
        for (int i = 0; i < enemies.size(); i++) {
            if (i > 0) enemyNames.append(", ");
            enemyNames.append(enemies.get(i).getName());
        }
        controller.getAdventureLog().log(String.format(Messages.get(Messages.Key.LOG_BATTLE_STARTED), enemyNames));
        
        // Set interrupt if present
        if (battleData.containsKey("interrupt")) {
            Map<String, Object> interruptData = (Map<String, Object>) battleData.get("interrupt");
            if (interruptData.containsKey("stamina")) {
                currentBattle.setInterrupt(new StaminaInterrupt((Integer) interruptData.get("stamina")));
            } else if (interruptData.containsKey("turn")) {
                int turn = (Integer) interruptData.get("turn");
                Integer page = interruptData.containsKey("page") ? (Integer) interruptData.get("page") : null;
                currentBattle.setInterrupt(new TurnInterrupt(turn, page));
            } else if (interruptData.containsKey("turnWon")) {
                currentBattle.setInterrupt(new TurnWonInterrupt((Integer) interruptData.get("turnWon")));
            } else if (interruptData.containsKey("turnLost")) {
                int turns = (Integer) interruptData.get("turnLost");
                int chapter = (Integer) interruptData.get("page");
                currentBattle.setInterrupt(new TurnLostInterrupt(turns, chapter));
            } else if (interruptData.containsKey("enemiesKilled")) {
                currentBattle.setInterrupt(new EnemiesKilledInterrupt((Integer) interruptData.get("enemiesKilled")));
            } else if (interruptData.containsKey("heroStamina")) {
                int threshold = (Integer) interruptData.get("heroStamina");
                int chapter = (Integer) interruptData.get("page");
                currentBattle.setInterrupt(new HeroStaminaInterrupt(threshold, chapter));
            } else if (interruptData.containsKey("enemyKilled") && interruptData.containsKey("enemyDamaged")) {
                String enemyToKill = (String) interruptData.get("enemyKilled");
                String enemyToDamage = (String) interruptData.get("enemyDamaged");
                int damageThreshold = (Integer) interruptData.get("enemyDamagedStamina");
                currentBattle.setInterrupt(new CombinedEnemyInterrupt(enemyToKill, enemyToDamage, damageThreshold));
            } else if (interruptData.containsKey("perEnemy")) {
                Map<String, Integer> enemyThresholds = (Map<String, Integer>) interruptData.get("perEnemy");
                currentBattle.setInterrupt(new PerEnemyStaminaInterrupt(enemyThresholds));
            } else if (interruptData.containsKey("everyTurnWon") && (Boolean) interruptData.get("everyTurnWon")) {
                int dice = (Integer) interruptData.get("dice");
                List<Integer> triggers = (List<Integer>) interruptData.get("trigger");
                int chapter = (Integer) interruptData.get("page");
                currentBattle.setInterrupt(new ConditionalInterrupt(dice, triggers, chapter, new java.util.Random()));
            } else if (interruptData.containsKey("everyTurnLost") && (Boolean) interruptData.get("everyTurnLost")) {
                int dice = (Integer) interruptData.get("dice");
                List<Integer> triggers = (List<Integer>) interruptData.get("trigger");
                int chapter = (Integer) interruptData.get("page");
                currentBattle.setInterrupt(new EveryTurnLostInterrupt(dice, triggers, chapter, new java.util.Random()));
            }
        }
        
        // Set escape turn if present
        if (battleData.containsKey("escape")) {
            Map<String, Object> escapeData = (Map<String, Object>) battleData.get("escape");
            if (escapeData.containsKey("turn")) {
                currentBattle.setEscapeTurn((Integer) escapeData.get("turn"));
            }
        }
        
        // Set modifier if present
        if (battleData.containsKey("modifier")) {
            Map<String, Object> modifierData = (Map<String, Object>) battleData.get("modifier");
            int value = (Integer) modifierData.get("value");
            String text = (String) modifierData.get("text");
            currentBattle.setModifier(value, text);
        }
        
        // Apply effect from previous action if present
        if (controller.getNextBattleAttackModifier() != null) {
            currentBattle.setModifier(controller.getNextBattleAttackModifier(), controller.getNextBattleEffectText());
            controller.clearNextBattleEffect();
        }
        
        // Set extra damage if present
        if (battleData.containsKey("extraHeroDamage")) {
            Map<String, Object> extraDamageData = (Map<String, Object>) battleData.get("extraHeroDamage");
            
            // Check for simple attribute damage format (skill, stamina, luck)
            if (extraDamageData.containsKey("skill") || extraDamageData.containsKey("stamina") || extraDamageData.containsKey("luck")) {
                Integer skillDamage = extraDamageData.containsKey("skill") ? (Integer) extraDamageData.get("skill") : null;
                Integer staminaDamage = extraDamageData.containsKey("stamina") ? (Integer) extraDamageData.get("stamina") : null;
                Integer luckDamage = extraDamageData.containsKey("luck") ? (Integer) extraDamageData.get("luck") : null;
                currentBattle.setExtraAttributeDamage(skillDamage, staminaDamage, luckDamage);
            }
            // Check for random extra damage format
            else if (extraDamageData.containsKey("randomExtraDamage")) {
                Map<String, Object> randomData = (Map<String, Object>) extraDamageData.get("randomExtraDamage");
                int dice = (Integer) randomData.get("dice");
                List<Map<String, Object>> damageList = (List<Map<String, Object>>) randomData.get("damage");
                Map<String, Object> damageInfo = damageList.get(0);
                int damageAmount = (Integer) damageInfo.get("stamina");
                List<Integer> triggers = (List<Integer>) damageInfo.get("trigger");
                currentBattle.setExtraDamage(dice, triggers, damageAmount);
            }
        }
        
        // Set ally if present
        if (battleData.containsKey("ally")) {
            Map<String, Object> allyData = (Map<String, Object>) battleData.get("ally");
            String allyName = (String) allyData.get("name");
            int allySkill = (Integer) allyData.get("skill");
            int allyStamina = (Integer) allyData.get("stamina");
            currentBattle.setAlly(allyName, allySkill, allyStamina);
        }
        
        centerPanel = new JPanel(new BorderLayout());
        battleStatsPanel = new JPanel();
        battleStatsPanel.setLayout(new BoxLayout(battleStatsPanel, BoxLayout.Y_AXIS));
        TitledBorder battleBorder = BorderFactory.createTitledBorder(Messages.get(Messages.Key.BATTLE_TITLE));
        battleBorder.setTitleFont(new Font("Arial", Font.BOLD, 24));
        battleStatsPanel.setBorder(battleBorder);
        
        enemyRadioButtons = new ArrayList<>();
        enemyButtonGroup = new ButtonGroup();
        
        if (mode == 1) {
            // Sequential mode: just show labels, no radio buttons
            for (int i = 0; i < enemies.size(); i++) {
                JLabel label = new JLabel();
                label.setFont(new Font("Arial", Font.PLAIN, 20));
                label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                battleStatsPanel.add(label);
                // Create dummy radio button for compatibility with updateDisplay
                JRadioButton dummyButton = new JRadioButton();
                dummyButton.setVisible(false);
                enemyRadioButtons.add(dummyButton);
            }
        } else {
            // Simultaneous mode: show radio buttons for target selection
            for (int i = 0; i < enemies.size(); i++) {
                JRadioButton radioButton = new JRadioButton();
                radioButton.setFont(new Font("Arial", Font.PLAIN, 20));
                radioButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                final int index = i;
                radioButton.addActionListener(e -> currentBattle.setSelectedEnemy(index));
                enemyRadioButtons.add(radioButton);
                enemyButtonGroup.add(radioButton);
                battleStatsPanel.add(radioButton);
            }
            
            if (!enemies.isEmpty()) {
                enemyRadioButtons.get(0).setSelected(true);
            }
        }
        
        dicePanel = DiceAnimator.createDicePanel("src/resources/table.jpg");
        // In sequential mode, only show one enemy at a time
        int dicePanelHeight = (mode == 1 ? 1 : enemies.size()) * 100; // 100px per enemy
        dicePanel.setPreferredSize(new Dimension(400, dicePanelHeight));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(battleStatsPanel, BorderLayout.NORTH);
        topPanel.add(dicePanel, BorderLayout.CENTER);
        
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        // Add battleText to battle log if present
        if (battleData.containsKey("battleText")) {
            currentBattle.appendToBattleLog((String) battleData.get("battleText") + "\n\n");
        }
        
        // Add modifier text to battle log if present
        if (currentBattle.getModifierText() != null) {
            currentBattle.appendToBattleLog(currentBattle.getModifierText() + "\n\n");
        }
        
        // Add ally stats to battle log if present
        if (currentBattle.getAlly() != null) {
            Enemy ally = currentBattle.getAlly();
            String allyStats = String.format("%s (Ally) - %s: %d, %s: %d\n\n",
                ally.getName(),
                Messages.get(Messages.Key.SKILL), ally.getSkill(),
                Messages.get(Messages.Key.STAMINA), ally.getStamina());
            currentBattle.appendToBattleLog(allyStats);
        }
        
        updateDisplay();
        return centerPanel;
    }
    
    public JPanel resume(Battle savedBattle, Map<String, Object> savedActionData) {
        this.currentBattle = savedBattle;
        this.battleActionData = savedActionData;
        
        // Recreate the battle UI with saved state
        centerPanel = new JPanel(new BorderLayout());
        
        // Create battle stats panel
        battleStatsPanel = new JPanel();
        battleStatsPanel.setLayout(new BoxLayout(battleStatsPanel, BoxLayout.Y_AXIS));
        TitledBorder border = BorderFactory.createTitledBorder(Messages.get(Messages.Key.BATTLE_TITLE));
        border.setTitleColor(Color.WHITE);
        battleStatsPanel.setBorder(border);
        battleStatsPanel.setOpaque(false);
        
        // Recreate enemy display matching start() method
        enemyRadioButtons = new ArrayList<>();
        enemyButtonGroup = new ButtonGroup();
        List<Enemy> enemies = currentBattle.getEnemies();
        int mode = currentBattle.getMode();
        
        if (mode == 1) {
            // Sequential mode: just show labels, no radio buttons
            for (int i = 0; i < enemies.size(); i++) {
                JLabel label = new JLabel();
                label.setFont(new Font("Arial", Font.PLAIN, 20));
                label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                battleStatsPanel.add(label);
                // Create dummy radio button for compatibility with updateDisplay
                JRadioButton dummyButton = new JRadioButton();
                dummyButton.setVisible(false);
                enemyRadioButtons.add(dummyButton);
            }
        } else {
            // Simultaneous mode: show radio buttons for target selection
            for (int i = 0; i < enemies.size(); i++) {
                JRadioButton radioButton = new JRadioButton();
                radioButton.setFont(new Font("Arial", Font.PLAIN, 20));
                radioButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                final int index = i;
                radioButton.addActionListener(e -> currentBattle.setSelectedEnemy(index));
                enemyRadioButtons.add(radioButton);
                enemyButtonGroup.add(radioButton);
                battleStatsPanel.add(radioButton);
            }
            
            // Restore selected enemy
            int selectedIndex = currentBattle.getSelectedEnemyIndex();
            if (selectedIndex >= 0 && selectedIndex < enemyRadioButtons.size()) {
                enemyRadioButtons.get(selectedIndex).setSelected(true);
            }
        }
        
        // Create dice panel with background
        dicePanel = DiceAnimator.createDicePanel("src/resources/table.jpg");
        int dicePanelHeight = (mode == 1 ? 1 : enemies.size()) * 100;
        dicePanel.setPreferredSize(new Dimension(400, dicePanelHeight));
        
        // Create top panel with battle stats and dice
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(battleStatsPanel, BorderLayout.NORTH);
        topPanel.add(dicePanel, BorderLayout.CENTER);
        
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        // Add "Battle resumed!" message to battle log
        currentBattle.appendToBattleLog(Messages.get(Messages.Key.BATTLE_RESUMED) + "\n\n");
        
        // Restore battle log in text area
        textArea.setText(currentBattle.getBattleLog());
        textArea.setCaretPosition(textArea.getDocument().getLength());
        
        updateDisplay();
        return centerPanel;
    }

    public void updateDisplay() {
        gameWindow.updateHeroStats();
        
        List<Enemy> enemies = currentBattle.getEnemies();
        
        if (currentBattle.getMode() == 1) {
            // Sequential mode: update labels
            for (int i = 0; i < enemies.size(); i++) {
                Enemy enemy = enemies.get(i);
                JLabel label = (JLabel) battleStatsPanel.getComponent(i);
                
                String text = String.format("%s %s: %d %s: %d", 
                    enemy.getName(),
                    Messages.get(Messages.Key.SKILL), enemy.getSkill(),
                    Messages.get(Messages.Key.STAMINA), enemy.getStamina());
                
                // Bold the first active enemy (current target)
                if (enemy.isActive() && !currentBattle.getActiveEnemies().isEmpty() && currentBattle.getActiveEnemies().get(0) == enemy) {
                    text = "<html><b>" + text + "</b></html>";
                }
                
                label.setText(text);
            }
        } else {
            // Simultaneous mode: update radio buttons
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
                radioButton.setEnabled(enemy.isActive());
                
                if (!enemy.isActive() && radioButton.isSelected()) {
                    for (int j = 0; j < enemies.size(); j++) {
                        if (enemies.get(j).isActive()) {
                            enemyRadioButtons.get(j).setSelected(true);
                            currentBattle.setSelectedEnemy(j);
                            break;
                        }
                    }
                }
            }
        }
        
        textArea.setText(currentBattle.getBattleLog());
        buttonPanel.removeAll();
        
        if (currentBattle.isOver()) {
            if (currentBattle.heroWon()) {
                Map<String, Object> battleData = (Map<String, Object>) battleActionData.get("battle");
                
                // Check if interrupted with custom chapter
                int targetChapter;
                if (currentBattle.wasInterrupted() && currentBattle.getInterrupt() != null && currentBattle.getInterrupt().getChapter() != null) {
                    targetChapter = currentBattle.getInterrupt().getChapter();
                } else {
                    targetChapter = (Integer) battleData.get("win");
                }
                
                // Use winText if present, otherwise use default victory message
                // Skip victory message if interrupt is not a true victory (e.g., hero escapes)
                boolean showVictoryMessage = !(currentBattle.wasInterrupted() && 
                    currentBattle.getInterrupt() != null && 
                    !currentBattle.getInterrupt().isVictory());
                
                if (showVictoryMessage) {
                    if (battleData.containsKey("winText")) {
                        textArea.append("\n" + battleData.get("winText"));
                    } else {
                        textArea.append("\n" + Messages.get(Messages.Key.BATTLE_VICTORY_ALL));
                    }
                } else {
                    // Show interrupt message for non-victory interrupts
                    textArea.append("\n" + Messages.get(Messages.Key.BATTLE_INTERRUPT_HAPPENS));
                }
                
                JButton continueButton = new JButton(Messages.get(Messages.Key.BATTLE_CLOSE));
                continueButton.addActionListener(e -> {
                    currentBattle = null;
                    controller.getAdventureLog().log(String.format(Messages.get(Messages.Key.LOG_BATTLE_WON), controller.getHero().getStamina()));
                    controller.goToChapter(targetChapter);
                    onComplete.run();
                });
                buttonPanel.add(continueButton);
            } else {
                textArea.append("\n" + Messages.get(Messages.Key.BATTLE_DEFEAT_GENERAL));
                currentBattle = null;
                controller.getAdventureLog().log(Messages.get(Messages.Key.LOG_BATTLE_LOST));
                onComplete.run();
            }
        } else {
            JButton nextTurnButton = new JButton(Messages.get(Messages.Key.BATTLE_NEXT_TURN));
            nextTurnButton.addActionListener(e -> {
                nextTurnButton.setEnabled(false);
                
                List<Enemy> aliveBeforeTurn = new ArrayList<>(currentBattle.getAliveEnemies());
                currentBattle.executeTurn();
                
                // Log turn result
                controller.getAdventureLog().log(String.format(Messages.get(Messages.Key.LOG_BATTLE_TURN), currentBattle.getCurrentTurn(), currentBattle.getLastTurnResult().replace("\n", " ")));
                
                dicePanel.removeAll();
                dicePanel.setLayout(new BoxLayout(dicePanel, BoxLayout.Y_AXIS));
                
                // Reset dice panel size for normal turn
                int dicePanelHeight = (currentBattle.getMode() == 1 ? 1 : aliveBeforeTurn.size()) * 100;
                dicePanel.setPreferredSize(new Dimension(400, dicePanelHeight));
                
                List<AnimatedDicePanel> animatedPanels = new ArrayList<>();
                
                // Show ally vs enemy dice if in ally phase
                if (currentBattle.isAllyPhase() && currentBattle.getAlly() != null) {
                    Enemy ally = currentBattle.getAlly();
                    Enemy enemy = currentBattle.getEnemies().get(0);
                    
                    JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
                    rowPanel.setOpaque(false);
                    
                    JLabel allyLabel = new JLabel(ally.getName() + ":");
                    allyLabel.setFont(new Font("Arial", Font.BOLD, 20));
                    rowPanel.add(allyLabel);
                    
                    AnimatedDicePanel allyDicePanel = new AnimatedDicePanel(
                        ally.getHeroDice1(), ally.getHeroDice2());
                    animatedPanels.add(allyDicePanel);
                    rowPanel.add(allyDicePanel);
                    
                    JLabel enemyLabel = new JLabel(enemy.getName() + ":");
                    enemyLabel.setFont(new Font("Arial", Font.BOLD, 20));
                    rowPanel.add(enemyLabel);
                    
                    AnimatedDicePanel enemyDicePanel = new AnimatedDicePanel(
                        enemy.getEnemyDice1(), enemy.getEnemyDice2());
                    animatedPanels.add(enemyDicePanel);
                    rowPanel.add(enemyDicePanel);
                    
                    dicePanel.add(rowPanel);
                } else {
                    // In sequential mode, only show dice for the first alive enemy
                    List<Enemy> enemiesToShow = currentBattle.getMode() == 1 
                        ? aliveBeforeTurn.subList(0, 1) 
                        : aliveBeforeTurn;
                    
                    for (Enemy enemy : enemiesToShow) {
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
                        
                        // Update battle log and text area immediately after normal turn
                        textArea.setText(currentBattle.getBattleLog());
                        gameWindow.updateHeroStats();
                        
                        // If extra damage is enabled, show extra damage button instead of updating display
                        if (currentBattle.hasExtraDamage() && !currentBattle.isOver()) {
                            buttonPanel.removeAll();
                            JButton extraDamageButton = new JButton(Messages.get(Messages.Key.BATTLE_EXTRA_DAMAGE_BUTTON));
                            extraDamageButton.addActionListener(evt2 -> {
                                extraDamageButton.setEnabled(false);
                                
                                // Roll for extra damage first
                                int damage = currentBattle.rollExtraDamage();
                                int roll = currentBattle.getLastExtraDamageRoll();
                                
                                // Increase dice panel size for extra row (add 100px once)
                                dicePanel.setPreferredSize(new Dimension(400, dicePanel.getPreferredSize().height + 100));
                                dicePanel.revalidate();
                                
                                // Add extra damage dice row
                                JPanel extraRowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
                                extraRowPanel.setOpaque(false);
                                
                                JLabel extraLabel = new JLabel("Extra:");
                                extraLabel.setFont(new Font("Arial", Font.BOLD, 20));
                                extraRowPanel.add(extraLabel);
                                
                                AnimatedDicePanel extraDicePanel = new AnimatedDicePanel(roll);
                                extraRowPanel.add(extraDicePanel);
                                
                                dicePanel.add(extraRowPanel);
                                dicePanel.revalidate();
                                dicePanel.repaint();
                                
                                // Animate extra damage die
                                Timer extraAnimTimer = new Timer(50, null);
                                final int[] extraCount = {0};
                                extraAnimTimer.addActionListener(evt3 -> {
                                    if (extraCount[0] < 20) {
                                        extraDicePanel.updateAnimation();
                                        extraCount[0]++;
                                    } else {
                                        extraAnimTimer.stop();
                                        extraDicePanel.stopAnimation();
                                        
                                        String resultMsg;
                                        if (damage > 0) {
                                            resultMsg = String.format(Messages.get(Messages.Key.BATTLE_EXTRA_DAMAGE_HIT), damage);
                                            textArea.append("\n" + resultMsg + "\n\n");
                                            currentBattle.appendToBattleLog(resultMsg + "\n\n");
                                            controller.getAdventureLog().log(String.format(Messages.get(Messages.Key.LOG_BATTLE_EXTRA_DAMAGE), roll, String.format("%d STAMINA lost", damage)));
                                        } else {
                                            resultMsg = Messages.get(Messages.Key.BATTLE_EXTRA_DAMAGE_MISS);
                                            textArea.append("\n" + resultMsg + "\n\n");
                                            currentBattle.appendToBattleLog(resultMsg + "\n\n");
                                            controller.getAdventureLog().log(String.format(Messages.get(Messages.Key.LOG_BATTLE_EXTRA_DAMAGE), roll, "no damage"));
                                        }
                                        
                                        updateDisplay();
                                    }
                                });
                                extraAnimTimer.start();
                            });
                            buttonPanel.add(extraDamageButton);
                            buttonPanel.revalidate();
                            buttonPanel.repaint();
                        } else if (currentBattle.needsInterruptUI() && !currentBattle.isOver()) {
                            // Show interrupt check button
                            buttonPanel.removeAll();
                            JButton interruptCheckButton = new JButton(Messages.get(Messages.Key.BATTLE_INTERRUPT_ROLL));
                            interruptCheckButton.addActionListener(evt2 -> {
                                interruptCheckButton.setEnabled(false);
                                
                                // Check for interrupt (rolls dice internally)
                                boolean interrupted = currentBattle.checkInterrupt();
                                int[] rolls = currentBattle.getInterrupt().getDiceRolls();
                                
                                // Show dice animation
                                dicePanel.setPreferredSize(new Dimension(400, dicePanel.getPreferredSize().height + 100));
                                dicePanel.revalidate();
                                
                                JPanel interruptRowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
                                interruptRowPanel.setOpaque(false);
                                
                                JLabel interruptLabel = new JLabel(Messages.get(Messages.Key.BATTLE_INTERRUPT_ROLL) + ":");
                                interruptLabel.setFont(new Font("Arial", Font.BOLD, 20));
                                interruptRowPanel.add(interruptLabel);
                                
                                AnimatedDicePanel interruptDicePanel = new AnimatedDicePanel(rolls);
                                interruptRowPanel.add(interruptDicePanel);
                                
                                dicePanel.add(interruptRowPanel);
                                dicePanel.revalidate();
                                dicePanel.repaint();
                                
                                // Animate dice
                                Timer interruptAnimTimer = new Timer(50, null);
                                final int[] interruptCount = {0};
                                interruptAnimTimer.addActionListener(evt3 -> {
                                    if (interruptCount[0] < 20) {
                                        interruptDicePanel.updateAnimation();
                                        interruptCount[0]++;
                                    } else {
                                        interruptAnimTimer.stop();
                                        interruptDicePanel.stopAnimation();
                                        
                                        String resultMsg;
                                        if (interrupted) {
                                            resultMsg = Messages.get(Messages.Key.BATTLE_INTERRUPT_HAPPENS);
                                        } else {
                                            resultMsg = Messages.get(Messages.Key.BATTLE_INTERRUPT_FAIL);
                                        }
                                        textArea.append("\n" + resultMsg + "\n\n");
                                        currentBattle.appendToBattleLog(resultMsg + "\n\n");
                                        
                                        updateDisplay();
                                    }
                                });
                                interruptAnimTimer.start();
                            });
                            buttonPanel.add(interruptCheckButton);
                            buttonPanel.revalidate();
                            buttonPanel.repaint();
                        } else {
                            updateDisplay();
                            nextTurnButton.setEnabled(true);
                        }
                    }
                });
                animTimer.start();
            });
            buttonPanel.add(nextTurnButton);
            
            // Add escape button if escape is available
            if (currentBattle.canEscape()) {
                Map<String, Object> battleData = (Map<String, Object>) battleActionData.get("battle");
                Map<String, Object> escapeData = (Map<String, Object>) battleData.get("escape");
                int escapeChapter = (Integer) escapeData.get("chapter");
                boolean returnToBattle = escapeData.containsKey("returnToBattle") && (Boolean) escapeData.get("returnToBattle");
                
                // Use custom button text if provided, otherwise default escape text
                String buttonText = escapeData.containsKey("buttonText") ? 
                    (String) escapeData.get("buttonText") : 
                    Messages.get(Messages.Key.BATTLE_ESCAPE);
                
                JButton escapeButton = new JButton(buttonText);
                escapeButton.addActionListener(e -> {
                    // Escaping costs 2 STAMINA unless withoutDamage is true
                    boolean withoutDamage = escapeData.containsKey("withoutDamage") && (Boolean) escapeData.get("withoutDamage");
                    if (!withoutDamage) {
                        controller.getHero().modifyStaminaSilent(-2);
                        gameWindow.updateHeroStats();
                    }
                    controller.getAdventureLog().log(Messages.get(Messages.Key.LOG_BATTLE_ESCAPED));
                    
                    // If returnToBattle is true, save battle state and set return point
                    if (returnToBattle) {
                        controller.setReturnChapter(controller.getCurrentChapter().index);
                        controller.saveBattleState(currentBattle, battleActionData);
                    }
                    
                    currentBattle = null;
                    controller.goToChapter(escapeChapter);
                    onComplete.run();
                });
                buttonPanel.add(escapeButton);
            }
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
