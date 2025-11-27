package com.adventure.actions;

import com.adventure.Adventure;
import com.adventure.GameController;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class CheckParameterActionTest {
    
    @Test
    public void testCanHandle() {
        CheckParameterAction action = new CheckParameterAction();
        assertTrue(action.canHandle(Map.of("checkParameter", Map.of())));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        CheckParameterAction action = new CheckParameterAction();
        assertEquals(ActionType.MULTIPLE_BUTTONS, action.getActionType());
    }
    
    @Test
    public void testGetChoices() {
        CheckParameterAction action = new CheckParameterAction();
        Map<String, Object> data = Map.of("checkParameter", Map.of(
            "parameter", "SKILL",
            "threshold", 10,
            "greaterThanOrEquals", Map.of("chapter", 1, "text", "Success"),
            "lessThan", Map.of("chapter", 2, "text", "Failure")
        ));
        
        List<Action.Choice> choices = action.getChoices(data);
        assertEquals(2, choices.size());
        assertEquals("Success", choices.get(0).text);
        assertEquals("Failure", choices.get(1).text);
    }
    
    @Test
    public void testMeetsThresholdTrue() {
        CheckParameterAction action = new CheckParameterAction();
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Map<String, Object> data = Map.of("checkParameter", Map.of(
            "parameter", "SKILL",
            "threshold", 10,
            "greaterThanOrEquals", Map.of("chapter", 1, "text", "Success"),
            "lessThan", Map.of("chapter", 2, "text", "Failure")
        ));
        
        assertTrue(action.meetsThreshold(controller, data));
    }
    
    @Test
    public void testMeetsThresholdFalse() {
        CheckParameterAction action = new CheckParameterAction();
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Map<String, Object> data = Map.of("checkParameter", Map.of(
            "parameter", "SKILL",
            "threshold", 20,
            "greaterThanOrEquals", Map.of("chapter", 1, "text", "Success"),
            "lessThan", Map.of("chapter", 2, "text", "Failure")
        ));
        
        assertFalse(action.meetsThreshold(controller, data));
    }
}
