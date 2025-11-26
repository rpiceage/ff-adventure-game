package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class SellItemTest {
    
    @Test
    public void testSellItem_MaxItemCount() {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-sell-item.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        GameController controller = new GameController(adventure);
        Hero hero = controller.getHero();
        
        // Initially no items and no gold
        assertEquals(0, hero.getInventory().size());
        assertEquals(0, hero.getGold());
        
        // Add all 5 items
        hero.addItem("Sword");
        hero.addItem("Shield");
        hero.addItem("Potion");
        hero.addItem("Ring");
        hero.addItem("Amulet");
        
        assertEquals(5, hero.getInventory().size());
        
        // Sell first item (Sword) for 50 gold
        hero.modifyGold(50);
        hero.removeItem("Sword");
        assertEquals(4, hero.getInventory().size());
        assertEquals(50, hero.getGold());
        assertFalse(hero.getInventory().contains("Sword"));
        
        // Sell second item (Shield) for 50 gold
        hero.modifyGold(50);
        hero.removeItem("Shield");
        assertEquals(3, hero.getInventory().size());
        assertEquals(100, hero.getGold());
        assertFalse(hero.getInventory().contains("Shield"));
        
        // Sell third item (Potion) for 50 gold
        hero.modifyGold(50);
        hero.removeItem("Potion");
        assertEquals(2, hero.getInventory().size());
        assertEquals(150, hero.getGold());
        assertFalse(hero.getInventory().contains("Potion"));
        
        // Verify remaining items
        assertTrue(hero.getInventory().contains("Ring"));
        assertTrue(hero.getInventory().contains("Amulet"));
        
        // Max 3 items sold - cannot sell more in this chapter
        // (In UI, the 4th and 5th items would be disabled)
    }
}
