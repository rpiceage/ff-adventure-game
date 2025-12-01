package com.adventure.interrupts;

import com.adventure.Battle;
import com.adventure.Enemy;

public class StaminaInterrupt implements BattleInterrupt {
    private final int threshold;
    
    public StaminaInterrupt(int threshold) {
        this.threshold = threshold;
    }
    
    @Override
    public boolean shouldCheck(Battle battle) {
        return !battle.getAliveEnemies().isEmpty();
    }
    
    @Override
    public boolean isTriggered(Battle battle) {
        Enemy firstEnemy = battle.getAliveEnemies().get(0);
        return firstEnemy.getStamina() <= threshold && firstEnemy.getStamina() > 0;
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
