package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

public class EventTest {
    
    @Test
    public void testEventIsRecorded() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-with-events.yaml");
        Adventure adventure = yaml.loadAs(inputStream, Adventure.class);
        GameController controller = new GameController(adventure);
        
        // Initially no events
        assertFalse(controller.getHero().hasEvent("Djinn"));
        assertEquals(0, controller.getHero().getEvents().size());
        
        // Free the Djinn
        controller.selectChoice(0);
        assertEquals(1, controller.getCurrentChapter().index);
        
        // Event is recorded
        assertTrue(controller.getHero().hasEvent("Djinn"));
        assertEquals(1, controller.getHero().getEvents().size());
        assertEquals("Djinn", controller.getHero().getEvents().get(0));
    }
    
    @Test
    public void testEventNotRecordedWhenNotChosen() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-with-events.yaml");
        Adventure adventure = yaml.loadAs(inputStream, Adventure.class);
        GameController controller = new GameController(adventure);
        
        // Leave Djinn trapped
        controller.selectChoice(1);
        assertEquals(2, controller.getCurrentChapter().index);
        
        // Event not recorded
        assertFalse(controller.getHero().hasEvent("Djinn"));
        assertEquals(0, controller.getHero().getEvents().size());
    }
    
    @Test
    public void testEventPersistsAcrossChapters() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-with-events.yaml");
        Adventure adventure = yaml.loadAs(inputStream, Adventure.class);
        GameController controller = new GameController(adventure);
        
        // Free the Djinn
        controller.selectChoice(0);
        assertTrue(controller.getHero().hasEvent("Djinn"));
        
        // Continue to next chapter
        controller.selectChoice(0);
        assertEquals(3, controller.getCurrentChapter().index);
        
        // Event still recorded
        assertTrue(controller.getHero().hasEvent("Djinn"));
        
        // Continue further
        controller.selectChoice(0);
        assertEquals(4, controller.getCurrentChapter().index);
        
        // Event still recorded
        assertTrue(controller.getHero().hasEvent("Djinn"));
    }
    
    @Test
    public void testMultipleEvents() {
        Hero hero = new Hero(12, 24, 12);
        
        hero.addEvent("Djinn");
        hero.addEvent("Dragon");
        hero.addEvent("Treasure");
        
        assertEquals(3, hero.getEvents().size());
        assertTrue(hero.hasEvent("Djinn"));
        assertTrue(hero.hasEvent("Dragon"));
        assertTrue(hero.hasEvent("Treasure"));
        assertFalse(hero.hasEvent("Wizard"));
    }
}
