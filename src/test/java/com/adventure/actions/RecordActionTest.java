package com.adventure.actions;

import com.adventure.Adventure;
import com.adventure.GameController;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;
import java.io.InputStream;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class RecordActionTest {
    
    @Test
    public void testCanHandle() {
        RecordAction action = new RecordAction();
        assertTrue(action.canHandle(Map.of("record", Map.of())));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        RecordAction action = new RecordAction();
        assertEquals(ActionType.PASSIVE, action.getActionType());
    }
    
    @Test
    public void testSaveAndRestore() {
        RecordAction action = new RecordAction();
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        controller.getHero().setSkill(10);
        
        Map<String, Object> saveData = Map.of("record", Map.of("set", Map.of("field", "SKILL")));
        action.execute(controller, saveData);
        
        controller.getHero().setSkill(5);
        assertEquals(5, controller.getHero().getSkill());
        
        Map<String, Object> restoreData = Map.of("record", Map.of("restore", Map.of("field", "SKILL")));
        action.execute(controller, restoreData);
        
        assertEquals(10, controller.getHero().getSkill());
    }
    
    @Test
    public void testRestoreToInitial() {
        RecordAction action = new RecordAction();
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        int initialSkill = controller.getHero().getSkill();
        controller.getHero().setSkill(5);
        
        Map<String, Object> restoreData = Map.of("record", Map.of("restore", Map.of("field", "SKILL", "initial", true)));
        action.execute(controller, restoreData);
        
        assertEquals(initialSkill, controller.getHero().getSkill());
    }
}
