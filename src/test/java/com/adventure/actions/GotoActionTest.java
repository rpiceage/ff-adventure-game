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

public class GotoActionTest {
    
    @Test
    public void testCanHandle() {
        GotoAction action = new GotoAction();
        assertTrue(action.canHandle(Map.of("goto", List.of())));
        assertFalse(action.canHandle(Map.of("display", "data")));
    }
    
    @Test
    public void testGetActionType() {
        GotoAction action = new GotoAction();
        assertEquals(ActionType.MULTIPLE_BUTTONS, action.getActionType());
    }
    
    @Test
    public void testGetChoices() {
        GotoAction action = new GotoAction();
        Map<String, Object> data = Map.of("goto", List.of(
            Map.of("chapter", 1, "text", "Go left"),
            Map.of("chapter", 2, "text", "Go right")
        ));
        
        List<Action.Choice> choices = action.getChoices(data);
        assertEquals(2, choices.size());
        assertEquals("Go left", choices.get(0).text);
        assertEquals("Go right", choices.get(1).text);
    }
    
    @Test
    public void testFilterVisitedChapters() {
        GotoAction action = new GotoAction();
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        controller.getHero().visitChapter(1);
        
        Map<String, Object> data = Map.of("goto", List.of(
            Map.of("chapter", 1, "text", "Go to 1"),
            Map.of("chapter", 2, "text", "Go to 2")
        ));
        
        List<Action.Choice> choices = action.getChoices(controller, data);
        assertEquals(1, choices.size());
        assertEquals("Go to 2", choices.get(0).text);
    }
}
