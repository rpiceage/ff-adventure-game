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

public class DeathActionTest {
    
    @Test
    public void testCanHandle() {
        DeathAction action = new DeathAction();
        assertTrue(action.canHandle(Map.of("death", "text")));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        DeathAction action = new DeathAction();
        assertEquals(ActionType.PASSIVE, action.getActionType());
    }
    
    @Test
    public void testExecute() {
        DeathAction action = new DeathAction();
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        assertEquals(24, controller.getHero().getStamina());
        
        action.execute(controller, Map.of("death", "You died"));
        
        assertEquals(0, controller.getHero().getStamina());
    }
}
