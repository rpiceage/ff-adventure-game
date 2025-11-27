package com.adventure.actions;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class SellItemActionTest {
    
    @Test
    public void testCanHandle() {
        SellItemAction action = new SellItemAction();
        assertTrue(action.canHandle(Map.of("sellItem", Map.of())));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        SellItemAction action = new SellItemAction();
        assertEquals(ActionType.DISPLAY, action.getActionType());
    }
    
    @Test
    public void testGetGoldPerItem() {
        SellItemAction action = new SellItemAction();
        Map<String, Object> data = Map.of("sellItem", Map.of("all", 50));
        assertEquals(50, action.getGoldPerItem(data));
    }
    
    @Test
    public void testGetMaxItemCount() {
        SellItemAction action = new SellItemAction();
        Map<String, Object> data = Map.of("sellItem", Map.of("all", 50, "maxItemCount", 10));
        assertEquals(10, action.getMaxItemCount(data));
    }
    
    @Test
    public void testGetMaxItemCountDefault() {
        SellItemAction action = new SellItemAction();
        Map<String, Object> data = Map.of("sellItem", Map.of("all", 50));
        assertEquals(Integer.MAX_VALUE, action.getMaxItemCount(data));
    }
}
