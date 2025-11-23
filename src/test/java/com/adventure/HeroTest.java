package com.adventure;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class HeroTest {
    
    @Test
    public void testGoldModification() {
        Hero hero = new Hero(10, 20, 10, 0);
        hero.modifyGold(10);
        
        assertEquals(10, hero.getGold());
        List<String> mods = hero.getLastModifications();
        assertEquals(1, mods.size());
        assertTrue(mods.get(0).contains("GOLD"));
        assertTrue(mods.get(0).contains("+10"));
    }

    @Test
    public void testGoldCannotGoNegative() {
        Hero hero = new Hero(10, 20, 10, 5);
        hero.modifyGold(-10);
        
        assertEquals(0, hero.getGold());
    }

    @Test
    public void testGoldCanExceedInitial() {
        Hero hero = new Hero(10, 20, 10, 10);
        hero.modifyGold(20);
        
        assertEquals(30, hero.getGold());
    }

    @Test
    public void testGoldDefaultsToZero() {
        Hero hero = new Hero(10, 20, 10);
        assertEquals(0, hero.getGold());
    }

    @Test
    public void testGoldNegativeModification() {
        Hero hero = new Hero(10, 20, 10, 15);
        hero.modifyGold(-5);
        
        assertEquals(10, hero.getGold());
        List<String> mods = hero.getLastModifications();
        assertEquals(1, mods.size());
        assertTrue(mods.get(0).contains("GOLD"));
        assertTrue(mods.get(0).contains("-5"));
    }

    @Test
    public void testAddItem() {
        Hero hero = new Hero(10, 20, 10);
        hero.addItem("Sword");
        
        assertEquals(1, hero.getInventory().size());
        assertEquals("Sword", hero.getInventory().get(0));
        assertTrue(hero.hasItem("Sword"));
    }

    @Test
    public void testAddMultipleItems() {
        Hero hero = new Hero(10, 20, 10);
        hero.addItem("Sword");
        hero.addItem("Shield");
        hero.addItem("Potion");
        
        assertEquals(3, hero.getInventory().size());
        assertTrue(hero.hasItem("Sword"));
        assertTrue(hero.hasItem("Shield"));
        assertTrue(hero.hasItem("Potion"));
        assertFalse(hero.hasItem("Helmet"));
    }

    @Test
    public void testInventoryStartsEmpty() {
        Hero hero = new Hero(10, 20, 10);
        assertEquals(0, hero.getInventory().size());
        assertFalse(hero.hasItem("Anything"));
    }

    @Test
    public void testDuplicateItemsAllowed() {
        Hero hero = new Hero(10, 20, 10);
        hero.addItem("Potion");
        hero.addItem("Potion");
        
        assertEquals(2, hero.getInventory().size());
        assertTrue(hero.hasItem("Potion"));
    }
    
    @Test
    public void testModifyUnknownAttribute() {
        Hero hero = new Hero(12, 24, 12);
        com.adventure.actions.ModifyAction action = new com.adventure.actions.ModifyAction();
        
        Map<String, Object> actionData = new java.util.HashMap<>();
        Map<String, Object> modify = new java.util.HashMap<>();
        List<Map<String, Object>> values = new java.util.ArrayList<>();
        Map<String, Object> mod = new java.util.HashMap<>();
        mod.put("field", "INVALID");
        mod.put("value", 5);
        values.add(mod);
        modify.put("values", values);
        actionData.put("modify", modify);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            action.applyModifications(hero, actionData);
        });
        
        assertTrue(exception.getMessage().contains("Unknown attribute"));
        assertTrue(exception.getMessage().contains("INVALID"));
    }
}
