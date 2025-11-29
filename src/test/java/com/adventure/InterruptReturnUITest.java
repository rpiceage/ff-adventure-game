package com.adventure;

import com.adventure.ui.BattleUI;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class InterruptReturnUITest {
    
    private Adventure loadAdventure(String resourceName) {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream inputStream = getClass().getResourceAsStream("/" + resourceName);
        return yaml.load(inputStream);
    }
    
    @Test
    public void testInterruptReturnPreservesBattleState() throws Exception {
        // Load adventure with weak enemy
        Adventure adventure = loadAdventure("sample-with-interrupt-return-ui-test.yaml");
        GameController controller = new GameController(adventure);
        
        // Create UI components
        JTextArea textArea = new JTextArea();
        JPanel buttonPanel = new JPanel();
        GameWindow gameWindow = new GameWindow(adventure);
        
        // Start battle with fixed random so hero always wins first turn
        Random fixedRandom = new Random() {
            private int callCount = 0;
            
            @Override
            public int nextInt(int bound) {
                callCount++;
                // Hero dice: high rolls
                if (callCount <= 2) return 5; // 6, 6
                // Enemy dice: low rolls
                return 0; // 1, 1
            }
        };
        
        // Create battle
        Hero hero = controller.getHero();
        Enemy enemy = new Enemy("Weak Monster", 5, 10);
        Battle battle = new Battle(hero, java.util.Arrays.asList(enemy), fixedRandom, 0);
        
        // Save initial enemy stamina
        int initialStamina = enemy.getStamina();
        assertEquals(10, initialStamina);
        
        // Execute first turn - hero should win and enemy loses 2 stamina
        battle.executeTurn();
        assertEquals(8, enemy.getStamina());
        assertTrue(battle.heroDealtDamageThisTurn());
        
        // Simulate escape with returnToBattle
        controller.setReturnChapter(0);
        controller.saveBattleState(battle, new java.util.HashMap<>());
        
        // Verify battle state is saved
        assertNotNull(controller.getSavedBattle());
        assertEquals(0, controller.getReturnChapter());
        
        // Navigate to interrupt chapter
        controller.goToChapter(1);
        assertEquals(1, controller.getCurrentChapter().index);
        
        // Verify battle state is still saved
        assertNotNull(controller.getSavedBattle());
        assertEquals(0, controller.getReturnChapter());
        
        // Navigate back to battle chapter
        controller.goToChapter(0);
        assertEquals(0, controller.getCurrentChapter().index);
        
        // Verify saved battle still has reduced enemy stamina
        Battle savedBattle = controller.getSavedBattle();
        assertNotNull(savedBattle);
        assertEquals(1, savedBattle.getEnemies().size());
        assertEquals(8, savedBattle.getEnemies().get(0).getStamina());
        assertEquals("Weak Monster", savedBattle.getEnemies().get(0).getName());
        
        // Verify turn count is preserved
        assertEquals(1, savedBattle.getCurrentTurn());
    }
    
    @Test
    public void testBattleLogPreservedAfterReturn() {
        Adventure adventure = loadAdventure("sample-with-interrupt-return-ui-test.yaml");
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        Enemy enemy = new Enemy("Test Enemy", 8, 12);
        Battle battle = new Battle(hero, java.util.Arrays.asList(enemy));
        
        // Add some content to battle log
        battle.appendToBattleLog("Turn 1 result\n");
        battle.appendToBattleLog("Turn 2 result\n");
        
        String originalLog = battle.getBattleLog();
        assertTrue(originalLog.contains("Turn 1 result"));
        assertTrue(originalLog.contains("Turn 2 result"));
        
        // Save battle state
        controller.saveBattleState(battle, new java.util.HashMap<>());
        
        // Retrieve saved battle
        Battle savedBattle = controller.getSavedBattle();
        assertNotNull(savedBattle);
        
        // Verify battle log is preserved
        String savedLog = savedBattle.getBattleLog();
        assertEquals(originalLog, savedLog);
        assertTrue(savedLog.contains("Turn 1 result"));
        assertTrue(savedLog.contains("Turn 2 result"));
    }
    
    @Test
    public void testMultipleEnemiesStatePreserved() {
        Adventure adventure = loadAdventure("sample-with-interrupt-return-ui-test.yaml");
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        Enemy enemy1 = new Enemy("Enemy 1", 8, 10);
        Enemy enemy2 = new Enemy("Enemy 2", 7, 8);
        Battle battle = new Battle(hero, java.util.Arrays.asList(enemy1, enemy2));
        
        // Damage first enemy
        enemy1.setStamina(6);
        // Damage second enemy
        enemy2.setStamina(4);
        
        // Save battle state
        controller.saveBattleState(battle, new java.util.HashMap<>());
        
        // Retrieve and verify
        Battle savedBattle = controller.getSavedBattle();
        assertNotNull(savedBattle);
        assertEquals(2, savedBattle.getEnemies().size());
        assertEquals(6, savedBattle.getEnemies().get(0).getStamina());
        assertEquals(4, savedBattle.getEnemies().get(1).getStamina());
    }
    
    @Test
    public void testSelectedEnemyPreserved() {
        Adventure adventure = loadAdventure("sample-with-interrupt-return-ui-test.yaml");
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        Enemy enemy1 = new Enemy("Enemy 1", 8, 10);
        Enemy enemy2 = new Enemy("Enemy 2", 7, 8);
        Battle battle = new Battle(hero, java.util.Arrays.asList(enemy1, enemy2));
        
        // Select second enemy
        battle.setSelectedEnemy(1);
        assertEquals(1, battle.getSelectedEnemyIndex());
        
        // Save battle state
        controller.saveBattleState(battle, new java.util.HashMap<>());
        
        // Retrieve and verify
        Battle savedBattle = controller.getSavedBattle();
        assertNotNull(savedBattle);
        assertEquals(1, savedBattle.getSelectedEnemyIndex());
    }
}
