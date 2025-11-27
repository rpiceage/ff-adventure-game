package com.adventure.actions;

import com.adventure.Hero;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class ModifyActionTest {
    
    @Test
    public void testCanHandle() {
        ModifyAction action = new ModifyAction();
        assertTrue(action.canHandle(Map.of("modify", Map.of())));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        ModifyAction action = new ModifyAction();
        assertEquals(ActionType.PASSIVE, action.getActionType());
    }
    
    @Test
    public void testApplyModifications() {
        ModifyAction action = new ModifyAction();
        Hero hero = new Hero(10, 20, 10, 5, 3);
        
        Map<String, Object> data = Map.of("modify", Map.of("values", List.of(
            Map.of("field", "STAMINA", "value", -5),
            Map.of("field", "GOLD", "value", 10)
        )));
        
        action.applyModifications(hero, data);
        
        assertEquals(15, hero.getStamina());
        assertEquals(15, hero.getGold());
    }
    
    @Test
    public void testInvalidAttribute() {
        ModifyAction action = new ModifyAction();
        Hero hero = new Hero(10, 20, 10, 0, 0);
        
        Map<String, Object> data = Map.of("modify", Map.of("values", List.of(
            Map.of("field", "INVALID", "value", 5)
        )));
        
        assertThrows(IllegalArgumentException.class, () -> action.applyModifications(hero, data));
    }
}
