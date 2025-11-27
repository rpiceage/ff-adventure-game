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

public class CheckEventActionTest {
    
    @Test
    public void testCanHandle() {
        CheckEventAction action = new CheckEventAction();
        assertTrue(action.canHandle(Map.of("checkEvent", Map.of())));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        CheckEventAction action = new CheckEventAction();
        assertEquals(ActionType.MULTIPLE_BUTTONS, action.getActionType());
    }
    
    @Test
    public void testGetChoices() {
        CheckEventAction action = new CheckEventAction();
        Map<String, Object> data = Map.of("checkEvent", Map.of(
            "name", List.of("Event1"),
            "existing", Map.of("chapter", 1, "text", "You have it"),
            "missing", Map.of("chapter", 2, "text", "You don't have it")
        ));
        
        List<Action.Choice> choices = action.getChoices(data);
        assertEquals(2, choices.size());
        assertEquals("You have it", choices.get(0).text);
        assertEquals("You don't have it", choices.get(1).text);
    }
    
    @Test
    public void testHasEventOrItemWithEvent() {
        CheckEventAction action = new CheckEventAction();
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        controller.getHero().addEvent("TestEvent");
        
        Map<String, Object> data = Map.of("checkEvent", Map.of(
            "name", List.of("TestEvent"),
            "existing", Map.of("chapter", 1, "text", "Yes"),
            "missing", Map.of("chapter", 2, "text", "No")
        ));
        
        assertTrue(action.hasEventOrItem(controller, data));
    }
    
    @Test
    public void testHasEventOrItemWithItem() {
        CheckEventAction action = new CheckEventAction();
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        controller.getHero().addItem("Sword");
        
        Map<String, Object> data = Map.of("checkEvent", Map.of(
            "name", List.of("Sword"),
            "existing", Map.of("chapter", 1, "text", "Yes"),
            "missing", Map.of("chapter", 2, "text", "No")
        ));
        
        assertTrue(action.hasEventOrItem(controller, data));
    }
    
    @Test
    public void testHasEventOrItemWithNeither() {
        CheckEventAction action = new CheckEventAction();
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Map<String, Object> data = Map.of("checkEvent", Map.of(
            "name", List.of("NonExistent"),
            "existing", Map.of("chapter", 1, "text", "Yes"),
            "missing", Map.of("chapter", 2, "text", "No")
        ));
        
        assertFalse(action.hasEventOrItem(controller, data));
    }
}
