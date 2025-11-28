package com.adventure;

public interface BattleInterrupt {
    boolean shouldCheck(Battle battle);
    boolean isTriggered(Battle battle);
    boolean needsUI();
    int[] getDiceRolls();
    Integer getChapter();
}
