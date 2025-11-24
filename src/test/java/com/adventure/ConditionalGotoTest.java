package com.adventure;

import com.adventure.actions.Action;
import com.adventure.actions.GotoAction;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class ConditionalGotoTest {
    
    @Test
    public void testConditionalChoiceEnabled() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-conditional-goto.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        assertEquals(50, hero.getGold());
        
        // Choice should be enabled
        controller.selectChoice(0); // Buy cheap item
        assertEquals(1, controller.getCurrentChapter().index);
    }
    
    @Test
    public void testMixedEnabledAndDisabledChoices() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-conditional-goto.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        assertEquals(50, hero.getGold());
        
        // Get the goto action and check choices
        Map<String, Object> actionData = controller.getCurrentActionData();
        GotoAction gotoAction = new GotoAction();
        List<Action.Choice> choices = gotoAction.getChoices(controller, actionData);
        
        // Should have 3 choices
        assertEquals(3, choices.size());
        assertTrue(choices.get(0).enabled);   // Buy cheap item (50 gold) - enabled
        assertFalse(choices.get(1).enabled);  // Buy expensive item (100 gold) - disabled
        assertTrue(choices.get(2).enabled);   // Leave - enabled
        
        // Can select enabled choices
        controller.selectChoice(0); // Buy cheap item
        assertEquals(1, controller.getCurrentChapter().index);
    }
    
    @Test
    public void testAllChoicesDisabled() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-conditional-goto.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        hero.setGold(30); // Not enough for either item
        
        // Get the goto action and check choices
        Map<String, Object> actionData = controller.getCurrentActionData();
        GotoAction gotoAction = new GotoAction();
        List<Action.Choice> choices = gotoAction.getChoices(controller, actionData);
        
        // Should have 3 choices
        assertEquals(3, choices.size());
        assertFalse(choices.get(0).enabled); // Buy cheap item - disabled
        assertFalse(choices.get(1).enabled); // Buy expensive item - disabled
        assertTrue(choices.get(2).enabled);  // Leave - enabled
        
        // Can still select "Leave"
        controller.selectChoice(2); // Leave
        assertEquals(3, controller.getCurrentChapter().index);
    }
}
