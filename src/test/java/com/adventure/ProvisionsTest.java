package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

public class ProvisionsTest {
    
    @Test
    public void testProvisionsInitialization() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-with-provisions.yaml");
        Adventure adventure = yaml.loadAs(inputStream, Adventure.class);
        GameController controller = new GameController(adventure);
        
        assertEquals(3, controller.getHero().getProvisions());
    }
    
    @Test
    public void testConsumeProvision() {
        Hero hero = new Hero(12, 24, 12, 0, 3);
        
        // Lose stamina
        hero.modifyStaminaSilent(-5);
        assertEquals(19, hero.getStamina());
        assertEquals(3, hero.getProvisions());
        
        // Consume provision
        assertTrue(hero.consumeProvision());
        assertEquals(23, hero.getStamina());
        assertEquals(2, hero.getProvisions());
    }
    
    @Test
    public void testConsumeProvisionRestoresCappedAtMax() {
        Hero hero = new Hero(12, 24, 12, 0, 3);
        
        // Lose 2 stamina
        hero.modifyStaminaSilent(-2);
        assertEquals(22, hero.getStamina());
        
        // Consume provision - should restore 4 but cap at 24
        assertTrue(hero.consumeProvision());
        assertEquals(24, hero.getStamina());
        assertEquals(2, hero.getProvisions());
    }
    
    @Test
    public void testCannotConsumeWhenAtMaxStamina() {
        Hero hero = new Hero(12, 24, 12, 0, 3);
        
        // At max stamina
        assertEquals(24, hero.getStamina());
        
        // Cannot consume
        assertFalse(hero.consumeProvision());
        assertEquals(24, hero.getStamina());
        assertEquals(3, hero.getProvisions());
    }
    
    @Test
    public void testCannotConsumeWhenNoProvisions() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        
        // Lose stamina
        hero.modifyStaminaSilent(-5);
        assertEquals(19, hero.getStamina());
        
        // Cannot consume - no provisions
        assertFalse(hero.consumeProvision());
        assertEquals(19, hero.getStamina());
        assertEquals(0, hero.getProvisions());
    }
    
    @Test
    public void testMultipleConsumptions() {
        Hero hero = new Hero(12, 24, 12, 0, 3);
        
        // Lose 10 stamina
        hero.modifyStaminaSilent(-10);
        assertEquals(14, hero.getStamina());
        
        // Consume first provision
        assertTrue(hero.consumeProvision());
        assertEquals(18, hero.getStamina());
        assertEquals(2, hero.getProvisions());
        
        // Consume second provision
        assertTrue(hero.consumeProvision());
        assertEquals(22, hero.getStamina());
        assertEquals(1, hero.getProvisions());
        
        // Consume third provision
        assertTrue(hero.consumeProvision());
        assertEquals(24, hero.getStamina());
        assertEquals(0, hero.getProvisions());
        
        // Cannot consume anymore
        assertFalse(hero.consumeProvision());
    }
    
    @Test
    public void testModifyProvisions() {
        Hero hero = new Hero(12, 24, 12, 0, 3);
        
        assertEquals(3, hero.getProvisions());
        
        // Add provisions
        hero.modifyProvisions(2);
        assertEquals(5, hero.getProvisions());
        
        // Remove provisions
        hero.modifyProvisions(-3);
        assertEquals(2, hero.getProvisions());
        
        // Cannot go below 0
        hero.modifyProvisions(-10);
        assertEquals(0, hero.getProvisions());
    }
}
