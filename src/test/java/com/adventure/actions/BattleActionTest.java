package com.adventure.actions;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class BattleActionTest {
    
    @Test
    public void testCanHandle() {
        BattleAction action = new BattleAction();
        assertTrue(action.canHandle(Map.of("battle", Map.of())));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        BattleAction action = new BattleAction();
        assertEquals(ActionType.SINGLE_BUTTON, action.getActionType());
    }
    
    @Test
    public void testGetButtonText() {
        BattleAction action = new BattleAction();
        assertNotNull(action.getButtonText());
    }
}
