package com.adventure;

import java.util.HashSet;
import java.util.Set;

public class ChapterStateManager {
    private Set<Integer> chaptersWithExecutedRandomModify = new HashSet<>();
    private Set<Integer> chaptersWithExecutedRandomGoto = new HashSet<>();
    private int lastDisplayedChapter = -1;
    private int soldItemsCount = 0;
    private int takenItemsCount = 0;
    
    public boolean hasExecutedRandomModify(int chapter) {
        return chaptersWithExecutedRandomModify.contains(chapter);
    }
    
    public void markRandomModifyExecuted(int chapter) {
        chaptersWithExecutedRandomModify.add(chapter);
    }
    
    public boolean hasExecutedRandomGoto(int chapter) {
        return chaptersWithExecutedRandomGoto.contains(chapter);
    }
    
    public void markRandomGotoExecuted(int chapter) {
        chaptersWithExecutedRandomGoto.add(chapter);
    }
    
    public void resetForNewChapter() {
        soldItemsCount = 0;
        takenItemsCount = 0;
    }
    
    public int getSoldItemsCount() {
        return soldItemsCount;
    }
    
    public void incrementSoldItems() {
        soldItemsCount++;
    }
    
    public int getTakenItemsCount() {
        return takenItemsCount;
    }
    
    public void incrementTakenItems() {
        takenItemsCount++;
    }
    
    public int getLastDisplayedChapter() {
        return lastDisplayedChapter;
    }
    
    public void setLastDisplayedChapter(int chapter) {
        lastDisplayedChapter = chapter;
    }
}
