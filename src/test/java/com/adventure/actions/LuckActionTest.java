package com.adventure.actions;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class LuckActionTest {
    
    @Test
    public void testCanHandle() {
        LuckAction action = new LuckAction();
        assertTrue(action.canHandle(Map.of("luck", Map.of())));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        LuckAction action = new LuckAction();
        assertEquals(ActionType.SINGLE_BUTTON, action.getActionType());
    }
    
    @Test
    public void testGetButtonText() {
        LuckAction action = new LuckAction();
        assertNotNull(action.getButtonText());
    }
}
