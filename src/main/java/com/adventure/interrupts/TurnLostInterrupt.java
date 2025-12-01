package com.adventure.interrupts;

import com.adventure.Battle;

public class TurnLostInterrupt implements BattleInterrupt {
    private final int turnsToLose;
    private final int chapter;
    private int enemyWonTurns = 0;
    
    public TurnLostInterrupt(int turnsToLose, int chapter) {
        this.turnsToLose = turnsToLose;
        this.chapter = chapter;
    }
    
    @Override
    public boolean shouldCheck(Battle battle) {
        return battle.enemyDealtDamageThisTurn();
    }
    
    @Override
    public boolean isTriggered(Battle battle) {
        enemyWonTurns++;
        return enemyWonTurns >= turnsToLose;
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
        return chapter;
    }
    
    @Override
    public boolean isVictory() {
        return false;
    }
}
