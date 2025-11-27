package com.adventure.actions;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class UseItemActionTest {
    
    @Test
    public void testCanHandle() {
        UseItemAction action = new UseItemAction();
        assertTrue(action.canHandle(Map.of("useItem", List.of())));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        UseItemAction action = new UseItemAction();
        assertEquals(ActionType.DISPLAY, action.getActionType());
    }
}
