package com.adventure.interrupts;

import com.adventure.Battle;

public class HeroStaminaInterrupt implements BattleInterrupt {
    private final int threshold;
    private final int chapter;
    
    public HeroStaminaInterrupt(int threshold, int chapter) {
        this.threshold = threshold;
        this.chapter = chapter;
    }
    
    @Override
    public boolean shouldCheck(Battle battle) {
        return battle.getHero().getStamina() <= threshold;
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
        return false; // Hero escapes, not a true victory
    }
}
