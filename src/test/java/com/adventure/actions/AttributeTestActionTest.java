package com.adventure.actions;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class AttributeTestActionTest {
    
    @Test
    public void testCanHandle() {
        AttributeTestAction action = new AttributeTestAction();
        assertTrue(action.canHandle(Map.of("attributeTest", Map.of())));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        AttributeTestAction action = new AttributeTestAction();
        assertEquals(ActionType.SINGLE_BUTTON, action.getActionType());
    }
    
    @Test
    public void testGetButtonText() {
        AttributeTestAction action = new AttributeTestAction();
        assertNotNull(action.getButtonText());
    }
}
