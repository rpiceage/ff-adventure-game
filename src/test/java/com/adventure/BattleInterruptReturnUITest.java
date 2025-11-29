package com.adventure;

import com.adventure.ui.BattleUI;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

import javax.swing.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class BattleInterruptReturnUITest {
    
    private Adventure loadAdventure(String resourceName) {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream inputStream = getClass().getResourceAsStream("/" + resourceName);
        return yaml.load(inputStream);
    }
    
    @Test
    public void testBattleResumeUIRestoresState() throws Exception {
        Adventure adventure = loadAdventure("sample-with-interrupt-return-ui-test.yaml");
        GameController controller = new GameController(adventure);
        
        JTextArea textArea = new JTextArea();
        JPanel buttonPanel = new JPanel();
        
        Hero hero = controller.getHero();
        Enemy enemy = new Enemy("Test Enemy", 8, 12);
        Battle battle = new Battle(hero, java.util.Arrays.asList(enemy));
        
        // Simulate battle progress
        battle.executeTurn();
        enemy.setStamina(8); // Reduced from 12
        battle.appendToBattleLog("Turn 1 completed\n");
        
        Map<String, Object> battleData = new HashMap<>();
        battleData.put("battle", new HashMap<>());
        
        // Save battle state
        controller.saveBattleState(battle, battleData);
        
        SwingUtilities.invokeAndWait(() -> {
            GameWindow gameWindow = new GameWindow(adventure);
            
            BattleUI battleUI = new BattleUI(textArea, buttonPanel, controller, gameWindow, () -> {});
            
            // Resume battle
            Battle savedBattle = controller.getSavedBattle();
            Map<String, Object> savedData = controller.getSavedBattleActionData();
            
            JPanel battlePanel = battleUI.resume(savedBattle, savedData);
            assertNotNull(battlePanel);
            
            // Verify battle log contains resumed message
            String displayedText = textArea.getText();
            assertTrue(displayedText.contains("Battle resumed!"), "Battle log should contain 'Battle resumed!'");
            assertTrue(displayedText.contains("Turn 1 completed"), "Battle log should contain previous turns");
            
            // Verify enemy stamina is displayed correctly
            assertEquals(8, savedBattle.getEnemies().get(0).getStamina());
        });
    }
    
    @Test
    public void testBattleStatePreservedThroughSaveRestore() throws Exception {
        Adventure adventure = loadAdventure("sample-with-interrupt-return-ui-test.yaml");
        GameController controller = new GameController(adventure);
        
        // Fixed random for predictable results
        Random fixedRandom = new Random() {
            @Override
            public int nextInt(int bound) {
                return 5; // Always roll 6
            }
        };
        
        Hero hero = controller.getHero();
        Enemy enemy = new Enemy("Weak Monster", 5, 10);
        Battle battle = new Battle(hero, java.util.Arrays.asList(enemy), fixedRandom, 0);
        
        // Execute turn - hero should win
        battle.executeTurn();
        int staminaAfterTurn = enemy.getStamina();
        assertEquals(8, staminaAfterTurn, "Enemy should have 8 stamina after losing one turn");
        
        // Save battle state
        Map<String, Object> battleData = new HashMap<>();
        controller.saveBattleState(battle, battleData);
        
        // Verify saved battle preserves state
        Battle savedBattle = controller.getSavedBattle();
        assertNotNull(savedBattle);
        assertEquals(8, savedBattle.getEnemies().get(0).getStamina(), "Saved battle should preserve enemy stamina");
        assertEquals(1, savedBattle.getCurrentTurn(), "Saved battle should preserve turn count");
        
        // Create UI with saved battle
        SwingUtilities.invokeAndWait(() -> {
            JTextArea textArea = new JTextArea();
            JPanel buttonPanel = new JPanel();
            GameWindow gameWindow = new GameWindow(adventure);
            
            BattleUI battleUI = new BattleUI(textArea, buttonPanel, controller, gameWindow, () -> {});
            JPanel battlePanel = battleUI.resume(savedBattle, battleData);
            
            assertNotNull(battlePanel, "Battle panel should be created");
            
            // Verify UI shows correct state
            String battleLog = textArea.getText();
            assertTrue(battleLog.contains("Battle resumed!"), "Battle log should show resumed message");
        });
    }
}
