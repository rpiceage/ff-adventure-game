package com.adventure;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class NotificationTest {
    
    @Test
    public void testNormalModification() {
        Hero hero = new Hero(12, 24, 12);
        hero.modifyStamina(-5);
        List<String> mods = hero.getLastModifications();
        assertEquals(1, mods.size());
        assertEquals("STAMINA -5", mods.get(0));
    }

    @Test
    public void testFullyBlockedModification() {
        Hero hero = new Hero(12, 24, 12);
        hero.modifySkill(5);
        List<String> mods = hero.getLastModifications();
        assertEquals(1, mods.size());
        assertEquals("SKILL would have been modified but initial value cannot be exceeded", mods.get(0));
        assertEquals(12, hero.getSkill());
    }

    @Test
    public void testPartiallyCappedModification() {
        Hero hero = new Hero(12, 24, 12);
        hero.modifyStamina(-10);
        hero.clearModifications();
        hero.modifyStamina(15);
        List<String> mods = hero.getLastModifications();
        assertEquals(1, mods.size());
        assertEquals("STAMINA +10 (capped at 24)", mods.get(0));
        assertEquals(24, hero.getStamina());
    }

    @Test
    public void testMultipleModifications() {
        Hero hero = new Hero(12, 24, 12);
        hero.modifySkill(-2);
        hero.modifyStamina(-5);
        hero.modifyLuck(1);
        List<String> mods = hero.getLastModifications();
        assertEquals(3, mods.size());
        assertEquals("SKILL -2", mods.get(0));
        assertEquals("STAMINA -5", mods.get(1));
        assertEquals("LUCK would have been modified but initial value cannot be exceeded", mods.get(2));
    }

    @Test
    public void testClearModifications() {
        Hero hero = new Hero(12, 24, 12);
        hero.modifySkill(-1);
        hero.clearModifications();
        List<String> mods = hero.getLastModifications();
        assertEquals(0, mods.size());
    }
}
