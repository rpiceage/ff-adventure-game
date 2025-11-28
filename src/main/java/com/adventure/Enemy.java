package com.adventure;

public class Enemy {
    private String name;
    private int skill;
    private int stamina;
    private int heroDice1;
    private int heroDice2;
    private int enemyDice1;
    private int enemyDice2;
    private Integer retreatThreshold;
    private boolean retreated;

    public Enemy(String name, int skill, int stamina) {
        this.name = name;
        this.skill = skill;
        this.stamina = stamina;
        this.retreatThreshold = null;
        this.retreated = false;
    }

    public String getName() { return name; }
    public int getSkill() { return skill; }
    public int getStamina() { return stamina; }
    public int getHeroDice1() { return heroDice1; }
    public int getHeroDice2() { return heroDice2; }
    public int getEnemyDice1() { return enemyDice1; }
    public int getEnemyDice2() { return enemyDice2; }

    public void setStamina(int stamina) {
        this.stamina = Math.max(0, stamina);
    }

    public void setHeroDice(int dice1, int dice2) {
        this.heroDice1 = dice1;
        this.heroDice2 = dice2;
    }

    public void setEnemyDice(int dice1, int dice2) {
        this.enemyDice1 = dice1;
        this.enemyDice2 = dice2;
    }

    public boolean isAlive() {
        return stamina > 0;
    }
    
    public void setRetreatThreshold(Integer threshold) {
        this.retreatThreshold = threshold;
    }
    
    public Integer getRetreatThreshold() {
        return retreatThreshold;
    }
    
    public boolean hasRetreated() {
        return retreated;
    }
    
    public void retreat() {
        this.retreated = true;
    }
    
    public boolean isActive() {
        return isAlive() && !retreated;
    }
}
