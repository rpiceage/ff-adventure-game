package com.adventure.interrupts;

import com.adventure.Battle;

public class CombinedEnemyInterrupt implements BattleInterrupt {
    private final String enemyToKill;
    private final String enemyToDamage;
    private final int damageThreshold;
    
    public CombinedEnemyInterrupt(String enemyToKill, String enemyToDamage, int damageThreshold) {
        this.enemyToKill = enemyToKill;
        this.enemyToDamage = enemyToDamage;
        this.damageThreshold = damageThreshold;
    }
    
    @Override
    public boolean shouldCheck(Battle battle) {
        boolean killedEnemy = battle.getEnemies().stream()
            .anyMatch(e -> e.getName().equals(enemyToKill) && e.getStamina() == 0);
        
        boolean damagedEnemy = battle.getEnemies().stream()
            .anyMatch(e -> e.getName().equals(enemyToDamage) && e.getStamina() <= damageThreshold && e.getStamina() > 0);
        
        return killedEnemy && damagedEnemy;
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
        return false; // Ambiguous outcome, show "Battle interrupted!" message
    }
}
