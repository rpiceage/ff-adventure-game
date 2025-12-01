package com.adventure;

import com.adventure.*;
import com.adventure.interrupts.*;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class TurnLostInterruptFlowTest {
    
    @Test
    public void testBattleInterruptsAfterEnemyWinsOnce() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        Enemy enemy = new Enemy("Captain", 11, 10);
        
        // Fixed random: enemy wins first turn
        Random fixedRandom = new Random() {
            private int callCount = 0;
            
            @Override
            public int nextInt(int bound) {
                callCount++;
                // First 2 calls: hero dice (low rolls)
                if (callCount <= 2) return 0; // 1, 1
                // Next 2 calls: enemy dice (high rolls)
                return 5; // 6, 6
            }
        };
        
        Battle battle = new Battle(hero, Arrays.asList(enemy), fixedRandom, 0);
        TurnLostInterrupt interrupt = new TurnLostInterrupt(1, 393);
        battle.setInterrupt(interrupt);
        
        // Execute turn - enemy should win
        battle.executeTurn();
        
        assertTrue(battle.enemyDealtDamageThisTurn());
        
        // Check interrupt - should trigger immediately
        boolean triggered = battle.checkInterrupt();
        assertTrue(triggered);
        assertTrue(battle.wasInterrupted());
        assertEquals(393, battle.getInterrupt().getChapter());
    }
    
    @Test
    public void testBattleContinuesWhenHeroWins() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        Enemy enemy = new Enemy("Captain", 11, 10);
        
        // Fixed random: hero wins first turn
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
        TurnLostInterrupt interrupt = new TurnLostInterrupt(1, 393);
        battle.setInterrupt(interrupt);
        
        // Execute turn - hero should win
        battle.executeTurn();
        
        assertFalse(battle.enemyDealtDamageThisTurn());
        
        // Check interrupt - should not trigger
        boolean triggered = battle.checkInterrupt();
        assertFalse(triggered);
        assertFalse(battle.wasInterrupted());
    }
    
    @Test
    public void testBattleInterruptsAfterMultipleEnemyWins() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        Enemy enemy = new Enemy("Captain", 11, 20);
        
        // Fixed random: hero wins first turn, enemy wins second turn
        Random fixedRandom = new Random() {
            private int callCount = 0;
            
            @Override
            public int nextInt(int bound) {
                callCount++;
                int turnCall = (callCount - 1) % 4;
                // First turn: hero wins (high, low)
                if (callCount <= 2) return 5; // hero: 6, 6
                if (callCount <= 4) return 0; // enemy: 1, 1
                // Second turn: enemy wins (low, high)
                if (turnCall < 2) return 0; // hero: 1, 1
                return 5; // enemy: 6, 6
            }
        };
        
        Battle battle = new Battle(hero, Arrays.asList(enemy), fixedRandom, 0);
        TurnLostInterrupt interrupt = new TurnLostInterrupt(1, 393);
        battle.setInterrupt(interrupt);
        
        // First turn - hero wins
        battle.executeTurn();
        assertFalse(battle.enemyDealtDamageThisTurn());
        assertFalse(battle.checkInterrupt());
        
        // Second turn - enemy wins, interrupt triggers
        battle.executeTurn();
        assertTrue(battle.enemyDealtDamageThisTurn());
        assertTrue(battle.checkInterrupt());
        assertTrue(battle.wasInterrupted());
    }
    
    @Test
    public void testTurnLostCountsMultipleWins() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        Enemy enemy = new Enemy("Captain", 11, 20);
        
        // Fixed random: enemy wins first 2 turns
        Random fixedRandom = new Random() {
            private int callCount = 0;
            
            @Override
            public int nextInt(int bound) {
                callCount++;
                int turnCall = (callCount - 1) % 4;
                // Pattern: hero low, enemy high
                if (turnCall < 2) return 0; // hero: 1, 1
                return 5; // enemy: 6, 6
            }
        };
        
        Battle battle = new Battle(hero, Arrays.asList(enemy), fixedRandom, 0);
        TurnLostInterrupt interrupt = new TurnLostInterrupt(2, 393);
        battle.setInterrupt(interrupt);
        
        // First turn - enemy wins, but need 2 wins
        battle.executeTurn();
        assertTrue(battle.enemyDealtDamageThisTurn());
        // Don't check interrupt yet - it needs 2 wins
        
        // Second turn - enemy wins again, interrupt triggers
        battle.executeTurn();
        assertTrue(battle.enemyDealtDamageThisTurn());
        assertTrue(battle.checkInterrupt());
        assertTrue(battle.wasInterrupted());
    }
    
    @Test
    public void testNoUINeeded() {
        TurnLostInterrupt interrupt = new TurnLostInterrupt(1, 393);
        assertFalse(interrupt.needsUI());
    }
    
    @Test
    public void testIsVictoryReturnsFalse() {
        TurnLostInterrupt interrupt = new TurnLostInterrupt(1, 393);
        assertFalse(interrupt.isVictory());
    }
}
