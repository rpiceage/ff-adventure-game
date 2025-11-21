package com.adventure;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class BattleTest {

    private static class FixedRandom extends Random {
        private int[] values;
        private int index = 0;

        public FixedRandom(int... values) {
            this.values = values;
        }

        @Override
        public int nextInt(int bound) {
            // Return 0-based value (will be +1 in Battle code)
            return values[index++] - 1;
        }
    }

    @Test
    public void testHeroWinsTurn() {
        Hero hero = new Hero(10, 20, 10);
        FixedRandom random = new FixedRandom(5, 5, 2, 2); // Hero: 10+5+5=20, Enemy: 8+2+2=12
        Battle battle = new Battle(hero, "Goblin", 8, 10, random);

        battle.executeTurn();

        assertEquals(20, hero.getStamina());
        assertEquals(8, battle.getEnemyStamina());
        assertFalse(battle.isOver());
    }

    @Test
    public void testEnemyWinsTurn() {
        Hero hero = new Hero(10, 20, 10);
        FixedRandom random = new FixedRandom(2, 2, 5, 5); // Hero: 10+2+2=14, Enemy: 12+5+5=22
        Battle battle = new Battle(hero, "Orc", 12, 10, random);

        battle.executeTurn();

        assertEquals(18, hero.getStamina());
        assertEquals(10, battle.getEnemyStamina());
        assertFalse(battle.isOver());
    }

    @Test
    public void testDrawTurn() {
        Hero hero = new Hero(10, 20, 10);
        FixedRandom random = new FixedRandom(3, 3, 3, 3); // Hero: 10+3+3=16, Enemy: 10+3+3=16
        Battle battle = new Battle(hero, "Troll", 10, 10, random);

        battle.executeTurn();

        assertEquals(20, hero.getStamina());
        assertEquals(10, battle.getEnemyStamina());
        assertFalse(battle.isOver());
    }

    @Test
    public void testHeroWinsBattle() {
        Hero hero = new Hero(10, 20, 10);
        FixedRandom random = new FixedRandom(5, 5, 2, 2, 5, 5, 2, 2, 5, 5, 2, 2, 5, 5, 2, 2, 5, 5, 2, 2);
        Battle battle = new Battle(hero, "Weak Enemy", 8, 10, random);

        while (!battle.isOver()) {
            battle.executeTurn();
        }

        assertTrue(battle.heroWon());
        assertEquals(0, battle.getEnemyStamina());
        assertTrue(hero.getStamina() > 0);
    }

    @Test
    public void testHeroLosesBattle() {
        Hero hero = new Hero(10, 6, 10);
        FixedRandom random = new FixedRandom(2, 2, 5, 5, 2, 2, 5, 5, 2, 2, 5, 5);
        Battle battle = new Battle(hero, "Strong Enemy", 12, 20, random);

        while (!battle.isOver()) {
            battle.executeTurn();
        }

        assertFalse(battle.heroWon());
        assertEquals(0, hero.getStamina());
    }

    @Test
    public void testBattleLog() {
        Hero hero = new Hero(10, 20, 10);
        FixedRandom random = new FixedRandom(5, 5, 2, 2);
        Battle battle = new Battle(hero, "Enemy", 8, 10, random);

        battle.executeTurn();

        String log = battle.getBattleLog();
        assertTrue(log.contains("Hero:"));
        assertTrue(log.contains("Enemy:"));
        assertTrue(log.contains("loses 2 STAMINA"));
    }

    @Test
    public void testMultipleEnemiesBattle() {
        Hero hero = new Hero(10, 20, 10);
        java.util.List<Enemy> enemies = java.util.Arrays.asList(
            new Enemy("Goblin 1", 8, 4),
            new Enemy("Goblin 2", 8, 4)
        );
        // Hero wins vs both: 10+5+5=20 > 8+2+2=12
        FixedRandom random = new FixedRandom(5, 5, 2, 2, 5, 5, 2, 2);
        Battle battle = new Battle(hero, enemies, random);

        battle.executeTurn();

        assertEquals(20, hero.getStamina()); // Hero wins both, takes no damage
        assertEquals(2, enemies.get(0).getStamina()); // Selected enemy takes damage
        assertEquals(4, enemies.get(1).getStamina()); // Non-selected enemy takes no damage
    }

    @Test
    public void testHeroSelectsTarget() {
        Hero hero = new Hero(10, 20, 10);
        java.util.List<Enemy> enemies = java.util.Arrays.asList(
            new Enemy("Enemy 1", 8, 10),
            new Enemy("Enemy 2", 8, 10)
        );
        Battle battle = new Battle(hero, enemies);

        assertEquals(0, battle.getSelectedEnemyIndex());
        
        battle.setSelectedEnemy(1);
        assertEquals(1, battle.getSelectedEnemyIndex());
    }

    @Test
    public void testHeroTakesDamageFromMultipleEnemies() {
        Hero hero = new Hero(10, 20, 10);
        java.util.List<Enemy> enemies = java.util.Arrays.asList(
            new Enemy("Strong 1", 12, 10),
            new Enemy("Strong 2", 12, 10)
        );
        // Hero loses vs both: 10+2+2=14 < 12+5+5=22
        FixedRandom random = new FixedRandom(2, 2, 5, 5, 2, 2, 5, 5);
        Battle battle = new Battle(hero, enemies, random);

        battle.executeTurn();

        assertEquals(16, hero.getStamina()); // Takes 4 damage (2 per enemy)
        assertEquals(10, enemies.get(0).getStamina());
        assertEquals(10, enemies.get(1).getStamina());
    }

    @Test
    public void testHeroRollsSeparatelyAgainstEachEnemy() {
        Hero hero = new Hero(10, 20, 10);
        java.util.List<Enemy> enemies = java.util.Arrays.asList(
            new Enemy("Enemy 1", 8, 10),
            new Enemy("Enemy 2", 12, 10)
        );
        // Hero wins vs Enemy 1: 10+5+5=20 > 8+2+2=12
        // Hero loses vs Enemy 2: 10+2+2=14 < 12+5+5=22
        FixedRandom random = new FixedRandom(5, 5, 2, 2, 2, 2, 5, 5);
        Battle battle = new Battle(hero, enemies, random);

        battle.executeTurn();

        assertEquals(18, hero.getStamina()); // Takes 2 damage from Enemy 2
        assertEquals(8, enemies.get(0).getStamina()); // Selected enemy takes damage
        assertEquals(10, enemies.get(1).getStamina());
        
        // Verify dice were rolled separately
        assertEquals(5, enemies.get(0).getHeroDice1());
        assertEquals(5, enemies.get(0).getHeroDice2());
        assertEquals(2, enemies.get(1).getHeroDice1());
        assertEquals(2, enemies.get(1).getHeroDice2());
    }

    @Test
    public void testBattleEndsWhenAllEnemiesDead() {
        Hero hero = new Hero(10, 20, 10);
        java.util.List<Enemy> enemies = java.util.Arrays.asList(
            new Enemy("Weak 1", 8, 2),
            new Enemy("Weak 2", 8, 2)
        );
        // Hero always wins
        FixedRandom random = new FixedRandom(5, 5, 2, 2, 5, 5, 2, 2, 5, 5, 2, 2, 5, 5, 2, 2);
        Battle battle = new Battle(hero, enemies, random);

        battle.executeTurn(); // First enemy dies
        assertFalse(battle.isOver());
        
        battle.setSelectedEnemy(1); // Target second enemy
        battle.executeTurn(); // Second enemy dies
        
        assertTrue(battle.isOver());
        assertTrue(battle.heroWon());
        assertEquals(0, battle.getAliveEnemies().size());
    }

    @Test
    public void testMultipleEnemiesBattleFlow() {
        Hero hero = new Hero(10, 20, 10);
        java.util.List<Enemy> enemies = java.util.Arrays.asList(
            new Enemy("Troll 1", 9, 6),
            new Enemy("Troll 2", 9, 4)
        );
        
        // Turn 1: Hero wins vs Troll 1 (selected), loses vs Troll 2
        // Hero: 10+5+4=19, Troll1: 9+2+3=14, Troll2: 9+5+4=18
        FixedRandom random = new FixedRandom(
            5, 4, 2, 3,  // Hero vs Troll 1: hero wins (19 > 14)
            5, 4, 5, 4,  // Hero vs Troll 2: hero wins (19 > 18)
            // Turn 2: Hero wins vs Troll 1, loses vs Troll 2
            5, 5, 2, 2,  // Hero vs Troll 1: hero wins (20 > 13)
            2, 2, 5, 5,  // Hero vs Troll 2: hero loses (14 < 19)
            // Turn 3: Hero wins vs Troll 1, Troll 2 not rolled (Troll 1 dies)
            5, 5, 2, 2,  // Hero vs Troll 1: hero wins (20 > 13)
            5, 5, 2, 2,  // Hero vs Troll 2: hero wins
            // Turn 4: Only Troll 2 alive
            5, 5, 2, 2,  // Hero vs Troll 2: hero wins
            // Turn 5: Finish Troll 2
            5, 5, 2, 2   // Hero vs Troll 2: hero wins
        );
        Battle battle = new Battle(hero, enemies, random);
        
        // Turn 1: Target Troll 1, hero wins both rolls
        battle.setSelectedEnemy(0);
        battle.executeTurn();
        assertEquals(4, enemies.get(0).getStamina()); // Troll 1 takes 2 damage (targeted)
        assertEquals(4, enemies.get(1).getStamina()); // Troll 2 unchanged (hero won but not targeted)
        assertEquals(20, hero.getStamina()); // Hero takes no damage
        assertFalse(battle.isOver());
        
        // Turn 2: Target Troll 1, hero wins vs Troll 1, loses vs Troll 2
        battle.executeTurn();
        assertEquals(2, enemies.get(0).getStamina()); // Troll 1 takes 2 more damage
        assertEquals(4, enemies.get(1).getStamina()); // Troll 2 unchanged
        assertEquals(18, hero.getStamina()); // Hero takes 2 damage from Troll 2
        assertFalse(battle.isOver());
        
        // Turn 3: Target Troll 1 again
        battle.executeTurn();
        assertEquals(0, enemies.get(0).getStamina()); // Troll 1 dies
        assertEquals(4, enemies.get(1).getStamina()); // Troll 2 unchanged
        
        assertFalse(battle.isOver()); // Troll 2 still alive
        
        // Turn 4: Switch to Troll 2
        battle.setSelectedEnemy(1);
        battle.executeTurn();
        assertEquals(2, enemies.get(1).getStamina()); // Troll 2 takes 2 damage
        
        assertFalse(battle.isOver());
        
        // Turn 5: Finish Troll 2
        battle.executeTurn();
        assertEquals(0, enemies.get(1).getStamina()); // Troll 2 dies
        
        assertTrue(battle.isOver());
        assertTrue(battle.heroWon());
        assertEquals(0, battle.getAliveEnemies().size());
    }
}
