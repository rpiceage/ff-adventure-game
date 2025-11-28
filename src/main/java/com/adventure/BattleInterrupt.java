package com.adventure;

public interface BattleInterrupt {
    boolean shouldCheck(Battle battle);
    boolean isTriggered(Battle battle);
    boolean needsUI();
    int[] getDiceRolls();
    Integer getChapter();
    boolean isVictory(); // true if interrupt counts as victory, false if escape/survival
}
