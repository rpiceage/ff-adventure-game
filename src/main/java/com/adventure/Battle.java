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
    }

    public Battle(Hero hero, List<Enemy> enemies) {
        this(hero, enemies, new Random());
    }

    public Battle(Hero hero, List<Enemy> enemies, Random random) {
        this.hero = hero;
        this.enemies = enemies;
        this.selectedEnemyIndex = 0;
        this.random = random;
        this.lastTurnResult = "";
        this.battleLog = new StringBuilder();
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

    public List<Enemy> getAliveEnemies() {
        return enemies.stream().filter(Enemy::isAlive).collect(Collectors.toList());
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

    public void executeTurn() {
        StringBuilder turnResult = new StringBuilder();
        int heroDamageTaken = 0;

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (!enemy.isAlive()) continue;

            int heroDice1 = random.nextInt(6) + 1;
            int heroDice2 = random.nextInt(6) + 1;
            int heroAttack = hero.getSkill() + heroDice1 + heroDice2;

            int enemyDice1 = random.nextInt(6) + 1;
            int enemyDice2 = random.nextInt(6) + 1;
            int enemyAttack = enemy.getSkill() + enemyDice1 + enemyDice2;

            enemy.setHeroDice(heroDice1, heroDice2);
            enemy.setEnemyDice(enemyDice1, enemyDice2);

            turnResult.append(String.format("Hero: %d vs %s: %d - ",
                heroAttack, enemy.getName(), enemyAttack));

            if (heroAttack > enemyAttack) {
                if (i == selectedEnemyIndex) {
                    enemy.setStamina(enemy.getStamina() - 2);
                    turnResult.append(enemy.getName()).append(" loses 2 STAMINA");
                } else {
                    turnResult.append("Hero wins but not targeting this enemy");
                }
            } else if (enemyAttack > heroAttack) {
                heroDamageTaken += 2;
                turnResult.append("Hero loses");
            } else {
                turnResult.append("Draw");
            }
            turnResult.append("\n");
        }

        if (heroDamageTaken > 0) {
            hero.modifyStaminaSilent(-heroDamageTaken);
            turnResult.append(String.format("Hero takes %d STAMINA damage total\n", heroDamageTaken));
        }

        lastTurnResult = turnResult.toString();
        battleLog.append(lastTurnResult).append("\n");
    }

    public boolean isOver() {
        return hero.getStamina() == 0 || getAliveEnemies().isEmpty();
    }

    public boolean heroWon() {
        return getAliveEnemies().isEmpty() && hero.getStamina() > 0;
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
}
