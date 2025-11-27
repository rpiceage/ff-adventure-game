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

public class NewEventActionTest {
    
    @Test
    public void testCanHandle() {
        NewEventAction action = new NewEventAction();
        assertTrue(action.canHandle(Map.of("newEvent", Map.of())));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        NewEventAction action = new NewEventAction();
        assertEquals(ActionType.PASSIVE, action.getActionType());
    }
    
    @Test
    public void testExecute() {
        NewEventAction action = new NewEventAction();
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Map<String, Object> data = Map.of("newEvent", Map.of("name", "TestEvent"));
        action.execute(controller, data);
        
        assertTrue(controller.getHero().hasEvent("TestEvent"));
    }
}
