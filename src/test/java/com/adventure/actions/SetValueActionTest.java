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

public class SetValueActionTest {
    
    @Test
    public void testCanHandle() {
        SetValueAction action = new SetValueAction();
        assertTrue(action.canHandle(Map.of("setValue", Map.of())));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        SetValueAction action = new SetValueAction();
        assertEquals(ActionType.PASSIVE, action.getActionType());
    }
    
    @Test
    public void testExecute() {
        SetValueAction action = new SetValueAction();
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Map<String, Object> data = Map.of("setValue", Map.of("values", List.of(
            Map.of("field", "STAMINA", "value", 10),
            Map.of("field", "GOLD", "value", 50)
        )));
        
        action.execute(controller, data);
        
        assertEquals(10, controller.getHero().getStamina());
        assertEquals(50, controller.getHero().getGold());
    }
    
    @Test
    public void testInvalidAttribute() {
        SetValueAction action = new SetValueAction();
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Map<String, Object> data = Map.of("setValue", Map.of("values", List.of(
            Map.of("field", "INVALID", "value", 5)
        )));
        
        assertThrows(IllegalArgumentException.class, () -> action.execute(controller, data));
    }
}
