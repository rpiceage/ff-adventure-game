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
        assertEquals(10, controller.getHero().getSkill());
        assertEquals(20, controller.getHero().getStamina());
        assertEquals(10, controller.getHero().getLuck());
    }

    @Test
    public void testLeftPathReducesStamina() {
        controller.selectChoice(0); // Left
        assertEquals(1, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("went left"));
        assertEquals(18, controller.getHero().getStamina()); // -2
    }

    @Test
    public void testRightPathIncreasesStats() {
        controller.selectChoice(1); // Right
        assertEquals(2, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("healing potion"));
        assertEquals(25, controller.getHero().getStamina()); // +5
        assertEquals(11, controller.getHero().getLuck()); // +1
    }

    @Test
    public void testLeftThenFight() {
        controller.selectChoice(0); // Left
        controller.selectChoice(0); // Fight
        assertEquals(3, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("You win"));
        assertEquals(11, controller.getHero().getSkill()); // +1
        assertEquals(13, controller.getHero().getStamina()); // -2 then -5
    }

    @Test
    public void testLeftThenRun() {
        controller.selectChoice(0); // Left
        controller.selectChoice(1); // Run
        assertEquals(4, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("lose confidence"));
        assertEquals(8, controller.getHero().getLuck()); // -2
    }

    @Test
    public void testEndingHasNoChoices() {
        controller.selectChoice(0); // Left
        controller.selectChoice(0); // Fight
        assertEquals(0, controller.getChoices().size());
    }
}
