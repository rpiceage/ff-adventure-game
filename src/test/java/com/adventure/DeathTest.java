package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

public class DeathTest {
    
    @Test
    public void testDeathAction() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-with-death.yaml");
        Adventure adventure = yaml.loadAs(inputStream, Adventure.class);
        GameController controller = new GameController(adventure);
        
        // Hero starts alive
        assertEquals(24, controller.getHero().getStamina());
        assertFalse(controller.isGameOver());
        
        // Choose wrong door
        controller.selectChoice(1);
        assertEquals(2, controller.getCurrentChapter().index);
        
        // Hero dies
        assertEquals(0, controller.getHero().getStamina());
        assertTrue(controller.isGameOver());
    }
    
    @Test
    public void testDeathActionKillsHeroRegardlessOfStamina() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-with-death.yaml");
        Adventure adventure = yaml.loadAs(inputStream, Adventure.class);
        GameController controller = new GameController(adventure);
        
        // Hero has full stamina
        assertEquals(24, controller.getHero().getStamina());
        
        // Choose wrong door
        controller.goToChapter(2);
        
        // Hero dies even with full stamina
        assertEquals(0, controller.getHero().getStamina());
        assertTrue(controller.isGameOver());
    }
    
    @Test
    public void testDeathActionDisplaysMessage() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-with-death.yaml");
        Adventure adventure = yaml.loadAs(inputStream, Adventure.class);
        GameController controller = new GameController(adventure);
        
        // Go to death chapter
        controller.goToChapter(2);
        
        // Check death message is in chapter
        String deathMessage = (String) controller.getCurrentChapter().actions.get(0).get("death");
        assertTrue(deathMessage.contains("Your adventure ends here"));
        assertTrue(controller.isGameOver());
    }
}
