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
        battle.setInterrupt(new StaminaInterrupt(3));
        
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
        battle.setInterrupt(new StaminaInterrupt(3));
        
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
    
    @Test
    public void testBattleInterruptAfterTurns() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        Enemy enemy = new Enemy("Triceratops", 8, 30);
        
        Random fixedRandom = new Random() {
            @Override
            public int nextInt(int bound) {
                if (bound == 6) {
                    // Hero always wins
                    return 5;
                }
                return super.nextInt(bound);
            }
        };
        
        Battle battle = new Battle(hero, List.of(enemy), fixedRandom, 0);
        battle.setInterrupt(new TurnInterrupt(3));
        
        // Turn 1
        battle.executeTurn();
        assertFalse(battle.isOver());
        assertEquals(28, enemy.getStamina());
        
        // Turn 2
        battle.executeTurn();
        assertFalse(battle.isOver());
        assertEquals(26, enemy.getStamina());
        
        // Turn 3 - battle should end
        battle.executeTurn();
        assertTrue(battle.isOver());
        assertTrue(battle.wasInterrupted());
        assertEquals(24, enemy.getStamina()); // Enemy still alive
        assertEquals(24, hero.getStamina()); // Hero not hurt
    }
    
    @Test
    public void testBattleInterruptTurnWithHeroDamage() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        Enemy enemy = new Enemy("Strong Enemy", 10, 20);
        
        Random fixedRandom = new Random() {
            private int callCount = 0;
            @Override
            public int nextInt(int bound) {
                if (bound == 6) {
                    callCount++;
                    // Turn 1: Enemy wins, Turn 2: Hero wins, Turn 3: Enemy wins
                    return new int[]{1, 1, 5, 5,  // Turn 1
                                     5, 5, 1, 1,  // Turn 2
                                     1, 1, 5, 5}[(callCount - 1) % 12];
                }
                return super.nextInt(bound);
            }
        };
        
        Battle battle = new Battle(hero, List.of(enemy), fixedRandom, 0);
        battle.setInterrupt(new TurnInterrupt(3));
        
        // Turn 1: Enemy wins
        battle.executeTurn();
        assertEquals(22, hero.getStamina());
        
        // Turn 2: Hero wins
        battle.executeTurn();
        assertEquals(18, enemy.getStamina());
        
        // Turn 3: Battle ends (even though enemy would win)
        battle.executeTurn();
        assertTrue(battle.isOver());
        assertTrue(battle.wasInterrupted());
        assertEquals(20, hero.getStamina()); // Hero took damage in turn 3
        assertEquals(18, enemy.getStamina()); // Enemy still alive
    }
    
    @Test
    public void testConditionalInterruptOnHeroWin() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        Enemy enemy = new Enemy("Hawk", 10, 16);
        
        Random fixedRandom = new Random() {
            private int callCount = 0;
            @Override
            public int nextInt(int bound) {
                if (bound == 6) {
                    callCount++;
                    // Turn 1: Hero wins (dice 5+5=10), Enemy (dice 1+1=2), interrupt roll = 2 (no interrupt)
                    // Turn 2: Hero wins (dice 5+5=10), Enemy (dice 1+1=2), interrupt roll = 1 (interrupt!)
                    return new int[]{4, 4, 0, 0,  // Turn 1: hero wins (5+5 vs 1+1)
                                     1,           // Interrupt check: roll 2 (no interrupt)
                                     4, 4, 0, 0,  // Turn 2: hero wins (5+5 vs 1+1)
                                     0}[(callCount - 1) % 10]; // Interrupt check: roll 1 (interrupt!)
                }
                return super.nextInt(bound);
            }
        };
        
        Battle battle = new Battle(hero, List.of(enemy), fixedRandom, 0);
        battle.setInterrupt(new ConditionalInterrupt(1, List.of(1), 55, fixedRandom));
        
        // Turn 1: Hero wins, interrupt check rolls 2 (no interrupt)
        battle.executeTurn();
        assertFalse(battle.isOver());
        assertEquals(14, enemy.getStamina());
        
        // Manually check for interrupt (simulating UI button click)
        assertFalse(battle.checkInterrupt());
        
        // Turn 2: Hero wins, interrupt check rolls 1 (interrupt!)
        battle.executeTurn();
        assertFalse(battle.isOver()); // Not over yet, need to check interrupt
        
        // Manually check for interrupt (simulating UI button click)
        assertTrue(battle.checkInterrupt());
        assertTrue(battle.isOver());
        assertTrue(battle.wasInterrupted());
        assertEquals(55, battle.getInterrupt().getChapter());
        assertEquals(12, enemy.getStamina()); // Enemy still alive
        assertEquals(24, hero.getStamina()); // Hero not hurt
    }
    
    @Test
    public void testConditionalInterruptOnlyWhenHeroWins() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        Enemy enemy = new Enemy("Strong Enemy", 12, 16);
        
        Random fixedRandom = new Random() {
            private int callCount = 0;
            @Override
            public int nextInt(int bound) {
                if (bound == 6) {
                    callCount++;
                    // Turn 1: Enemy wins (no interrupt check should happen)
                    // Turn 2: Hero wins, interrupt roll = 1 (interrupt!)
                    return new int[]{0, 0, 4, 4,  // Turn 1: enemy wins (1+1 vs 5+5)
                                     4, 4, 0, 0,  // Turn 2: hero wins (5+5 vs 1+1)
                                     0}[(callCount - 1) % 9]; // Interrupt check: roll 1
                }
                return super.nextInt(bound);
            }
        };
        
        Battle battle = new Battle(hero, List.of(enemy), fixedRandom, 0);
        battle.setInterrupt(new ConditionalInterrupt(1, List.of(1), 55, fixedRandom));
        
        // Turn 1: Enemy wins (no interrupt check)
        battle.executeTurn();
        assertFalse(battle.isOver());
        assertEquals(22, hero.getStamina());
        
        // Turn 2: Hero wins, interrupt check triggers
        battle.executeTurn();
        assertFalse(battle.isOver()); // Not over yet
        
        // Manually check for interrupt (simulating UI button click)
        assertTrue(battle.checkInterrupt());
        assertTrue(battle.isOver());
        assertTrue(battle.wasInterrupted());
        assertEquals(55, battle.getInterrupt().getChapter());
    }
}
