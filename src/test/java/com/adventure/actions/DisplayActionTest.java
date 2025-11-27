package com.adventure.actions;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class DisplayActionTest {
    
    @Test
    public void testCanHandle() {
        DisplayAction action = new DisplayAction();
        assertTrue(action.canHandle(Map.of("display", "text")));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        DisplayAction action = new DisplayAction();
        assertEquals(ActionType.DISPLAY, action.getActionType());
    }
    
    @Test
    public void testGetDisplayText() {
        DisplayAction action = new DisplayAction();
        Map<String, Object> data = Map.of("display", "  Test text  ");
        assertEquals("Test text", action.getDisplayText(data));
    }
}
