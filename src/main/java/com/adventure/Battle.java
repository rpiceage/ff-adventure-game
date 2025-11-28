package com.adventure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Battle {
    private Hero hero;
    private List<Enemy> enemies;
    private int selectedEnemyIndex;
    private Random random;
    private String lastTurnResult;
    private StringBuilder battleLog;
    private int mode; // 0 = simultaneous (default), 1 = sequential
    private BattleInterrupt interrupt; // Optional: battle interrupt condition
    private boolean interrupted; // Track if battle was interrupted
    private Integer escapeTurn; // Optional: turn after which escape is allowed
    private int currentTurn; // Track current turn number
    private boolean heroDealtDamageThisTurn; // Track if hero won this turn
    private int modifierValue; // Modifier to hero's attack strength
    private String modifierText; // Explanation text for the modifier
    private boolean hasExtraDamage; // Whether extra damage is enabled
    private int extraDamageDice; // Number of dice for extra damage
    private List<Integer> extraDamageTriggers; // Die values that trigger damage
    private int extraDamageAmount; // Amount of stamina lost on trigger
    private int lastExtraDamageRoll; // Last extra damage die roll
    private Enemy ally; // Optional ally that fights first
    private boolean allyPhase; // True if ally is currently fighting
    private Integer extraSkillDamage; // Extra SKILL damage when hero is hit
    private Integer extraStaminaDamage; // Extra STAMINA damage when hero is hit
    private Integer extraLuckDamage; // Extra LUCK damage when hero is hit

    public Battle(Hero hero, String enemyName, int enemySkill, int enemyStamina) {
        this(hero, enemyName, enemySkill, enemyStamina, new Random());
    }

    public Battle(Hero hero, String enemyName, int enemySkill, int enemyStamina, Random random) {
        this.hero = hero;
        this.enemies = new ArrayList<>();
        this.enemies.add(new Enemy(enemyName, enemySkill, enemyStamina));
        this.selectedEnemyIndex = 0;
        this.random = random;
        this.lastTurnResult = "";
        this.battleLog = new StringBuilder();
        this.mode = 0;
        this.interrupt = null;
        this.interrupted = false;
        this.escapeTurn = null;
        this.currentTurn = 0;
        this.modifierValue = 0;
        this.modifierText = null;
        this.hasExtraDamage = false;
        this.extraDamageDice = 1;
        this.extraDamageTriggers = new ArrayList<>();
        this.extraDamageAmount = 0;
        this.lastExtraDamageRoll = 0;
        this.ally = null;
        this.allyPhase = false;
        this.extraSkillDamage = null;
        this.extraStaminaDamage = null;
        this.extraLuckDamage = null;
    }

    public Battle(Hero hero, List<Enemy> enemies) {
        this(hero, enemies, new Random(), 0);
    }

    public Battle(Hero hero, List<Enemy> enemies, Random random) {
        this(hero, enemies, random, 0);
    }

    public Battle(Hero hero, List<Enemy> enemies, Random random, int mode) {
        this.hero = hero;
        this.enemies = enemies;
        this.selectedEnemyIndex = 0;
        this.random = random;
        this.lastTurnResult = "";
        this.battleLog = new StringBuilder();
        this.mode = mode;
        this.interrupt = null;
        this.interrupted = false;
        this.escapeTurn = null;
        this.currentTurn = 0;
        this.modifierValue = 0;
        this.modifierText = null;
        this.hasExtraDamage = false;
        this.extraDamageDice = 1;
        this.extraDamageTriggers = new ArrayList<>();
        this.extraDamageAmount = 0;
        this.lastExtraDamageRoll = 0;
        this.ally = null;
        this.allyPhase = false;
        this.extraSkillDamage = null;
        this.extraStaminaDamage = null;
        this.extraLuckDamage = null;
    }

    public String getEnemyName() {
        return enemies.get(selectedEnemyIndex).getName();
    }

    public int getEnemySkill() {
        return enemies.get(selectedEnemyIndex).getSkill();
    }

    public int getEnemyStamina() {
        return enemies.get(selectedEnemyIndex).getStamina();
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public int getMode() {
        return mode;
    }

    public List<Enemy> getAliveEnemies() {
        return enemies.stream().filter(Enemy::isAlive).collect(Collectors.toList());
    }
    
    public List<Enemy> getActiveEnemies() {
        return enemies.stream().filter(Enemy::isActive).collect(Collectors.toList());
    }

    public int getSelectedEnemyIndex() {
        return selectedEnemyIndex;
    }

    public void setSelectedEnemy(int index) {
        this.selectedEnemyIndex = index;
    }

    public Hero getHero() {
        return hero;
    }

    public String getLastTurnResult() {
        return lastTurnResult;
    }

    public String getBattleLog() {
        return battleLog.toString();
    }
    
    public void appendToBattleLog(String text) {
        battleLog.append(text);
    }

    public void executeTurn() {
        currentTurn++;
        StringBuilder turnResult = new StringBuilder();
        int heroDamageTaken = 0;
        boolean heroDealtDamage = false;
        heroDealtDamageThisTurn = false;

        if (allyPhase && ally != null && ally.isAlive()) {
            // Ally fights the first enemy
            Enemy enemy = enemies.get(0);
            
            int allyDice1 = random.nextInt(6) + 1;
            int allyDice2 = random.nextInt(6) + 1;
            int allyAttack = ally.getSkill() + allyDice1 + allyDice2;

            int enemyDice1 = random.nextInt(6) + 1;
            int enemyDice2 = random.nextInt(6) + 1;
            int enemyAttack = enemy.getSkill() + enemyDice1 + enemyDice2;

            ally.setHeroDice(allyDice1, allyDice2);
            ally.setEnemyDice(enemyDice1, enemyDice2);
            enemy.setHeroDice(allyDice1, allyDice2);
            enemy.setEnemyDice(enemyDice1, enemyDice2);

            turnResult.append(String.format("%s: %d %s %s: %d - ",
                ally.getName(), allyAttack, 
                Messages.get(Messages.Key.BATTLE_VS), enemy.getName(), enemyAttack));

            if (allyAttack > enemyAttack) {
                enemy.setStamina(enemy.getStamina() - 2);
                turnResult.append(enemy.getName()).append(" ").append(Messages.get(Messages.Key.BATTLE_LOSES_STAMINA));
            } else if (enemyAttack > allyAttack) {
                ally.setStamina(ally.getStamina() - 2);
                turnResult.append(String.format(Messages.get(Messages.Key.BATTLE_ALLY_LOSES_STAMINA), ally.getName(), ally.getStamina()));
            } else {
                turnResult.append(Messages.get(Messages.Key.BATTLE_DRAW));
            }
            turnResult.append("\n");
            
            // Check if ally phase is over
            if (!ally.isAlive()) {
                allyPhase = false;
                turnResult.append(String.format(Messages.get(Messages.Key.LOG_BATTLE_ALLY_DIED), ally.getName())).append("\n");
            } else if (!enemy.isAlive()) {
                allyPhase = false;
            }
        } else if (mode == 1) {
            // Sequential mode: only fight the first active enemy
            Enemy enemy = getActiveEnemies().get(0);
            int i = enemies.indexOf(enemy);
            
            int heroDice1 = random.nextInt(6) + 1;
            int heroDice2 = random.nextInt(6) + 1;
            int heroAttack = hero.getSkill() + heroDice1 + heroDice2 + modifierValue;

            int enemyDice1 = random.nextInt(6) + 1;
            int enemyDice2 = random.nextInt(6) + 1;
            int enemyAttack = enemy.getSkill() + enemyDice1 + enemyDice2;

            enemy.setHeroDice(heroDice1, heroDice2);
            enemy.setEnemyDice(enemyDice1, enemyDice2);

            turnResult.append(String.format("%s: %d %s %s: %d - ",
                Messages.get(Messages.Key.BATTLE_HERO), heroAttack, 
                Messages.get(Messages.Key.BATTLE_VS), enemy.getName(), enemyAttack));

            if (heroAttack > enemyAttack) {
                enemy.setStamina(enemy.getStamina() - 2);
                heroDealtDamage = true;
                heroDealtDamageThisTurn = true;
                turnResult.append(enemy.getName()).append(" ").append(Messages.get(Messages.Key.BATTLE_LOSES_STAMINA));
            } else if (enemyAttack > heroAttack) {
                heroDamageTaken = 2;
                turnResult.append(Messages.get(Messages.Key.BATTLE_HERO_LOSES));
            } else {
                turnResult.append(Messages.get(Messages.Key.BATTLE_DRAW));
            }
            turnResult.append("\n");
        } else {
            // Simultaneous mode: fight all alive enemies
            for (int i = 0; i < enemies.size(); i++) {
                Enemy enemy = enemies.get(i);
                if (!enemy.isAlive()) continue;

                int heroDice1 = random.nextInt(6) + 1;
                int heroDice2 = random.nextInt(6) + 1;
                int heroAttack = hero.getSkill() + heroDice1 + heroDice2 + modifierValue;

                int enemyDice1 = random.nextInt(6) + 1;
                int enemyDice2 = random.nextInt(6) + 1;
                int enemyAttack = enemy.getSkill() + enemyDice1 + enemyDice2;

                enemy.setHeroDice(heroDice1, heroDice2);
                enemy.setEnemyDice(enemyDice1, enemyDice2);

                turnResult.append(String.format("%s: %d %s %s: %d - ",
                    Messages.get(Messages.Key.BATTLE_HERO), heroAttack, 
                    Messages.get(Messages.Key.BATTLE_VS), enemy.getName(), enemyAttack));

                if (heroAttack > enemyAttack) {
                    if (i == selectedEnemyIndex) {
                        enemy.setStamina(enemy.getStamina() - 2);
                        heroDealtDamage = true;
                        heroDealtDamageThisTurn = true;
                        turnResult.append(enemy.getName()).append(" ").append(Messages.get(Messages.Key.BATTLE_LOSES_STAMINA));
                    } else {
                        turnResult.append(Messages.get(Messages.Key.BATTLE_WINS_NOT_TARGETING));
                    }
                } else if (enemyAttack > heroAttack) {
                    heroDamageTaken += 2;
                    turnResult.append(Messages.get(Messages.Key.BATTLE_HERO_LOSES));
                } else {
                    turnResult.append(Messages.get(Messages.Key.BATTLE_DRAW));
                }
                turnResult.append("\n");
            }
        }

        if (heroDamageTaken > 0) {
            hero.modifyStaminaSilent(-heroDamageTaken);
            turnResult.append(String.format(Messages.get(Messages.Key.BATTLE_HERO_TAKES_DAMAGE), heroDamageTaken)).append("\n");
            
            // Apply extra attribute damage if configured
            if (extraSkillDamage != null && extraSkillDamage != 0) {
                hero.modifySkillSilent(-extraSkillDamage);
            }
            if (extraStaminaDamage != null && extraStaminaDamage != 0) {
                hero.modifyStaminaSilent(-extraStaminaDamage);
            }
            if (extraLuckDamage != null && extraLuckDamage != 0) {
                hero.modifyLuckSilent(-extraLuckDamage);
            }
        }
        
        // Check for enemy retreats
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && !enemy.hasRetreated() && 
                enemy.getRetreatThreshold() != null && 
                enemy.getStamina() <= enemy.getRetreatThreshold()) {
                enemy.retreat();
                turnResult.append(enemy.getName()).append(" retreats!\n");
            }
        }
        
        // Check for interrupt condition
        if (interrupt != null && interrupt.shouldCheck(this) && !interrupt.needsUI()) {
            if (interrupt.isTriggered(this)) {
                interrupted = true;
            }
        }

        lastTurnResult = turnResult.toString();
        battleLog.append(lastTurnResult).append("\n");
    }

    public boolean isOver() {
        return hero.getStamina() == 0 || getActiveEnemies().isEmpty() || interrupted;
    }

    public boolean heroWon() {
        return (getActiveEnemies().isEmpty() || interrupted) && hero.getStamina() > 0;
    }

    public int getLastHeroDice1() { 
        return enemies.get(selectedEnemyIndex).getHeroDice1();
    }
    
    public int getLastHeroDice2() { 
        return enemies.get(selectedEnemyIndex).getHeroDice2();
    }
    
    public int getLastEnemyDice1() { 
        return enemies.get(selectedEnemyIndex).getEnemyDice1();
    }
    
    public int getLastEnemyDice2() { 
        return enemies.get(selectedEnemyIndex).getEnemyDice2();
    }
    
    public void setInterrupt(BattleInterrupt interrupt) {
        this.interrupt = interrupt;
    }
    
    public BattleInterrupt getInterrupt() {
        return interrupt;
    }
    
    public boolean needsInterruptUI() {
        return interrupt != null && interrupt.shouldCheck(this) && interrupt.needsUI() && !interrupted;
    }
    
    public boolean checkInterrupt() {
        if (interrupt == null || !interrupt.shouldCheck(this)) {
            return false;
        }
        boolean triggered = interrupt.isTriggered(this);
        if (triggered) {
            interrupted = true;
        }
        return triggered;
    }
    
    public boolean heroDealtDamageThisTurn() {
        return heroDealtDamageThisTurn;
    }
    
    public boolean wasInterrupted() {
        return interrupted;
    }
    
    public void setEscapeTurn(Integer escapeTurn) {
        this.escapeTurn = escapeTurn;
    }
    
    public boolean canEscape() {
        return escapeTurn != null && currentTurn >= escapeTurn;
    }
    
    public int getCurrentTurn() {
        return currentTurn;
    }
    
    public void setModifier(int value, String text) {
        this.modifierValue = value;
        this.modifierText = text;
    }
    
    public String getModifierText() {
        return modifierText;
    }
    
    public void setExtraDamage(int dice, List<Integer> triggers, int damageAmount) {
        this.hasExtraDamage = true;
        this.extraDamageDice = dice;
        this.extraDamageTriggers = triggers;
        this.extraDamageAmount = damageAmount;
    }
    
    public void setExtraAttributeDamage(Integer skill, Integer stamina, Integer luck) {
        this.extraSkillDamage = skill;
        this.extraStaminaDamage = stamina;
        this.extraLuckDamage = luck;
    }
    
    public boolean hasExtraDamage() {
        return hasExtraDamage;
    }
    
    public int rollExtraDamage() {
        lastExtraDamageRoll = random.nextInt(6) + 1;
        if (extraDamageTriggers.contains(lastExtraDamageRoll)) {
            hero.modifyStaminaSilent(-extraDamageAmount);
            return extraDamageAmount;
        }
        return 0;
    }
    
    public int getLastExtraDamageRoll() {
        return lastExtraDamageRoll;
    }
    
    public void setAlly(String name, int skill, int stamina) {
        this.ally = new Enemy(name, skill, stamina);
        this.allyPhase = true;
    }
    
    public Enemy getAlly() {
        return ally;
    }
    
    public boolean isAllyPhase() {
        return allyPhase;
    }
}
