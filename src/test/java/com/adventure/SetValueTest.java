package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

public class SetValueTest {
    
    @Test
    public void testSetGoldToZero() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-set-value.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        hero.setGold(50);
        
        assertEquals(50, hero.getGold());
        
        controller.goToChapter(1);
        
        assertEquals(0, hero.getGold());
    }
    
    @Test
    public void testSetMultipleValues() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-set-value.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        hero.setGold(50);
        hero.setProvisions(5);
        
        controller.goToChapter(2);
        
        assertEquals(0, hero.getGold());
        assertEquals(0, hero.getProvisions());
        assertEquals(10, hero.getLuck());
    }
    
    @Test
    public void testSetStamina() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-set-value.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        
        controller.goToChapter(3);
        
        assertEquals(1, hero.getStamina());
    }
}
