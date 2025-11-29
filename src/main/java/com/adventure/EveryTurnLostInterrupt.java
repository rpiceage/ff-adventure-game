package com.adventure;

import java.util.List;
import java.util.Random;

public class EveryTurnLostInterrupt implements BattleInterrupt {
    private final int dice;
    private final List<Integer> triggers;
    private final int chapter;
    private final Random random;
    private int[] lastRolls;
    
    public EveryTurnLostInterrupt(int dice, List<Integer> triggers, int chapter, Random random) {
        this.dice = dice;
        this.triggers = triggers;
        this.chapter = chapter;
        this.random = random;
    }
    
    @Override
    public boolean shouldCheck(Battle battle) {
        return battle.enemyDealtDamageThisTurn();
    }
    
    @Override
    public boolean isTriggered(Battle battle) {
        lastRolls = new int[dice];
        int total = 0;
        for (int i = 0; i < dice; i++) {
            lastRolls[i] = random.nextInt(6) + 1;
            total += lastRolls[i];
        }
        return triggers.contains(total);
    }
    
    @Override
    public boolean needsUI() {
        return true;
    }
    
    @Override
    public int[] getDiceRolls() {
        return lastRolls;
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
