package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

public class VisitedChapterTest {
    
    @Test
    public void testChapterMarkedAsVisited() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-visited-chapters.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        
        // Chapter 0 should be marked as visited at start
        assertTrue(hero.hasVisitedChapter(0));
        assertFalse(hero.hasVisitedChapter(1));
        
        // Navigate to chapter 1
        controller.goToChapter(1);
        assertTrue(hero.hasVisitedChapter(1));
        assertFalse(hero.hasVisitedChapter(2));
    }
    
    @Test
    public void testVisitedChaptersOrder() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-visited-chapters.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        
        controller.goToChapter(1);
        controller.goToChapter(2);
        controller.goToChapter(1); // Visit 1 again
        
        assertEquals(4, hero.getVisitedChapters().size());
        assertEquals(0, hero.getVisitedChapters().get(0));
        assertEquals(1, hero.getVisitedChapters().get(1));
        assertEquals(2, hero.getVisitedChapters().get(2));
        assertEquals(1, hero.getVisitedChapters().get(3));
    }
    
    @Test
    public void testGotoActionFiltersVisitedChapters() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-visited-chapters.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        
        // At chapter 0, go to chapter 1
        controller.selectChoice(0);
        assertEquals(1, controller.getCurrentChapter().index);
        
        // At chapter 1, should have 3 options initially
        // But "Return to entrance" (chapter 0) should be filtered
        // So we should only see 2 options
        assertTrue(hero.hasVisitedChapter(0));
        assertTrue(hero.hasVisitedChapter(1));
    }
    
    @Test
    public void testFilteredChoiceIndexBug() {
        // This test reproduces the bug: 0 -> left (1) -> right path (2) -> should not reach 0
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-visited-chapters.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        
        // Start at 0, go left to chapter 1
        controller.selectChoice(0); // "Go left" - YAML index 0
        assertEquals(1, controller.getCurrentChapter().index);
        assertTrue(hero.hasVisitedChapter(0));
        assertTrue(hero.hasVisitedChapter(1));
        
        // At chapter 1, we have 3 options in YAML:
        // - index 0: "Return to entrance" (chapter 0) - FILTERED OUT
        // - index 1: "Continue to the right path" (chapter 2)
        // - index 2: "Go deeper into the dungeon" (chapter 3)
        
        // Select "Continue to the right path" - YAML index 1
        controller.selectChoice(1);
        assertEquals(2, controller.getCurrentChapter().index);
        assertTrue(hero.hasVisitedChapter(2));
    }
}
