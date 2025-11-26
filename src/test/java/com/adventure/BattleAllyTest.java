package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class BattleAllyTest {
    
    private static class FixedRandom extends Random {
        private final int[] values;
        private int index = 0;
        
        public FixedRandom(int... values) {
            this.values = values;
        }
        
        @Override
        public int nextInt(int bound) {
            return values[index++ % values.length];
        }
    }
    
    @Test
    public void testAlly_Dies_HeroContinues() {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-ally.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        Hero hero = new Hero(12, 24, 12);
        Enemy dragon = new Enemy("White Dragon", 12, 14);
        
        // Ally phase (5 turns to kill ally): ally rolls 1,1 enemy rolls 6,6
        // Hero phase (7 turns to kill dragon): hero rolls 6,6 enemy rolls 1,1
        Random fixedRandom = new FixedRandom(
            // Turn 1: ally 1,1 vs enemy 6,6
            1, 1, 6, 6,
            // Turn 2: ally 1,1 vs enemy 6,6
            1, 1, 6, 6,
            // Turn 3: ally 1,1 vs enemy 6,6
            1, 1, 6, 6,
            // Turn 4: ally 1,1 vs enemy 6,6
            1, 1, 6, 6,
            // Turn 5: ally 1,1 vs enemy 6,6 (ally dies)
            1, 1, 6, 6,
            // Turn 6: hero 6,6 vs enemy 1,1
            6, 6, 1, 1,
            // Turn 7: hero 6,6 vs enemy 1,1
            6, 6, 1, 1,
            // Turn 8: hero 6,6 vs enemy 1,1
            6, 6, 1, 1,
            // Turn 9: hero 6,6 vs enemy 1,1
            6, 6, 1, 1,
            // Turn 10: hero 6,6 vs enemy 1,1
            6, 6, 1, 1,
            // Turn 11: hero 6,6 vs enemy 1,1
            6, 6, 1, 1,
            // Turn 12: hero 6,6 vs enemy 1,1 (dragon dies)
            6, 6, 1, 1
        );
        
        Battle battle = new Battle(hero, java.util.Arrays.asList(dragon), fixedRandom, 0);
        battle.setAlly("Knight", 9, 10);
        
        assertTrue(battle.isAllyPhase());
        assertNotNull(battle.getAlly());
        assertEquals(10, battle.getAlly().getStamina());
        
        // Turn 1: Ally 9+1+1=11 vs Dragon 12+6+6=24 -> Ally loses 2 (8 left)
        battle.executeTurn();
        assertEquals(8, battle.getAlly().getStamina());
        assertEquals(14, dragon.getStamina());
        assertTrue(battle.isAllyPhase());
        
        // Turn 2: Ally loses 2 (6 left)
        battle.executeTurn();
        assertEquals(6, battle.getAlly().getStamina());
        
        // Turn 3: Ally loses 2 (4 left)
        battle.executeTurn();
        assertEquals(4, battle.getAlly().getStamina());
        
        // Turn 4: Ally loses 2 (2 left)
        battle.executeTurn();
        assertEquals(2, battle.getAlly().getStamina());
        
        // Turn 5: Ally loses 2 (0 left, dies)
        battle.executeTurn();
        assertEquals(0, battle.getAlly().getStamina());
        assertFalse(battle.isAllyPhase()); // Ally phase ends
        assertFalse(battle.isOver()); // Battle continues with hero
        
        // Turn 6: Hero 12+6+6=24 vs Dragon 12+1+1=14 -> Dragon loses 2 (12 left)
        battle.executeTurn();
        assertEquals(12, dragon.getStamina());
        assertEquals(24, hero.getStamina());
        
        // Turns 7-12: Hero keeps winning, dragon loses 2 each turn
        battle.executeTurn();
        assertEquals(10, dragon.getStamina());
        
        battle.executeTurn();
        assertEquals(8, dragon.getStamina());
        
        battle.executeTurn();
        assertEquals(6, dragon.getStamina());
        
        battle.executeTurn();
        assertEquals(4, dragon.getStamina());
        
        battle.executeTurn();
        assertEquals(2, dragon.getStamina());
        
        battle.executeTurn();
        assertEquals(0, dragon.getStamina());
        
        assertTrue(battle.isOver());
        assertTrue(battle.heroWon());
        assertEquals(24, hero.getStamina()); // Hero never took damage
    }
    
    @Test
    public void testAlly_Wins_BattleEnds() {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-ally.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        Hero hero = new Hero(12, 24, 12);
        Enemy dragon = new Enemy("White Dragon", 12, 14);
        
        // Ally wins all turns: ally rolls 6,6 enemy rolls 1,1
        Random fixedRandom = new FixedRandom(
            6, 6, 1, 1,  // Turn 1
            6, 6, 1, 1,  // Turn 2
            6, 6, 1, 1,  // Turn 3
            6, 6, 1, 1,  // Turn 4
            6, 6, 1, 1,  // Turn 5
            6, 6, 1, 1,  // Turn 6
            6, 6, 1, 1   // Turn 7 (dragon dies)
        );
        
        Battle battle = new Battle(hero, java.util.Arrays.asList(dragon), fixedRandom, 0);
        battle.setAlly("Knight", 9, 10);
        
        assertTrue(battle.isAllyPhase());
        assertEquals(10, battle.getAlly().getStamina());
        
        // Ally wins: 9+6+6=21 vs 12+1+1=14 -> Dragon loses 2 each turn
        for (int i = 0; i < 7; i++) {
            battle.executeTurn();
            assertTrue(battle.isAllyPhase() || battle.isOver());
        }
        
        assertTrue(battle.isOver());
        assertTrue(battle.heroWon());
        assertEquals(0, dragon.getStamina());
        assertEquals(10, battle.getAlly().getStamina()); // Ally survives
        assertEquals(24, hero.getStamina()); // Hero never fought
    }
}
