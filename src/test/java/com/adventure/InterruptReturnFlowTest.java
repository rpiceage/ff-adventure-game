package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class InterruptReturnFlowTest {
    
    private Adventure loadAdventure(String resourceName) {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream inputStream = getClass().getResourceAsStream("/" + resourceName);
        return yaml.load(inputStream);
    }
    
    @Test
    public void testReturnChapterSetAndGet() {
        Adventure adventure = loadAdventure("sample-with-interrupt-return.yaml");
        GameController controller = new GameController(adventure);
        
        assertNull(controller.getReturnChapter());
        
        controller.setReturnChapter(5);
        assertEquals(5, controller.getReturnChapter());
    }
    
    @Test
    public void testReturnChapterCanBeCleared() {
        Adventure adventure = loadAdventure("sample-with-interrupt-return.yaml");
        GameController controller = new GameController(adventure);
        
        controller.setReturnChapter(5);
        assertEquals(5, controller.getReturnChapter());
        
        controller.clearReturnChapter();
        assertNull(controller.getReturnChapter());
    }
    
    @Test
    public void testBattleStateSaveAndRestore() {
        Adventure adventure = loadAdventure("sample-with-interrupt-return.yaml");
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        Enemy enemy = new Enemy("Test", 10, 12);
        Battle battle = new Battle(hero, "Test", 10, 12);
        Map<String, Object> actionData = new HashMap<>();
        
        assertNull(controller.getSavedBattle());
        assertNull(controller.getSavedBattleActionData());
        
        controller.saveBattleState(battle, actionData);
        
        assertNotNull(controller.getSavedBattle());
        assertNotNull(controller.getSavedBattleActionData());
        assertEquals(battle, controller.getSavedBattle());
        assertEquals(actionData, controller.getSavedBattleActionData());
    }
    
    @Test
    public void testBattleStateCanBeCleared() {
        Adventure adventure = loadAdventure("sample-with-interrupt-return.yaml");
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        Battle battle = new Battle(hero, "Test", 10, 12);
        Map<String, Object> actionData = new HashMap<>();
        
        controller.saveBattleState(battle, actionData);
        assertNotNull(controller.getSavedBattle());
        
        controller.clearSavedBattle();
        assertNull(controller.getSavedBattle());
        assertNull(controller.getSavedBattleActionData());
    }
    
    @Test
    public void testReturnChapterPreservedAcrossNavigation() {
        Adventure adventure = loadAdventure("sample-with-interrupt-return.yaml");
        GameController controller = new GameController(adventure);
        
        // Set return chapter
        controller.setReturnChapter(0);
        
        // Navigate to another chapter
        controller.goToChapter(2);
        
        // Return chapter should still be set
        assertEquals(0, controller.getReturnChapter());
        
        // Navigate to interrupt chapter
        controller.goToChapter(1);
        
        // Return chapter should still be set
        assertEquals(0, controller.getReturnChapter());
    }
}
