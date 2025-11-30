package com.adventure;

import com.adventure.actions.Action;
import com.adventure.actions.CheckEventAction;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class NumberOfItemChoicesTest {
    
    @Test
    public void testNumberOfItemChoicesWithThreeRings() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-number-of-item-choices.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        
        // Start at chapter 0 - take three rings
        assertEquals(0, controller.getCurrentChapter().index);
        
        // Add three rings
        hero.addItem("Gold Ring");
        hero.addItem("Gold Ring");
        hero.addItem("Gold Ring");
        
        controller.goToChapter(1);
        assertEquals(1, controller.getCurrentChapter().index);
        
        // Should have 3 Gold Rings
        assertEquals(3, hero.getInventory().stream()
            .filter(item -> item.getName().equals("Gold Ring"))
            .count());
        
        // Find the checkEvent action
        Map<String, Object> checkEventData = null;
        for (Map<String, Object> actionData : controller.getCurrentChapter().actions) {
            if (actionData.containsKey("checkEvent")) {
                checkEventData = actionData;
                break;
            }
        }
        assertNotNull(checkEventData);
        
        // Verify the action can determine the correct chapter
        CheckEventAction action = new CheckEventAction();
        assertTrue(action.isNumberOfItemCheck(checkEventData));
        
        // With 3 rings, should navigate to chapter 30 (choice index 2)
        int targetChapter = action.getChapterForItemCount(controller, checkEventData, 2);
        assertEquals(30, targetChapter);
    }
    
    @Test
    public void testNumberOfItemChoicesWithTwoRings() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-number-of-item-choices.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        
        // Add only 2 rings
        hero.addItem("Gold Ring");
        hero.addItem("Gold Ring");
        
        controller.goToChapter(1);
        assertEquals(1, controller.getCurrentChapter().index);
        
        // Should have 2 Gold Rings
        assertEquals(2, hero.getInventory().stream()
            .filter(item -> item.getName().equals("Gold Ring"))
            .count());
        
        // Find the checkEvent action
        Map<String, Object> checkEventData = null;
        for (Map<String, Object> actionData : controller.getCurrentChapter().actions) {
            if (actionData.containsKey("checkEvent")) {
                checkEventData = actionData;
                break;
            }
        }
        assertNotNull(checkEventData);
        
        CheckEventAction action = new CheckEventAction();
        
        // With 2 rings, should navigate to chapter 20 (choice index 1)
        int targetChapter = action.getChapterForItemCount(controller, checkEventData, 1);
        assertEquals(20, targetChapter);
    }
    
    @Test
    public void testNumberOfItemChoicesWithOneRing() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-number-of-item-choices.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        
        // Add only 1 ring
        hero.addItem("Gold Ring");
        
        controller.goToChapter(1);
        assertEquals(1, controller.getCurrentChapter().index);
        
        // Should have 1 Gold Ring
        assertEquals(1, hero.getInventory().stream()
            .filter(item -> item.getName().equals("Gold Ring"))
            .count());
        
        // Find the checkEvent action
        Map<String, Object> checkEventData = null;
        for (Map<String, Object> actionData : controller.getCurrentChapter().actions) {
            if (actionData.containsKey("checkEvent")) {
                checkEventData = actionData;
                break;
            }
        }
        assertNotNull(checkEventData);
        
        CheckEventAction action = new CheckEventAction();
        
        // With 1 ring, should navigate to chapter 10 (choice index 0)
        int targetChapter = action.getChapterForItemCount(controller, checkEventData, 0);
        assertEquals(10, targetChapter);
    }
}
