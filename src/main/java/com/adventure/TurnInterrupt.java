package com.adventure;

public class TurnInterrupt implements BattleInterrupt {
    private final int turnLimit;
    
    public TurnInterrupt(int turnLimit) {
        this.turnLimit = turnLimit;
    }
    
    @Override
    public boolean shouldCheck(Battle battle) {
        return battle.getCurrentTurn() >= turnLimit;
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
}
