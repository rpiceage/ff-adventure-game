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

public class LoseItemActionTest {
    
    @Test
    public void testCanHandle() {
        LoseItemAction action = new LoseItemAction();
        assertTrue(action.canHandle(Map.of("loseItem", Map.of())));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        LoseItemAction action = new LoseItemAction();
        assertEquals(ActionType.PASSIVE, action.getActionType());
    }
    
    @Test
    public void testLoseSpecificItems() {
        LoseItemAction action = new LoseItemAction();
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        controller.getHero().addItem("Sword");
        controller.getHero().addItem("Shield");
        
        Map<String, Object> data = Map.of("loseItem", Map.of("lose", List.of("Sword")));
        action.execute(controller, data);
        
        assertFalse(controller.getHero().hasItem("Sword"));
        assertTrue(controller.getHero().hasItem("Shield"));
    }
    
    @Test
    public void testLoseAllExcept() {
        LoseItemAction action = new LoseItemAction();
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        controller.getHero().addItem("Sword");
        controller.getHero().addItem("Shield");
        controller.getHero().addItem("Potion");
        
        Map<String, Object> data = Map.of("loseItem", Map.of("all", Map.of("except", List.of("Sword"))));
        action.execute(controller, data);
        
        assertTrue(controller.getHero().hasItem("Sword"));
        assertFalse(controller.getHero().hasItem("Shield"));
        assertFalse(controller.getHero().hasItem("Potion"));
    }
}
