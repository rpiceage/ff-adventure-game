package com.adventure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;
import java.io.FileInputStream;
import static org.junit.jupiter.api.Assertions.*;

public class GameFlowTest {
    private GameController controller;

    @BeforeEach
    public void setup() throws Exception {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        Adventure adventure = yaml.load(new FileInputStream(".amazonq/resources/sample.yaml"));
        controller = new GameController(adventure);
    }

    @Test
    public void testStartsAtChapter0() {
        assertEquals(0, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("Left or right?"));
    }

    @Test
    public void testLeftPath() {
        controller.selectChoice(0); // Left
        assertEquals(1, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("went left"));
    }

    @Test
    public void testRightPath() {
        controller.selectChoice(1); // Right
        assertEquals(2, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("went right"));
    }

    @Test
    public void testLeftThenFight() {
        controller.selectChoice(0); // Left
        controller.selectChoice(0); // Fight
        assertEquals(3, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("You win"));
    }

    @Test
    public void testRightThenRun() {
        controller.selectChoice(1); // Right
        controller.selectChoice(1); // Run
        assertEquals(4, controller.getCurrentChapter().index);
        assertTrue(controller.getDisplayText().contains("You lose"));
    }

    @Test
    public void testEndingHasNoChoices() {
        controller.selectChoice(0); // Left
        controller.selectChoice(0); // Fight
        assertEquals(0, controller.getChoices().size());
    }
}
