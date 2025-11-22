package com.adventure;

import org.junit.jupiter.api.Test;
import java.util.List;
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
}
