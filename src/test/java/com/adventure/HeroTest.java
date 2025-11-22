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
}
