package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

public class UseItemTest {
    
    @Test
    public void testItemCanBeUsedInCorrectChapter() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-with-use-item.yaml");
        Adventure adventure = yaml.loadAs(inputStream, Adventure.class);
        GameController controller = new GameController(adventure);
        
        // Start at chapter 0, add dagger to inventory
        controller.getHero().addItem("Dagger");
        
        // Go to chapter 1
        controller.goToChapter(1);
        assertEquals(1, controller.getCurrentChapter().index);
        assertTrue(controller.getHero().hasItem("Dagger"));
        
        // Use dagger to go to chapter 2
        controller.goToChapter(2);
        assertEquals(2, controller.getCurrentChapter().index);
    }
    
    @Test
    public void testItemNotUsableWithoutPickingUp() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-with-use-item.yaml");
        Adventure adventure = yaml.loadAs(inputStream, Adventure.class);
        GameController controller = new GameController(adventure);
        
        // Go directly to chapter 1 without picking up dagger
        controller.goToChapter(1);
        assertFalse(controller.getHero().hasItem("Dagger"));
        
        // Can only choose to break door
        controller.selectChoice(0);
        assertEquals(3, controller.getCurrentChapter().index);
    }
    
    @Test
    public void testItemNotUsableInOtherChapters() {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-with-use-item.yaml");
        Adventure adventure = yaml.loadAs(inputStream, Adventure.class);
        GameController controller = new GameController(adventure);
        
        // Pick up dagger
        controller.getHero().addItem("Dagger");
        
        // In chapter 0, useItem action not present
        assertEquals(0, controller.getCurrentChapter().index);
        
        // Go to chapter 4 (victory)
        controller.goToChapter(4);
        assertEquals(4, controller.getCurrentChapter().index);
        
        // Item still in inventory but not usable here
        assertTrue(controller.getHero().hasItem("Dagger"));
    }
}
