package com.adventure.interrupts;

import com.adventure.Battle;

public class EnemiesKilledInterrupt implements BattleInterrupt {
    private final int enemiesToKill;
    private int enemiesKilled = 0;
    
    public EnemiesKilledInterrupt(int enemiesToKill) {
        this.enemiesToKill = enemiesToKill;
    }
    
    @Override
    public boolean shouldCheck(Battle battle) {
        int currentDead = battle.getEnemies().size() - battle.getAliveEnemies().size();
        if (currentDead > enemiesKilled) {
            enemiesKilled = currentDead;
        }
        return enemiesKilled >= enemiesToKill;
    }
    
    @Override
    public boolean isTriggered(Battle battle) {
        return true;
    }
    
    @Override
    public boolean needsUI() {
        return false;
    }
    
    @Override
    public int[] getDiceRolls() {
        return null;
    }
    
    @Override
    public Integer getChapter() {
        return null;
    }
    
    @Override
    public boolean isVictory() {
        return true;
    }
}
