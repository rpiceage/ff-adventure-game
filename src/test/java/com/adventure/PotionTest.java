package com.adventure;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PotionTest {
    
    @Test
    public void testSkillPotionRestoresSkill() {
        Hero hero = new Hero(10, 20, 10);
        
        // Add SKILL potion
        Item skillPotion = Item.createPotion(Messages.get(Messages.Key.POTION_SKILL));
        hero.addItem(skillPotion);
        
        assertEquals(1, hero.getInventory().size());
        assertTrue(skillPotion.canUseAnyTime());
        
        // Reduce skill
        hero.setSkill(5);
        assertEquals(5, hero.getSkill());
        
        // Use potion
        skillPotion.use(hero);
        assertEquals(10, hero.getSkill());
        
        // Remove potion after use
        hero.removeItem(skillPotion);
        assertEquals(0, hero.getInventory().size());
    }
    
    @Test
    public void testStaminaPotionRestoresStamina() {
        Hero hero = new Hero(10, 20, 10);
        
        // Add STAMINA potion
        Item staminaPotion = Item.createPotion(Messages.get(Messages.Key.POTION_STAMINA));
        hero.addItem(staminaPotion);
        
        assertEquals(1, hero.getInventory().size());
        assertTrue(staminaPotion.canUseAnyTime());
        
        // Reduce stamina
        hero.setStamina(8);
        assertEquals(8, hero.getStamina());
        
        // Use potion
        staminaPotion.use(hero);
        assertEquals(20, hero.getStamina());
        
        // Remove potion after use
        hero.removeItem(staminaPotion);
        assertEquals(0, hero.getInventory().size());
    }
    
    @Test
    public void testLuckPotionRestoresLuckAndIncreasesMax() {
        Hero hero = new Hero(10, 20, 10);
        
        // Add LUCK potion
        Item luckPotion = Item.createPotion(Messages.get(Messages.Key.POTION_LUCK));
        hero.addItem(luckPotion);
        
        assertEquals(1, hero.getInventory().size());
        assertTrue(luckPotion.canUseAnyTime());
        
        // Reduce luck
        hero.setLuck(3);
        assertEquals(3, hero.getLuck());
        assertEquals(10, hero.getInitialLuck());
        
        // Use potion - should increase max by 1 AND restore to new max
        luckPotion.use(hero);
        assertEquals(11, hero.getLuck()); // Restored to new max
        assertEquals(11, hero.getInitialLuck()); // Max increased by 1
        
        // Remove potion after use
        hero.removeItem(luckPotion);
        assertEquals(0, hero.getInventory().size());
    }
    
    @Test
    public void testRegularItemsCannotBeUsedAnyTime() {
        Hero hero = new Hero(10, 20, 10);
        
        // Regular item (not a potion)
        Item sword = new Item("Sword");
        hero.addItem(sword);
        
        assertFalse(sword.canUseAnyTime());
        assertFalse(sword.hasEffect());
    }
    
    @Test
    public void testMultiplePotions() {
        Hero hero = new Hero(10, 20, 10);
        
        // Add multiple potions
        Item skillPotion = Item.createPotion(Messages.get(Messages.Key.POTION_SKILL));
        Item staminaPotion = Item.createPotion(Messages.get(Messages.Key.POTION_STAMINA));
        hero.addItem(skillPotion);
        hero.addItem(staminaPotion);
        
        assertEquals(2, hero.getInventory().size());
        
        // Reduce attributes
        hero.setSkill(5);
        hero.setStamina(10);
        
        // Use skill potion
        skillPotion.use(hero);
        assertEquals(10, hero.getSkill());
        assertEquals(10, hero.getStamina()); // Stamina unchanged
        
        // Use stamina potion
        staminaPotion.use(hero);
        assertEquals(10, hero.getSkill());
        assertEquals(20, hero.getStamina());
    }
}
