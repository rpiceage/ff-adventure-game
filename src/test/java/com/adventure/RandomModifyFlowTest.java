package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class RandomModifyFlowTest {
    
    @Test
    public void testRandomModifyCompleteFlow() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-with-random-modify.yaml");
        Adventure adventure = yaml.loadAs(inputStream, Adventure.class);
        GameController controller = new GameController(adventure);
        Hero hero = controller.getHero();
        
        // Chapter 0: randomModify with type 0 (decrease), 1 die
        assertEquals(0, controller.getCurrentChapter().index);
        int initialStamina = hero.getStamina();
        assertEquals(24, initialStamina);
        
        // Verify randomModify action exists
        Map<String, Object> randomModifyAction = controller.getCurrentChapter().actions.stream()
            .filter(a -> a.containsKey("randomModify"))
            .findFirst()
            .orElse(null);
        assertNotNull(randomModifyAction);
        
        Map<String, Object> randomModifyData = (Map<String, Object>) randomModifyAction.get("randomModify");
        assertEquals("STAMINA", randomModifyData.get("field"));
        assertEquals(0, randomModifyData.get("type"));
        assertEquals(1, randomModifyData.get("dice"));
        
        // Simulate rolling 4 on 1 die (type 0 = decrease by 4)
        hero.modifyStamina(-4);
        assertEquals(20, hero.getStamina()); // 24 - 4 = 20
        
        // Find goto action and navigate to chapter 1
        Map<String, Object> gotoAction = controller.getCurrentChapter().actions.stream()
            .filter(a -> a.containsKey("goto"))
            .findFirst()
            .orElse(null);
        assertNotNull(gotoAction);
        controller.selectChoice(0, gotoAction);
        assertEquals(1, controller.getCurrentChapter().index);
        
        // Chapter 1: randomModify with type 1 (increase), 2 dice
        randomModifyAction = controller.getCurrentChapter().actions.stream()
            .filter(a -> a.containsKey("randomModify"))
            .findFirst()
            .orElse(null);
        assertNotNull(randomModifyAction);
        
        randomModifyData = (Map<String, Object>) randomModifyAction.get("randomModify");
        assertEquals("STAMINA", randomModifyData.get("field"));
        assertEquals(1, randomModifyData.get("type"));
        assertEquals(2, randomModifyData.get("dice"));
        
        // Simulate rolling 3+5=8 on 2 dice (type 1 = increase by 8)
        hero.modifyStamina(8);
        assertEquals(24, hero.getStamina()); // 20 + 8 = 28, capped at 24
        
        // Navigate to chapter 2
        gotoAction = controller.getCurrentChapter().actions.stream()
            .filter(a -> a.containsKey("goto"))
            .findFirst()
            .orElse(null);
        assertNotNull(gotoAction);
        controller.selectChoice(0, gotoAction);
        assertEquals(2, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("adventure continues"));
    }
}
