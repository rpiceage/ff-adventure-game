package com.adventure;

import java.util.Random;

public class Battle {
    private Hero hero;
    private String enemyName;
    private int enemySkill;
    private int enemyStamina;
    private Random random;
    private String lastTurnResult;
    private StringBuilder battleLog;
    private int lastHeroDice1, lastHeroDice2, lastEnemyDice1, lastEnemyDice2;

    public Battle(Hero hero, String enemyName, int enemySkill, int enemyStamina) {
        this(hero, enemyName, enemySkill, enemyStamina, new Random());
    }

    public Battle(Hero hero, String enemyName, int enemySkill, int enemyStamina, Random random) {
        this.hero = hero;
        this.enemyName = enemyName;
        this.enemySkill = enemySkill;
        this.enemyStamina = enemyStamina;
        this.random = random;
        this.lastTurnResult = "";
        this.battleLog = new StringBuilder();
    }

    public String getEnemyName() {
        return enemyName;
    }

    public int getEnemySkill() {
        return enemySkill;
    }

    public int getEnemyStamina() {
        return enemyStamina;
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

    public void executeTurn() {
        lastHeroDice1 = random.nextInt(6) + 1;
        lastHeroDice2 = random.nextInt(6) + 1;
        int heroAttack = hero.getSkill() + lastHeroDice1 + lastHeroDice2;

        lastEnemyDice1 = random.nextInt(6) + 1;
        lastEnemyDice2 = random.nextInt(6) + 1;
        int enemyAttack = enemySkill + lastEnemyDice1 + lastEnemyDice2;

        lastTurnResult = String.format("Hero: %d vs %s: %d\n",
            heroAttack, enemyName, enemyAttack);

        if (heroAttack > enemyAttack) {
            enemyStamina = Math.max(0, enemyStamina - 2);
            lastTurnResult += enemyName + " loses 2 STAMINA";
        } else if (enemyAttack > heroAttack) {
            hero.modifyStaminaSilent(-2);
            lastTurnResult += "Hero loses 2 STAMINA";
        } else {
            lastTurnResult += "Draw - no damage";
        }
        
        battleLog.append(lastTurnResult).append("\n\n");
    }

    public boolean isOver() {
        return hero.getStamina() == 0 || enemyStamina == 0;
    }

    public boolean heroWon() {
        return enemyStamina == 0 && hero.getStamina() > 0;
    }

    public int getLastHeroDice1() { return lastHeroDice1; }
    public int getLastHeroDice2() { return lastHeroDice2; }
    public int getLastEnemyDice1() { return lastEnemyDice1; }
    public int getLastEnemyDice2() { return lastEnemyDice2; }
}
