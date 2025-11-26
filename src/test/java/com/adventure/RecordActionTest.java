package com.adventure;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RecordActionTest {
    
    @Test
    public void testRecord_SaveAndRestore() {
        Hero hero = new Hero(12, 24, 12);
        
        // Save initial SKILL value (12)
        hero.saveAttribute("SKILL");
        
        // Modify SKILL
        hero.modifySkill(-3);
        assertEquals(9, hero.getSkill());
        
        // Restore saved SKILL value
        hero.restoreAttribute("SKILL", false);
        assertEquals(12, hero.getSkill());
    }
    
    @Test
    public void testRecord_RestoreToInitial() {
        Hero hero = new Hero(12, 24, 12);
        
        // Reduce STAMINA
        hero.modifyStaminaSilent(-10);
        assertEquals(14, hero.getStamina());
        
        // Restore to initial value
        hero.restoreAttribute("STAMINA", true);
        assertEquals(24, hero.getStamina());
    }
    
    @Test
    public void testRecord_MultipleAttributes() {
        Hero hero = new Hero(12, 24, 12, 50, 3);
        
        // Save multiple attributes
        hero.saveAttribute("STAMINA");
        hero.saveAttribute("GOLD");
        hero.saveAttribute("PROVISIONS");
        
        // Modify them
        hero.modifyStaminaSilent(-10);
        hero.modifyGold(-30);
        hero.modifyProvisions(-2);
        
        assertEquals(14, hero.getStamina());
        assertEquals(20, hero.getGold());
        assertEquals(1, hero.getProvisions());
        
        // Restore all
        hero.restoreAttribute("STAMINA", false);
        hero.restoreAttribute("GOLD", false);
        hero.restoreAttribute("PROVISIONS", false);
        
        assertEquals(24, hero.getStamina());
        assertEquals(50, hero.getGold());
        assertEquals(3, hero.getProvisions());
    }
}
