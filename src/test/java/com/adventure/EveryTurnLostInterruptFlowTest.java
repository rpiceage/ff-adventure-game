package com.adventure;

import com.adventure.*;
import com.adventure.interrupts.*;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class EveryTurnLostInterruptFlowTest {
    
    @Test
    public void testBattleInterruptsWhenEnemyWinsAndRollMatches() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        Enemy enemy = new Enemy("Monk", 11, 8);
        
        // Fixed random: enemy wins turn, then interrupt roll is 5
        Random fixedRandom = new Random() {
            private int callCount = 0;
            
            @Override
            public int nextInt(int bound) {
                callCount++;
                // First 2 calls: hero dice (low rolls so hero loses)
                if (callCount <= 2) return 0; // 1, 1
                // Next 2 calls: enemy dice (high rolls so enemy wins)
                if (callCount <= 4) return 5; // 6, 6
                // 5th call: interrupt roll
                return 4; // 5 (matches trigger)
            }
        };
        
        Battle battle = new Battle(hero, Arrays.asList(enemy), fixedRandom, 0);
        EveryTurnLostInterrupt interrupt = new EveryTurnLostInterrupt(1, Arrays.asList(5, 6), 366, fixedRandom);
        battle.setInterrupt(interrupt);
        
        // Execute turn - enemy should win
        battle.executeTurn();
        
        assertTrue(battle.enemyDealtDamageThisTurn());
        assertTrue(battle.needsInterruptUI());
        
        // Check interrupt - should trigger
        boolean triggered = battle.checkInterrupt();
        assertTrue(triggered);
        assertTrue(battle.wasInterrupted());
        assertEquals(366, battle.getInterrupt().getChapter());
    }
    
    @Test
    public void testBattleContinuesWhenRollDoesNotMatch() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        Enemy enemy = new Enemy("Monk", 11, 8);
        
        // Fixed random: enemy wins turn, but interrupt roll is 3
        Random fixedRandom = new Random() {
            private int callCount = 0;
            
            @Override
            public int nextInt(int bound) {
                callCount++;
                // First 2 calls: hero dice (low rolls)
                if (callCount <= 2) return 0; // 1, 1
                // Next 2 calls: enemy dice (high rolls)
                if (callCount <= 4) return 5; // 6, 6
                // 5th call: interrupt roll
                return 2; // 3 (does not match trigger)
            }
        };
        
        Battle battle = new Battle(hero, Arrays.asList(enemy), fixedRandom, 0);
        EveryTurnLostInterrupt interrupt = new EveryTurnLostInterrupt(1, Arrays.asList(5, 6), 366, fixedRandom);
        battle.setInterrupt(interrupt);
        
        // Execute turn - enemy should win
        battle.executeTurn();
        
        assertTrue(battle.enemyDealtDamageThisTurn());
        assertTrue(battle.needsInterruptUI());
        
        // Check interrupt - should not trigger
        boolean triggered = battle.checkInterrupt();
        assertFalse(triggered);
        assertFalse(battle.wasInterrupted());
        
        // Battle should continue - can execute another turn
        assertFalse(battle.isOver());
    }
    
    @Test
    public void testNoInterruptUIWhenHeroWins() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        Enemy enemy = new Enemy("Monk", 11, 8);
        
        // Fixed random: hero wins turn
        Random fixedRandom = new Random() {
            private int callCount = 0;
            
            @Override
            public int nextInt(int bound) {
                callCount++;
                // First 2 calls: hero dice (high rolls)
                if (callCount <= 2) return 5; // 6, 6
                // Next 2 calls: enemy dice (low rolls)
                return 0; // 1, 1
            }
        };
        
        Battle battle = new Battle(hero, Arrays.asList(enemy), fixedRandom, 0);
        EveryTurnLostInterrupt interrupt = new EveryTurnLostInterrupt(1, Arrays.asList(5, 6), 366, fixedRandom);
        battle.setInterrupt(interrupt);
        
        // Execute turn - hero should win
        battle.executeTurn();
        
        assertFalse(battle.enemyDealtDamageThisTurn());
        assertFalse(battle.needsInterruptUI());
    }
    
    @Test
    public void testInterruptChecksEveryEnemyWinUntilTriggered() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        Enemy enemy = new Enemy("Monk", 11, 20);
        
        // Fixed random: enemy wins first turn (roll 3, no trigger), wins second turn (roll 5, triggers)
        Random fixedRandom = new Random() {
            private int callCount = 0;
            
            @Override
            public int nextInt(int bound) {
                callCount++;
                int turnCall = (callCount - 1) % 5;
                // Pattern: hero low, enemy high, interrupt roll
                if (turnCall < 2) return 0; // hero: 1, 1
                if (turnCall < 4) return 5; // enemy: 6, 6
                // First interrupt: 3 (no trigger), second interrupt: 5 (triggers)
                return (callCount <= 5) ? 2 : 4;
            }
        };
        
        Battle battle = new Battle(hero, Arrays.asList(enemy), fixedRandom, 0);
        EveryTurnLostInterrupt interrupt = new EveryTurnLostInterrupt(1, Arrays.asList(5, 6), 366, fixedRandom);
        battle.setInterrupt(interrupt);
        
        // First turn - enemy wins, interrupt roll is 3 (no trigger)
        battle.executeTurn();
        assertTrue(battle.needsInterruptUI());
        boolean triggered1 = battle.checkInterrupt();
        assertFalse(triggered1);
        assertFalse(battle.wasInterrupted());
        
        // Second turn - enemy wins again, interrupt roll is 5 (triggers)
        battle.executeTurn();
        assertTrue(battle.needsInterruptUI());
        boolean triggered2 = battle.checkInterrupt();
        assertTrue(triggered2);
        assertTrue(battle.wasInterrupted());
    }
}
