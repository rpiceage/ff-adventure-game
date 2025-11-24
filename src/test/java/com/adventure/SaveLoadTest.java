package com.adventure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class SaveLoadTest {
    
    @Test
    public void testSaveAndLoadGame(@TempDir File tempDir) throws Exception {
        // Load a game
        Yaml yaml = new Yaml();
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-complete.yaml");
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        GameController controller = new GameController(adventure, "sample-complete.yaml");
        
        // Make some changes
        controller.getHero().modifyGold(50);
        controller.getHero().addItem("Test Item");
        controller.getHero().addEvent("Test Event");
        controller.goToChapter(1);
        
        // Save the game
        SaveGame saveGame = controller.createSaveGame();
        File saveFile = new File(tempDir, "test.ffsave");
        SaveGameManager.save(saveGame, saveFile);
        
        // Verify save file exists
        assertTrue(saveFile.exists());
        
        // Load the game
        SaveGame loadedSave = SaveGameManager.load(saveFile);
        
        // Verify loaded data
        assertEquals("Complete Adventure Demo", loadedSave.getGameTitle());
        assertEquals("sample-complete.yaml", loadedSave.getGameYamlPath());
        assertEquals(1, loadedSave.getCurrentChapterIndex());
        assertEquals(60, loadedSave.getGold()); // 10 initial + 50 added
        assertTrue(loadedSave.getInventory().contains("Test Item"));
        assertTrue(loadedSave.getEvents().contains("Test Event"));
        
        // Create new controller and load save
        GameController newController = new GameController(adventure, "sample-complete.yaml");
        newController.loadSaveGame(loadedSave);
        
        // Verify state was restored
        assertEquals(1, newController.getCurrentChapter().index);
        assertEquals(60, newController.getHero().getGold()); // 10 initial + 50 added
        assertTrue(newController.getHero().hasItem("Test Item"));
        assertTrue(newController.getHero().hasEvent("Test Event"));
    }
    
    @Test
    public void testSaveGamePreservesAllHeroState(@TempDir File tempDir) throws Exception {
        Yaml yaml = new Yaml();
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-complete.yaml");
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        GameController controller = new GameController(adventure, "sample-complete.yaml");
        
        // Modify all hero attributes
        controller.getHero().modifySkill(-2);
        controller.getHero().modifyStamina(-5);
        controller.getHero().modifyLuck(-1);
        controller.getHero().modifyGold(100);
        controller.getHero().modifyProvisions(3);
        controller.getHero().addItem("Sword");
        controller.getHero().addItem("Shield");
        controller.getHero().addEvent("Met Wizard");
        
        // Save and load
        SaveGame saveGame = controller.createSaveGame();
        File saveFile = new File(tempDir, "test2.ffsave");
        SaveGameManager.save(saveGame, saveFile);
        
        SaveGame loaded = SaveGameManager.load(saveFile);
        
        // Verify all attributes
        assertEquals(10, loaded.getSkill());
        assertEquals(19, loaded.getStamina());
        assertEquals(11, loaded.getLuck());
        assertEquals(110, loaded.getGold()); // 10 initial + 100 added
        assertEquals(3, loaded.getProvisions());
        assertEquals(12, loaded.getMaxSkill());
        assertEquals(24, loaded.getMaxStamina());
        assertEquals(12, loaded.getMaxLuck());
        assertEquals(2, loaded.getInventory().size());
        assertEquals(1, loaded.getEvents().size());
    }
}
