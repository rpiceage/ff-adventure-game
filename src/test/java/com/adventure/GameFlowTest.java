package com.adventure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;
import java.io.InputStream;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GameFlowTest {
    private GameController controller;

    @BeforeEach
    public void setup() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adventure = yaml.load(input);
        controller = new GameController(adventure);
    }

    @Test
    public void testStartsAtChapter0() {
        assertNotNull(controller, "Controller is null");
        assertNotNull(controller.getCurrentChapter(), "Current chapter is null");
        assertEquals(0, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("Left or right?"));
        assertEquals(12, controller.getHero().getSkill());
        assertEquals(24, controller.getHero().getStamina());
        assertEquals(12, controller.getHero().getLuck());
    }

    @Test
    public void testLeftPathReducesStamina() {
        controller.selectChoice(0); // Left
        assertEquals(1, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("went left"));
        assertEquals(22, controller.getHero().getStamina()); // 24 - 2
    }

    @Test
    public void testRightPathIncreasesStats() {
        controller.selectChoice(1); // Right
        assertEquals(2, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("healing potion"));
        assertEquals(24, controller.getHero().getStamina()); // 24 + 5 capped at 24
        assertEquals(12, controller.getHero().getLuck()); // 12 + 1 capped at 12
    }

    @Test
    public void testLeftThenFight() {
        controller.selectChoice(0); // Left
        controller.selectChoice(0); // Fight
        assertEquals(3, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("You win"));
        assertEquals(12, controller.getHero().getSkill()); // 12 + 1 capped at 12
        assertEquals(17, controller.getHero().getStamina()); // 24 - 2 - 5
    }

    @Test
    public void testLeftThenRun() {
        controller.selectChoice(0); // Left
        controller.selectChoice(1); // Run
        assertEquals(4, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("lose confidence"));
        assertEquals(10, controller.getHero().getLuck()); // 12 - 2
    }

    @Test
    public void testEndingHasNoChoices() {
        controller.selectChoice(0); // Left
        controller.selectChoice(0); // Fight
        assertNull(controller.getCurrentAction());
    }

    @Test
    public void testGameOverWhenStaminaReachesZero() {
        controller.selectChoice(0); // Left
        controller.selectChoice(2); // Surrender
        assertEquals(6, controller.getCurrentChapter().index);
        assertEquals(0, controller.getHero().getStamina());
        assertTrue(controller.isGameOver());
    }

    @Test
    public void testGoldModificationFlow() {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-gold.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        GameController controller = new GameController(adventure);
        Hero hero = controller.getHero();
        
        // Initial gold should be 0
        assertEquals(0, hero.getGold());
        
        // Chapter 0: Find treasure chest (+10 gold)
        controller.goToChapter(0);
        assertEquals(10, hero.getGold());
        
        // Chapter 1: Pay merchant (-5 gold)
        controller.goToChapter(1);
        assertEquals(5, hero.getGold());
        
        // Verify both modifications were tracked
        List<String> mods = hero.getLastModifications();
        assertEquals(2, mods.size());
        assertTrue(mods.get(0).contains("GOLD") && mods.get(0).contains("+10"));
        assertTrue(mods.get(1).contains("GOLD") && mods.get(1).contains("-5"));
    }
}
