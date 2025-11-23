package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

public class CheckEventTest {
    
    @Test
    public void testCheckEventWithItem() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-with-check-event.yaml");
        Adventure adventure = yaml.loadAs(inputStream, Adventure.class);
        GameController controller = new GameController(adventure);
        
        // Take elf-boots
        controller.getHero().addItem("Elf-boots");
        
        // Go to bridge
        controller.goToChapter(1);
        assertEquals(1, controller.getCurrentChapter().index);
        
        // Should have elf-boots path available
        assertTrue(controller.getHero().hasItem("Elf-boots"));
        
        // Take elf-boots path
        controller.goToChapter(2);
        assertEquals(2, controller.getCurrentChapter().index);
        assertEquals("Your elf-boots allow you to walk across the bridge effortlessly. You make it safely!", 
            controller.getCurrentChapter().actions.get(0).get("display").toString().trim());
    }
    
    @Test
    public void testCheckEventWithoutItem() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-with-check-event.yaml");
        Adventure adventure = yaml.loadAs(inputStream, Adventure.class);
        GameController controller = new GameController(adventure);
        
        // Don't take elf-boots
        
        // Go to bridge
        controller.goToChapter(1);
        assertEquals(1, controller.getCurrentChapter().index);
        
        // Should not have elf-boots
        assertFalse(controller.getHero().hasItem("Elf-boots"));
        
        // Take normal boots path
        controller.goToChapter(3);
        assertEquals(3, controller.getCurrentChapter().index);
        
        // Lost stamina
        assertEquals(22, controller.getHero().getStamina());
    }
    
    @Test
    public void testCheckEventWithEvent() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-with-check-event.yaml");
        Adventure adventure = yaml.loadAs(inputStream, Adventure.class);
        GameController controller = new GameController(adventure);
        
        // Record event instead of item
        controller.getHero().addEvent("Elf-boots");
        
        // Go to bridge
        controller.goToChapter(1);
        assertEquals(1, controller.getCurrentChapter().index);
        
        // Should have event
        assertTrue(controller.getHero().hasEvent("Elf-boots"));
        
        // Take elf-boots path (event works same as item)
        controller.goToChapter(2);
        assertEquals(2, controller.getCurrentChapter().index);
    }
}
