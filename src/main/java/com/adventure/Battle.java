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
        int heroDice1 = random.nextInt(6) + 1;
        int heroDice2 = random.nextInt(6) + 1;
        int heroAttack = hero.getSkill() + heroDice1 + heroDice2;

        int enemyDice1 = random.nextInt(6) + 1;
        int enemyDice2 = random.nextInt(6) + 1;
        int enemyAttack = enemySkill + enemyDice1 + enemyDice2;

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
}
