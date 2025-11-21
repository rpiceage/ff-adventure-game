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
            return values[index++];
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
}
