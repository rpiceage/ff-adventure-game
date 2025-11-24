package com.adventure;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class BattleEscapeTest {
    
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
    public void testEscapeNotAvailableBeforeTurn() {
        Hero hero = new Hero(12, 24, 12);
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Enemy("Warrior", 9, 10));
        
        Battle battle = new Battle(hero, enemies, new Random(), 0);
        battle.setEscapeTurn(2);
        
        // Turn 0: escape not available
        assertFalse(battle.canEscape());
        assertEquals(0, battle.getCurrentTurn());
        
        // Turn 1: still not available
        battle.executeTurn();
        assertFalse(battle.canEscape());
        assertEquals(1, battle.getCurrentTurn());
    }
    
    @Test
    public void testEscapeAvailableAfterSpecifiedTurn() {
        Hero hero = new Hero(12, 24, 12);
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Enemy("Warrior", 9, 10));
        
        // 4 dice per turn (2 hero + 2 enemy) * 3 turns = 12 dice
        FixedRandom random = new FixedRandom(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3);
        Battle battle = new Battle(hero, enemies, random, 0);
        battle.setEscapeTurn(2);
        
        // Turn 1
        battle.executeTurn();
        assertFalse(battle.canEscape());
        
        // Turn 2: escape becomes available
        battle.executeTurn();
        assertTrue(battle.canEscape());
        assertEquals(2, battle.getCurrentTurn());
        
        // Turn 3: still available
        battle.executeTurn();
        assertTrue(battle.canEscape());
        assertEquals(3, battle.getCurrentTurn());
    }
    
    @Test
    public void testEscapeNotAvailableWhenNotSet() {
        Hero hero = new Hero(12, 24, 12);
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Enemy("Warrior", 9, 10));
        
        Battle battle = new Battle(hero, enemies, new Random(), 0);
        // No escape turn set
        
        battle.executeTurn();
        battle.executeTurn();
        battle.executeTurn();
        
        assertFalse(battle.canEscape());
    }
}
