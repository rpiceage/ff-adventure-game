package com.adventure.actions;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class RandomModifyActionTest {
    
    @Test
    public void testCanHandle() {
        RandomModifyAction action = new RandomModifyAction();
        assertTrue(action.canHandle(Map.of("randomModify", Map.of())));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        RandomModifyAction action = new RandomModifyAction();
        assertEquals(ActionType.SINGLE_BUTTON, action.getActionType());
    }
    
    @Test
    public void testGetButtonText() {
        RandomModifyAction action = new RandomModifyAction();
        assertEquals("Roll dice", action.getButtonText());
    }
}
