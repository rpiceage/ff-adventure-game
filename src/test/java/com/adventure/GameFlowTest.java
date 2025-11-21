package com.adventure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;
import java.io.InputStream;
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
}
