package com.adventure;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AdventureLogTest {
    
    @Test
    public void testLogAddsEntry() {
        AdventureLog log = new AdventureLog();
        log.log("Test message");
        
        List<String> entries = log.getEntries();
        assertEquals(1, entries.size());
        assertTrue(entries.get(0).contains("Test message"));
        assertTrue(entries.get(0).matches("\\[\\d{2}:\\d{2}:\\d{2}\\] Test message"));
    }
    
    @Test
    public void testMultipleEntries() {
        AdventureLog log = new AdventureLog();
        log.log("First");
        log.log("Second");
        log.log("Third");
        
        List<String> entries = log.getEntries();
        assertEquals(3, entries.size());
        assertTrue(entries.get(0).contains("First"));
        assertTrue(entries.get(1).contains("Second"));
        assertTrue(entries.get(2).contains("Third"));
    }
    
    @Test
    public void testGetFullLog() {
        AdventureLog log = new AdventureLog();
        log.log("Line 1");
        log.log("Line 2");
        
        String fullLog = log.getFullLog();
        assertTrue(fullLog.contains("Line 1"));
        assertTrue(fullLog.contains("Line 2"));
        assertTrue(fullLog.contains("\n"));
    }
    
    @Test
    public void testClear() {
        AdventureLog log = new AdventureLog();
        log.log("Message");
        log.clear();
        
        assertEquals(0, log.getEntries().size());
        assertEquals("", log.getFullLog());
    }
    
    @Test
    public void testRestoreEntries() {
        AdventureLog log = new AdventureLog();
        List<String> saved = List.of("[10:00:00] Entry 1", "[10:00:01] Entry 2");
        
        log.restoreEntries(saved);
        
        assertEquals(2, log.getEntries().size());
        assertEquals("[10:00:00] Entry 1", log.getEntries().get(0));
        assertEquals("[10:00:01] Entry 2", log.getEntries().get(1));
    }
    
    @Test
    public void testRestoreEntriesWithNull() {
        AdventureLog log = new AdventureLog();
        log.log("Existing");
        
        log.restoreEntries(null);
        
        assertEquals(0, log.getEntries().size());
    }
    
    @Test
    public void testGetEntriesReturnsDefensiveCopy() {
        AdventureLog log = new AdventureLog();
        log.log("Original");
        
        List<String> entries = log.getEntries();
        entries.add("Modified");
        
        assertEquals(1, log.getEntries().size());
    }
}
