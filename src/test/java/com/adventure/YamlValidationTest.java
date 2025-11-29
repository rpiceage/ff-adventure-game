package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class YamlValidationTest {
    
    @Test
    public void testAllBooksAreValid() {
        // Find all YAML files in resources/books
        File booksDir = new File("src/main/resources/books");
        assertTrue(booksDir.exists(), "Books directory not found");
        
        File[] yamlFiles = booksDir.listFiles((dir, name) -> name.endsWith(".yaml"));
        assertNotNull(yamlFiles, "No YAML files found");
        assertTrue(yamlFiles.length > 0, "No YAML files found in books directory");
        
        for (File yamlFile : yamlFiles) {
            String resourcePath = "/books/" + yamlFile.getName();
            System.out.println("Validating: " + resourcePath);
            validateYamlFile(resourcePath);
        }
    }
    
    private void validateYamlFile(String resourcePath) {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getResourceAsStream(resourcePath);
        assertNotNull(inputStream, "YAML file not found: " + resourcePath);
        
        Map<String, Object> data = yaml.load(inputStream);
        assertNotNull(data, "Failed to parse YAML: " + resourcePath);
        
        // Validate title
        assertTrue(data.containsKey("title"), resourcePath + ": Missing title");
        
        // Validate chapters
        assertTrue(data.containsKey("chapters"), resourcePath + ": Missing chapters");
        List<Map<String, Object>> chapters = (List<Map<String, Object>>) data.get("chapters");
        assertNotNull(chapters, resourcePath + ": Chapters is null");
        assertFalse(chapters.isEmpty(), resourcePath + ": No chapters found");
        
        Set<Integer> chapterIndices = new HashSet<>();
        
        for (Map<String, Object> chapter : chapters) {
            // Validate chapter structure
            assertTrue(chapter.containsKey("index"), resourcePath + ": Chapter missing index");
            assertTrue(chapter.containsKey("actions"), resourcePath + ": Chapter missing actions");
            
            int index = (Integer) chapter.get("index");
            assertFalse(chapterIndices.contains(index), resourcePath + ": Duplicate chapter index: " + index);
            chapterIndices.add(index);
            
            List<Map<String, Object>> actions = (List<Map<String, Object>>) chapter.get("actions");
            assertNotNull(actions, resourcePath + ": Chapter " + index + " has null actions");
            assertFalse(actions.isEmpty(), resourcePath + ": Chapter " + index + " has no actions");
            
            // Validate each action
            for (Map<String, Object> action : actions) {
                validateAction(action, index, resourcePath);
            }
        }
    }
    
    private void validateAction(Map<String, Object> action, int chapterIndex, String resourcePath) {
        assertFalse(action.isEmpty(), resourcePath + ": Empty action in chapter " + chapterIndex);
        
        // Check for known action types
        Set<String> knownActions = Set.of(
            "display", "goto", "modify", "battle", "luck", "addItem", "useItem", 
            "loseItem", "newEvent", "checkEvent", "death", "randomModify", 
            "randomGoto", "checkParameter", "setValue", "record", "sellItem", "effect",
            "attributeTest", "interrupt", "win"
        );
        
        boolean hasKnownAction = action.keySet().stream().anyMatch(knownActions::contains);
        assertTrue(hasKnownAction, resourcePath + ": Unknown action type in chapter " + chapterIndex + ": " + action.keySet());
        
        // Validate battle actions
        if (action.containsKey("battle")) {
            validateBattle((Map<String, Object>) action.get("battle"), chapterIndex, resourcePath);
        }
        
        // Validate goto actions
        if (action.containsKey("goto")) {
            validateGoto((List<Map<String, Object>>) action.get("goto"), chapterIndex, resourcePath);
        }
    }
    
    private void validateBattle(Map<String, Object> battle, int chapterIndex, String resourcePath) {
        assertTrue(battle.containsKey("enemies"), resourcePath + ": Battle in chapter " + chapterIndex + " missing enemies");
        assertTrue(battle.containsKey("win"), resourcePath + ": Battle in chapter " + chapterIndex + " missing win");
        
        List<Map<String, Object>> enemies = (List<Map<String, Object>>) battle.get("enemies");
        assertFalse(enemies.isEmpty(), resourcePath + ": Battle in chapter " + chapterIndex + " has no enemies");
        
        for (Map<String, Object> enemy : enemies) {
            assertTrue(enemy.containsKey("enemy"), resourcePath + ": Enemy missing name in chapter " + chapterIndex);
            assertTrue(enemy.containsKey("skill"), resourcePath + ": Enemy missing skill in chapter " + chapterIndex);
            assertTrue(enemy.containsKey("stamina"), resourcePath + ": Enemy missing stamina in chapter " + chapterIndex);
            
            // Validate no old interrupt format on enemies
            assertFalse(enemy.containsKey("interrupt"), 
                resourcePath + ": Enemy has old interrupt format in chapter " + chapterIndex + " - should use battle-level interrupt");
            assertFalse(enemy.containsKey("interruptPageTurnWon"), 
                resourcePath + ": Enemy has old interruptPageTurnWon format in chapter " + chapterIndex + " - should use turnLost interrupt");
        }
        
        // Validate interrupt if present
        if (battle.containsKey("interrupt")) {
            validateInterrupt((Map<String, Object>) battle.get("interrupt"), chapterIndex, resourcePath);
        }
    }
    
    private void validateInterrupt(Map<String, Object> interrupt, int chapterIndex, String resourcePath) {
        Set<String> validInterruptTypes = Set.of(
            "stamina", "turn", "turnWon", "turnLost", "enemiesKilled", 
            "heroStamina", "everyTurnWon", "everyTurnLost", 
            "enemyKilled", "enemyDamaged", "perEnemy"
        );
        
        boolean hasValidType = interrupt.keySet().stream().anyMatch(validInterruptTypes::contains);
        assertTrue(hasValidType, resourcePath + ": Unknown interrupt type in chapter " + chapterIndex + ": " + interrupt.keySet());
        
        // Validate specific interrupt types
        if (interrupt.containsKey("everyTurnWon") || interrupt.containsKey("everyTurnLost")) {
            assertTrue(interrupt.containsKey("dice"), resourcePath + ": Conditional interrupt missing dice in chapter " + chapterIndex);
            assertTrue(interrupt.containsKey("trigger"), resourcePath + ": Conditional interrupt missing trigger in chapter " + chapterIndex);
            assertTrue(interrupt.containsKey("page"), resourcePath + ": Conditional interrupt missing page in chapter " + chapterIndex);
        }
        
        if (interrupt.containsKey("turnLost")) {
            assertTrue(interrupt.containsKey("page"), resourcePath + ": turnLost interrupt missing page in chapter " + chapterIndex);
        }
        
        if (interrupt.containsKey("heroStamina")) {
            assertTrue(interrupt.containsKey("page"), resourcePath + ": heroStamina interrupt missing page in chapter " + chapterIndex);
        }
    }
    
    private void validateGoto(List<Map<String, Object>> choices, int chapterIndex, String resourcePath) {
        assertFalse(choices.isEmpty(), resourcePath + ": Goto in chapter " + chapterIndex + " has no choices");
        
        for (Map<String, Object> choice : choices) {
            assertTrue(choice.containsKey("chapter"), resourcePath + ": Goto choice missing chapter in chapter " + chapterIndex);
            assertTrue(choice.containsKey("text"), resourcePath + ": Goto choice missing text in chapter " + chapterIndex);
        }
    }
}
