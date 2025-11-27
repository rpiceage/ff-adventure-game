package com.adventure;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChapterStateManagerTest {
    
    @Test
    public void testRandomModifyTracking() {
        ChapterStateManager manager = new ChapterStateManager();
        
        assertFalse(manager.hasExecutedRandomModify(1));
        
        manager.markRandomModifyExecuted(1);
        assertTrue(manager.hasExecutedRandomModify(1));
        assertFalse(manager.hasExecutedRandomModify(2));
    }
    
    @Test
    public void testRandomGotoTracking() {
        ChapterStateManager manager = new ChapterStateManager();
        
        assertFalse(manager.hasExecutedRandomGoto(1));
        
        manager.markRandomGotoExecuted(1);
        assertTrue(manager.hasExecutedRandomGoto(1));
        assertFalse(manager.hasExecutedRandomGoto(2));
    }
    
    @Test
    public void testSoldItemsCount() {
        ChapterStateManager manager = new ChapterStateManager();
        
        assertEquals(0, manager.getSoldItemsCount());
        
        manager.incrementSoldItems();
        assertEquals(1, manager.getSoldItemsCount());
        
        manager.incrementSoldItems();
        assertEquals(2, manager.getSoldItemsCount());
    }
    
    @Test
    public void testTakenItemsCount() {
        ChapterStateManager manager = new ChapterStateManager();
        
        assertEquals(0, manager.getTakenItemsCount());
        
        manager.incrementTakenItems();
        assertEquals(1, manager.getTakenItemsCount());
        
        manager.incrementTakenItems();
        assertEquals(2, manager.getTakenItemsCount());
    }
    
    @Test
    public void testResetForNewChapter() {
        ChapterStateManager manager = new ChapterStateManager();
        
        manager.incrementSoldItems();
        manager.incrementTakenItems();
        
        manager.resetForNewChapter();
        
        assertEquals(0, manager.getSoldItemsCount());
        assertEquals(0, manager.getTakenItemsCount());
    }
    
    @Test
    public void testLastDisplayedChapter() {
        ChapterStateManager manager = new ChapterStateManager();
        
        assertEquals(-1, manager.getLastDisplayedChapter());
        
        manager.setLastDisplayedChapter(5);
        assertEquals(5, manager.getLastDisplayedChapter());
        
        manager.setLastDisplayedChapter(10);
        assertEquals(10, manager.getLastDisplayedChapter());
    }
    
    @Test
    public void testRandomModifyMultipleChapters() {
        ChapterStateManager manager = new ChapterStateManager();
        
        manager.markRandomModifyExecuted(1);
        manager.markRandomModifyExecuted(3);
        manager.markRandomModifyExecuted(5);
        
        assertTrue(manager.hasExecutedRandomModify(1));
        assertFalse(manager.hasExecutedRandomModify(2));
        assertTrue(manager.hasExecutedRandomModify(3));
        assertFalse(manager.hasExecutedRandomModify(4));
        assertTrue(manager.hasExecutedRandomModify(5));
    }
}
