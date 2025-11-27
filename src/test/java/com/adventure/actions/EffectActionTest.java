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

public class EffectActionTest {
    
    @Test
    public void testCanHandle() {
        EffectAction action = new EffectAction();
        assertTrue(action.canHandle(Map.of("effect", Map.of())));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        EffectAction action = new EffectAction();
        assertEquals(ActionType.PASSIVE, action.getActionType());
    }
    
    @Test
    public void testExecute() {
        EffectAction action = new EffectAction();
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Map<String, Object> data = Map.of("effect", Map.of("next", Map.of(
            "attackModifier", 2,
            "text", "Magic sword bonus"
        )));
        
        action.execute(controller, data);
        
        assertEquals(2, controller.getNextBattleAttackModifier());
        assertEquals("Magic sword bonus", controller.getNextBattleEffectText());
    }
    
    @Test
    public void testClearEffect() {
        EffectAction action = new EffectAction();
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Map<String, Object> data = Map.of("effect", Map.of("next", Map.of(
            "attackModifier", 1,
            "text", "Test effect"
        )));
        
        action.execute(controller, data);
        assertNotNull(controller.getNextBattleAttackModifier());
        
        controller.clearNextBattleEffect();
        assertNull(controller.getNextBattleAttackModifier());
        assertNull(controller.getNextBattleEffectText());
    }
}
