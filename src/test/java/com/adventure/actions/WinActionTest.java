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

public class WinActionTest {
    
    @Test
    public void testCanHandle() {
        WinAction action = new WinAction();
        assertTrue(action.canHandle(Map.of("win", "Victory text")));
        assertFalse(action.canHandle(Map.of("display", "text")));
        // Should not handle battle win field (integer)
        assertFalse(action.canHandle(Map.of("win", 1)));
    }
    
    @Test
    public void testGetActionType() {
        WinAction action = new WinAction();
        assertEquals(ActionType.DISPLAY, action.getActionType());
    }
    
    @Test
    public void testGetWinText() {
        WinAction action = new WinAction();
        Map<String, Object> data = Map.of("win", "  You won!  ");
        assertEquals("You won!", action.getWinText(data));
    }
    
    @Test
    public void testWinActionInGame() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-win.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        // Navigate to win chapter
        controller.goToChapter(1);
        
        // Check that win text is displayed
        String displayText = controller.getDisplayText();
        assertTrue(displayText.contains("Congratulations"));
        assertTrue(displayText.contains("won the game"));
    }
}
