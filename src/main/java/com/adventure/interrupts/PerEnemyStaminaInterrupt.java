package com.adventure.interrupts;

import com.adventure.Battle;

import java.util.Map;

public class PerEnemyStaminaInterrupt implements BattleInterrupt {
    private final Map<String, Integer> enemyThresholds;
    
    public PerEnemyStaminaInterrupt(Map<String, Integer> enemyThresholds) {
        this.enemyThresholds = enemyThresholds;
    }
    
    @Override
    public boolean shouldCheck(Battle battle) {
        return battle.getEnemies().stream()
            .anyMatch(e -> enemyThresholds.containsKey(e.getName()) && 
                          e.getStamina() <= enemyThresholds.get(e.getName()) && 
                          e.getStamina() > 0);
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
        return false;
    }
}
