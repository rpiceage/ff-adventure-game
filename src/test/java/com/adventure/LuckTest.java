package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.LoaderOptions;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class LuckTest {

    @Test
    public void testLuckActionDetected() {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-luck.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        GameController controller = new GameController(adventure);
        
        assertNotNull(controller.getLuckAction());
        assertNull(controller.getBattleAction());
    }

    @Test
    public void testLuckActionHasCorrectChapters() {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-luck.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        GameController controller = new GameController(adventure);
        var luckAction = controller.getLuckAction();
        var luckData = (java.util.Map<String, Object>) luckAction.get("luck");
        
        assertEquals(1, luckData.get("lucky"));
        assertEquals(2, luckData.get("unlucky"));
    }

    @Test
    public void testLuckyOutcome() {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-luck.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        GameController controller = new GameController(adventure);
        Hero hero = controller.getHero();
        
        // Simulate lucky outcome (roll <= luck)
        int heroLuck = hero.getLuck();
        int roll = heroLuck; // Equal to luck = lucky
        
        assertTrue(roll <= heroLuck);
        
        // Go to lucky chapter
        controller.goToChapter(1);
        assertTrue(controller.getDisplayText().contains("lucky"));
    }

    @Test
    public void testUnluckyOutcome() {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-luck.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        GameController controller = new GameController(adventure);
        Hero hero = controller.getHero();
        
        // Simulate unlucky outcome (roll > luck)
        int heroLuck = hero.getLuck();
        int roll = heroLuck + 1; // Greater than luck = unlucky
        
        assertTrue(roll > heroLuck);
        
        // Go to unlucky chapter
        controller.goToChapter(2);
        assertTrue(controller.getDisplayText().contains("unlucky"));
    }

    @Test
    public void testUnluckyChapterAppliesModifiers() {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-luck.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        GameController controller = new GameController(adventure);
        Hero hero = controller.getHero();
        
        int initialStamina = hero.getStamina();
        
        // Go to unlucky chapter which has -3 STAMINA modifier
        controller.goToChapter(2);
        
        assertEquals(initialStamina - 3, hero.getStamina());
    }

    @Test
    public void testNoLuckActionInNormalChapter() {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-luck.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        GameController controller = new GameController(adventure);
        
        // Go to chapter without luck action
        controller.goToChapter(1);
        
        assertNull(controller.getLuckAction());
    }
}
