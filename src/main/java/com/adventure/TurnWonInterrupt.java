package com.adventure;

public class TurnWonInterrupt implements BattleInterrupt {
    private final int turnsToWin;
    private int heroWonTurns = 0;
    
    public TurnWonInterrupt(int turnsToWin) {
        this.turnsToWin = turnsToWin;
    }
    
    @Override
    public boolean shouldCheck(Battle battle) {
        if (battle.heroDealtDamageThisTurn()) {
            heroWonTurns++;
        }
        return heroWonTurns >= turnsToWin;
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
