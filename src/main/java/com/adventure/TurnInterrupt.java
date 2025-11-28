package com.adventure;

public class TurnInterrupt implements BattleInterrupt {
    private final int turnLimit;
    private final Integer chapter;
    
    public TurnInterrupt(int turnLimit) {
        this(turnLimit, null);
    }
    
    public TurnInterrupt(int turnLimit, Integer chapter) {
        this.turnLimit = turnLimit;
        this.chapter = chapter;
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
        return chapter;
    }
    
    @Override
    public boolean isVictory() {
        return false; // Turn interrupt is ambiguous, show "Battle interrupted!" message
    }
}
