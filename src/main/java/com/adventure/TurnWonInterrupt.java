package com.adventure;

public class TurnWonInterrupt implements BattleInterrupt {
    private final int turnsToWin;
    private int heroWonTurns = 0;
    
    public TurnWonInterrupt(int turnsToWin) {
        this.turnsToWin = turnsToWin;
    }
    
    @Override
    public boolean shouldCheck(Battle battle) {
        return battle.heroDealtDamageThisTurn();
    }
    
    @Override
    public boolean isTriggered(Battle battle) {
        heroWonTurns++;
        return heroWonTurns >= turnsToWin;
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
