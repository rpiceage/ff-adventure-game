package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

import java.io.InputStream;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ExtraAttributeDamageTest {
    
    @Test
    public void testExtraSkillDamageOnEnemyHit() {
        // Load adventure
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-extra-attribute-damage.yaml");
        Adventure adventure = yaml.load(input);
        GameController controller = new GameController(adventure);
        
        Hero hero = controller.getHero();
        int initialSkill = hero.getSkill();
        int initialStamina = hero.getStamina();
        
        // Create enemy
        Enemy enemy = new Enemy("Skill Drainer", 8, 6);
        
        // Fixed random where enemy wins first turn
        Random fixedRandom = new Random() {
            private int callCount = 0;
            @Override
            public int nextInt(int bound) {
                if (bound == 6) {
                    callCount++;
                    // Turn 1: Hero rolls 1+1=2, Enemy rolls 5+5=10 (enemy wins)
                    // Turn 2: Hero rolls 5+5=10, Enemy rolls 1+1=2 (hero wins)
                    // Turn 3: Hero rolls 5+5=10, Enemy rolls 1+1=2 (hero wins)
                    // Turn 4: Hero rolls 5+5=10, Enemy rolls 1+1=2 (hero wins)
                    return new int[]{1, 1, 5, 5,  // Turn 1
                                     5, 5, 1, 1,  // Turn 2
                                     5, 5, 1, 1,  // Turn 3
                                     5, 5, 1, 1}[(callCount - 1) % 16];
                }
                return super.nextInt(bound);
            }
        };
        
        Battle battle = new Battle(hero, List.of(enemy), fixedRandom, 0);
        
        // Set extra skill damage
        battle.setExtraAttributeDamage(1, null, null);
        
        // Turn 1: Enemy wins
        battle.executeTurn();
        
        // Hero: skill=12 + dice=2 = 14
        // Enemy: skill=8 + dice=10 = 18
        // Enemy wins, hero takes 2 STAMINA + 1 SKILL damage
        assertEquals(initialStamina - 2, hero.getStamina());
        assertEquals(initialSkill - 1, hero.getSkill()); // Extra skill damage applied!
        
        // Turn 2: Hero wins
        battle.executeTurn();
        
        // Hero: skill=11 + dice=10 = 21
        // Enemy: skill=8 + dice=2 = 10
        // Hero wins, enemy takes 2 damage
        assertEquals(initialStamina - 2, hero.getStamina()); // No change
        assertEquals(initialSkill - 1, hero.getSkill()); // No change
        assertEquals(4, enemy.getStamina());
        
        // Turn 3: Hero wins
        battle.executeTurn();
        assertEquals(2, enemy.getStamina());
        
        // Turn 4: Hero wins, enemy dies
        battle.executeTurn();
        assertEquals(0, enemy.getStamina());
        assertTrue(battle.isOver());
        assertTrue(battle.heroWon());
        
        // Final check: hero lost 1 skill from the one enemy hit
        assertEquals(initialSkill - 1, hero.getSkill());
        assertEquals(initialStamina - 2, hero.getStamina());
    }
    
    @Test
    public void testExtraStaminaDamageOnEnemyHit() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        Enemy enemy = new Enemy("Stamina Drainer", 8, 6);
        
        // Fixed random where enemy wins
        Random fixedRandom = new Random() {
            private int callCount = 0;
            @Override
            public int nextInt(int bound) {
                if (bound == 6) {
                    callCount++;
                    return new int[]{1, 1, 5, 5}[(callCount - 1) % 4];
                }
                return super.nextInt(bound);
            }
        };
        
        Battle battle = new Battle(hero, List.of(enemy), fixedRandom, 0);
        
        // Set extra stamina damage (2 normal + 3 extra = 5 total)
        battle.setExtraAttributeDamage(null, 3, null);
        
        battle.executeTurn();
        
        // Hero takes 2 normal + 3 extra = 5 stamina damage
        assertEquals(19, hero.getStamina());
    }
    
    @Test
    public void testExtraLuckDamageOnEnemyHit() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        Enemy enemy = new Enemy("Luck Drainer", 8, 6);
        
        // Fixed random where enemy wins
        Random fixedRandom = new Random() {
            private int callCount = 0;
            @Override
            public int nextInt(int bound) {
                if (bound == 6) {
                    callCount++;
                    return new int[]{1, 1, 5, 5}[(callCount - 1) % 4];
                }
                return super.nextInt(bound);
            }
        };
        
        Battle battle = new Battle(hero, List.of(enemy), fixedRandom, 0);
        
        // Set extra luck damage
        battle.setExtraAttributeDamage(null, null, 2);
        
        battle.executeTurn();
        
        // Hero takes 2 stamina + 2 luck damage
        assertEquals(22, hero.getStamina());
        assertEquals(10, hero.getLuck());
    }
    
    @Test
    public void testMultipleAttributeDamageOnEnemyHit() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        Enemy enemy = new Enemy("Multi Drainer", 8, 6);
        
        // Fixed random where enemy wins
        Random fixedRandom = new Random() {
            private int callCount = 0;
            @Override
            public int nextInt(int bound) {
                if (bound == 6) {
                    callCount++;
                    return new int[]{1, 1, 5, 5}[(callCount - 1) % 4];
                }
                return super.nextInt(bound);
            }
        };
        
        Battle battle = new Battle(hero, List.of(enemy), fixedRandom, 0);
        
        // Set multiple extra damages
        battle.setExtraAttributeDamage(1, 1, 1);
        
        battle.executeTurn();
        
        // Hero takes 2 stamina (normal) + 1 stamina (extra) + 1 skill + 1 luck
        assertEquals(21, hero.getStamina()); // 24 - 2 - 1
        assertEquals(11, hero.getSkill());   // 12 - 1
        assertEquals(11, hero.getLuck());    // 12 - 1
    }
    
    @Test
    public void testNoExtraDamageWhenHeroWins() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        Enemy enemy = new Enemy("Skill Drainer", 8, 6);
        
        // Fixed random where hero wins
        Random fixedRandom = new Random() {
            private int callCount = 0;
            @Override
            public int nextInt(int bound) {
                if (bound == 6) {
                    callCount++;
                    return new int[]{5, 5, 1, 1}[(callCount - 1) % 4];
                }
                return super.nextInt(bound);
            }
        };
        
        Battle battle = new Battle(hero, List.of(enemy), fixedRandom, 0);
        
        // Set extra skill damage
        battle.setExtraAttributeDamage(1, null, null);
        
        battle.executeTurn();
        
        // Hero wins, no damage taken
        assertEquals(24, hero.getStamina());
        assertEquals(12, hero.getSkill()); // No skill loss
        assertEquals(4, enemy.getStamina());
    }
}
