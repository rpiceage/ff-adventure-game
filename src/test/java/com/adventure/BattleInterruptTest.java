package com.adventure;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class BattleInterruptTest {
    
    private static class FixedRandom extends Random {
        private int[] values;
        private int index = 0;

        public FixedRandom(int... values) {
            this.values = values;
        }

        @Override
        public int nextInt(int bound) {
            return values[index++] - 1;
        }
    }
    
    @Test
    public void testBattleInterruptWhenEnemyReachesThreshold() {
        Hero hero = new Hero(12, 24, 12);
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Enemy("Warrior", 7, 8));
        
        // Hero always wins to reduce enemy stamina
        FixedRandom random = new FixedRandom(6, 6, 2, 2, 6, 6, 2, 2, 6, 6, 2, 2);
        Battle battle = new Battle(hero, enemies, random, 0);
        battle.setInterruptStamina(3);
        
        assertFalse(battle.isOver());
        assertFalse(battle.wasInterrupted());
        
        // Turn 1: Enemy stamina 8 -> 6
        battle.executeTurn();
        assertFalse(battle.isOver());
        assertFalse(battle.wasInterrupted());
        assertEquals(6, enemies.get(0).getStamina());
        
        // Turn 2: Enemy stamina 6 -> 4
        battle.executeTurn();
        assertFalse(battle.isOver());
        assertFalse(battle.wasInterrupted());
        assertEquals(4, enemies.get(0).getStamina());
        
        // Turn 3: Enemy stamina 4 -> 2 (at or below threshold of 3)
        battle.executeTurn();
        assertTrue(battle.isOver(), "Battle should be over after interrupt");
        assertTrue(battle.wasInterrupted(), "Battle should be interrupted");
        assertTrue(battle.heroWon(), "Hero should win when interrupted");
        assertEquals(2, enemies.get(0).getStamina());
    }
    
    @Test
    public void testBattleInterruptExactlyAtThreshold() {
        Hero hero = new Hero(12, 24, 12);
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Enemy("Warrior", 7, 7));
        
        FixedRandom random = new FixedRandom(6, 6, 2, 2, 6, 6, 2, 2);
        Battle battle = new Battle(hero, enemies, random, 0);
        battle.setInterruptStamina(3);
        
        // Turn 1: Enemy stamina 7 -> 5
        battle.executeTurn();
        assertFalse(battle.wasInterrupted());
        
        // Turn 2: Enemy stamina 5 -> 3 (exactly at threshold)
        battle.executeTurn();
        assertTrue(battle.isOver());
        assertTrue(battle.wasInterrupted());
        assertEquals(3, enemies.get(0).getStamina());
    }
    
    @Test
    public void testBattleWithoutInterruptContinuesToDeath() {
        Hero hero = new Hero(12, 24, 12);
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Enemy("Warrior", 7, 4));
        
        FixedRandom random = new FixedRandom(6, 6, 2, 2, 6, 6, 2, 2);
        Battle battle = new Battle(hero, enemies, random, 0);
        // No interrupt set
        
        // Turn 1: Enemy stamina 4 -> 2
        battle.executeTurn();
        assertFalse(battle.isOver());
        
        // Turn 2: Enemy stamina 2 -> 0 (dead)
        battle.executeTurn();
        assertTrue(battle.isOver());
        assertFalse(battle.wasInterrupted());
        assertTrue(battle.heroWon());
        assertEquals(0, enemies.get(0).getStamina());
    }
}
