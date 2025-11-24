package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

public class LoseItemTest {
    
    @Test
    public void testLoseSpecificItem() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-lose-item.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        hero.addItem("Sword");
        hero.addItem("Shield");
        hero.addItem("Potion");
        
        assertEquals(3, hero.getInventory().size());
        
        controller.goToChapter(2);
        
        assertEquals(2, hero.getInventory().size());
        assertFalse(hero.getInventory().contains("Shield"));
        assertTrue(hero.getInventory().contains("Sword"));
        assertTrue(hero.getInventory().contains("Potion"));
    }
    
    @Test
    public void testLoseMultipleItems() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-lose-item.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        hero.addItem("Sword");
        hero.addItem("Shield");
        hero.addItem("Potion");
        
        controller.goToChapter(3);
        
        assertEquals(1, hero.getInventory().size());
        assertTrue(hero.getInventory().contains("Potion"));
    }
    
    @Test
    public void testLoseAllItemsExceptOne() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-lose-item.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        hero.addItem("Sword");
        hero.addItem("Shield");
        hero.addItem("Potion");
        hero.addItem("Key");
        
        controller.goToChapter(4);
        
        assertEquals(1, hero.getInventory().size());
        assertTrue(hero.getInventory().contains("Sword"));
    }
    
    @Test
    public void testLoseAllItems() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-lose-item.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        hero.addItem("Sword");
        hero.addItem("Shield");
        hero.addItem("Potion");
        
        controller.goToChapter(5);
        
        assertEquals(0, hero.getInventory().size());
    }
}
