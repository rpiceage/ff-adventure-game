package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

public class CheckParameterTest {
    
    @Test
    public void testCheckParameterMeetsThreshold() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-with-check-parameter.yaml");
        Adventure adventure = yaml.loadAs(inputStream, Adventure.class);
        GameController controller = new GameController(adventure);
        
        // Hero starts with SKILL 12, threshold is 10
        assertEquals(12, controller.getHero().getSkill());
        
        // Should navigate to greaterThanOrEquals chapter
        controller.goToChapter(1);
        assertEquals(1, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("broke through"));
    }
    
    @Test
    public void testCheckParameterBelowThreshold() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-with-check-parameter.yaml");
        Adventure adventure = yaml.loadAs(inputStream, Adventure.class);
        GameController controller = new GameController(adventure);
        
        // Reduce SKILL below threshold
        controller.getHero().modifySkill(-5);
        assertEquals(7, controller.getHero().getSkill());
        
        // Should navigate to lessThan chapter
        controller.goToChapter(2);
        assertEquals(2, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("too strong"));
    }
    
    @Test
    public void testCheckParameterExactThreshold() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-with-check-parameter.yaml");
        Adventure adventure = yaml.loadAs(inputStream, Adventure.class);
        GameController controller = new GameController(adventure);
        
        // Set SKILL to exactly threshold (10)
        controller.getHero().modifySkill(-2);
        assertEquals(10, controller.getHero().getSkill());
        
        // Should navigate to greaterThanOrEquals chapter (>= includes equal)
        controller.goToChapter(1);
        assertEquals(1, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("broke through"));
    }
}
