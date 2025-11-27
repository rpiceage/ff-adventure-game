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

public class AddItemActionTest {
    
    @Test
    public void testCanHandle() {
        AddItemAction action = new AddItemAction();
        assertTrue(action.canHandle(Map.of("addItem", Map.of())));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        AddItemAction action = new AddItemAction();
        assertEquals(ActionType.MULTIPLE_BUTTONS, action.getActionType());
    }
    
    @Test
    public void testGetChoices() {
        AddItemAction action = new AddItemAction();
        Map<String, Object> data = Map.of("addItem", Map.of("items", List.of(
            Map.of("name", "Sword"),
            Map.of("name", "Shield")
        )));
        
        List<Action.Choice> choices = action.getChoices(data);
        assertEquals(2, choices.size());
        assertTrue(choices.get(0).text.contains("Sword"));
        assertTrue(choices.get(1).text.contains("Shield"));
    }
    
    @Test
    public void testAddItem() {
        AddItemAction action = new AddItemAction();
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Map<String, Object> data = Map.of("addItem", Map.of("items", List.of(
            Map.of("name", "Sword")
        )));
        
        action.addItem(controller, data, 0);
        assertTrue(controller.getHero().hasItem("Sword"));
    }
}
