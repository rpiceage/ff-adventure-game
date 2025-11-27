package com.adventure.actions;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class RandomGotoActionTest {
    
    @Test
    public void testCanHandle() {
        RandomGotoAction action = new RandomGotoAction();
        assertTrue(action.canHandle(Map.of("randomGoto", Map.of())));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        RandomGotoAction action = new RandomGotoAction();
        assertEquals(ActionType.SINGLE_BUTTON, action.getActionType());
    }
    
    @Test
    public void testGetButtonText() {
        RandomGotoAction action = new RandomGotoAction();
        assertEquals("Roll dice", action.getButtonText());
    }
}
