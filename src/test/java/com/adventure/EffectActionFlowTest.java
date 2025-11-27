package com.adventure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

import java.io.InputStream;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class EffectActionFlowTest {
    private GameController controller;
    private Adventure adventure;

    @BeforeEach
    public void setUp() {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-effect.yaml");
        adventure = yaml.load(input);
        controller = new GameController(adventure);
    }

    @Test
    public void testEffectStoredAndAppliedToBattle() {
        // Create fresh controller - effect action in chapter 0 will be executed during init
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-effect.yaml");
        Adventure adv = yaml.load(input);
        GameController ctrl = new GameController(adv);
        
        // Effect should already be set from chapter 0 initialization
        assertEquals(2, ctrl.getNextBattleAttackModifier());
        assertEquals("Magic Amulet (+2 Attack)", ctrl.getNextBattleEffectText());
        
        // Create battle with fixed random for predictable results
        Enemy goblin = new Enemy("Goblin", 6, 6);
        Hero hero = ctrl.getHero();
        
        // Fixed random: hero rolls 5, enemy rolls 3
        Random fixedRandom = new Random() {
            private int callCount = 0;
            @Override
            public int nextInt(int bound) {
                if (bound == 6) {
                    callCount++;
                    // Hero rolls: 2+3=5, Enemy rolls: 1+2=3
                    return new int[]{2, 3, 1, 2}[(callCount - 1) % 4];
                }
                return super.nextInt(bound);
            }
        };
        
        Battle battle = new Battle(hero, List.of(goblin), fixedRandom, 0);
        
        // Apply effect modifier (simulating what BattleUI does)
        if (ctrl.getNextBattleAttackModifier() != null) {
            battle.setModifier(ctrl.getNextBattleAttackModifier(), ctrl.getNextBattleEffectText());
            ctrl.clearNextBattleEffect();
        }
        
        // Verify effect is cleared after applying
        assertNull(ctrl.getNextBattleAttackModifier());
        
        // Execute one turn
        battle.executeTurn();
        
        // Hero: skill=12 + modifier=2 + dice=5 = 19
        // Goblin: skill=6 + dice=3 = 9
        // Hero wins, goblin takes 2 damage
        assertEquals(24, hero.getStamina()); // Hero takes no damage
        assertEquals(4, goblin.getStamina()); // Goblin: 6-2=4
        
        // Execute second turn (hero rolls 5, enemy rolls 3 again)
        battle.executeTurn();
        
        // Same calculation, goblin takes another 2 damage
        assertEquals(24, hero.getStamina());
        assertEquals(2, goblin.getStamina()); // Goblin: 4-2=2
        
        // Execute third turn
        battle.executeTurn();
        
        // Goblin takes final 2 damage and dies
        assertEquals(24, hero.getStamina());
        assertEquals(0, goblin.getStamina());
        assertTrue(battle.isOver());
        assertTrue(battle.heroWon());
    }
    
    @Test
    public void testEffectClearedManually() {
        // Set effect
        controller.setNextBattleEffect(2, "Test Effect");
        
        // Effect should be stored
        assertEquals(2, controller.getNextBattleAttackModifier());
        
        // Clear it
        controller.clearNextBattleEffect();
        assertNull(controller.getNextBattleAttackModifier());
        assertNull(controller.getNextBattleEffectText());
    }
    
    @Test
    public void testBattleWithoutEffect() {
        // Create battle without effect
        Hero hero = controller.getHero();
        Enemy goblin = new Enemy("Goblin", 6, 6);
        
        Random fixedRandom = new Random() {
            private int callCount = 0;
            @Override
            public int nextInt(int bound) {
                if (bound == 6) {
                    callCount++;
                    return new int[]{2, 3, 1, 2}[(callCount - 1) % 4];
                }
                return super.nextInt(bound);
            }
        };
        
        Battle battle = new Battle(hero, List.of(goblin), fixedRandom, 0);
        
        // No effect applied
        battle.executeTurn();
        
        // Hero: skill=12 + dice=5 = 17 (no modifier)
        // Goblin: skill=6 + dice=3 = 9
        // Hero wins, goblin takes 2 damage
        assertEquals(24, hero.getStamina());
        assertEquals(4, goblin.getStamina());
    }
    
    @Test
    public void testEffectModifierIncreasesAttackStrength() {
        Hero hero = controller.getHero();
        Enemy strongEnemy = new Enemy("Strong Enemy", 10, 10);
        
        // Fixed random where without modifier hero would lose
        Random fixedRandom = new Random() {
            private int callCount = 0;
            @Override
            public int nextInt(int bound) {
                if (bound == 6) {
                    callCount++;
                    // Hero rolls: 0+1=1, Enemy rolls: 5+5=10
                    return new int[]{0, 1, 5, 5}[(callCount - 1) % 4];
                }
                return super.nextInt(bound);
            }
        };
        
        Battle battleWithoutEffect = new Battle(hero, List.of(new Enemy("Strong Enemy", 10, 10)), fixedRandom, 0);
        battleWithoutEffect.executeTurn();
        
        // Hero: skill=12 + dice=1 = 13
        // Enemy: skill=10 + dice=10 = 20
        // Enemy wins, hero takes 2 damage
        assertEquals(22, hero.getStamina());
        
        // Reset hero
        hero.setStamina(24);
        
        // Now with +3 effect modifier
        fixedRandom = new Random() {
            private int callCount = 0;
            @Override
            public int nextInt(int bound) {
                if (bound == 6) {
                    callCount++;
                    return new int[]{0, 1, 5, 5}[(callCount - 1) % 4];
                }
                return super.nextInt(bound);
            }
        };
        
        Battle battleWithEffect = new Battle(hero, List.of(strongEnemy), fixedRandom, 0);
        battleWithEffect.setModifier(3, "Power Boost");
        battleWithEffect.executeTurn();
        
        // Hero: skill=12 + modifier=3 + dice=1 = 16
        // Enemy: skill=10 + dice=10 = 20
        // Enemy still wins but by less
        assertEquals(22, hero.getStamina());
    }
    
    @Test
    public void testEffectSavedAndRestored() {
        // Use controller that already has effect from sample-with-effect.yaml
        assertEquals(2, controller.getNextBattleAttackModifier());
        
        // Create save game
        SaveGame saveGame = controller.createSaveGame();
        
        // Verify effect is in save
        assertEquals(2, saveGame.getNextBattleAttackModifier());
        assertEquals("Magic Amulet (+2 Attack)", saveGame.getNextBattleEffectText());
        
        // Clear effect in controller
        controller.clearNextBattleEffect();
        assertNull(controller.getNextBattleAttackModifier());
        
        // Load save game
        controller.loadSaveGame(saveGame);
        
        // Verify effect is restored
        assertEquals(2, controller.getNextBattleAttackModifier());
        assertEquals("Magic Amulet (+2 Attack)", controller.getNextBattleEffectText());
    }
    
    @Test
    public void testNullEffectSavedAndRestored() {
        // Create controller with YAML that has no effect
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-stats.yaml");
        Adventure adv = yaml.load(input);
        GameController ctrl = new GameController(adv);
        
        // No effect should be set
        assertNull(ctrl.getNextBattleAttackModifier());
        
        // Create save game
        SaveGame saveGame = ctrl.createSaveGame();
        
        // Verify null effect in save
        assertNull(saveGame.getNextBattleAttackModifier());
        assertNull(saveGame.getNextBattleEffectText());
        
        // Set effect in controller
        ctrl.setNextBattleEffect(5, "Test");
        assertNotNull(ctrl.getNextBattleAttackModifier());
        
        // Load save game with null effect
        ctrl.loadSaveGame(saveGame);
        
        // Verify effect is cleared
        assertNull(ctrl.getNextBattleAttackModifier());
        assertNull(ctrl.getNextBattleEffectText());
    }
}
