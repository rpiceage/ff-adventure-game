package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class RecordActionFlowTest {
    
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
    public void testRecord_SaveRestoreFlow() {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-record.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        GameController controller = new GameController(adventure);
        Hero hero = controller.getHero();
        
        // Controller starts at chapter 0 which saves SKILL (12) then reduces by 3
        assertEquals(9, hero.getSkill()); // SKILL reduced to 9 (saved value is 12)
        assertEquals(24, hero.getStamina());
        
        // Chapter 1: Battle with reduced SKILL
        controller.goToChapter(1);
        
        // Simulate battle with fixed random (hero wins quickly)
        Random fixedRandom = new FixedRandom(
            6, 6, 1, 1,  // Turn 1: hero wins
            6, 6, 1, 1,  // Turn 2: hero wins
            6, 6, 1, 1,  // Turn 3: hero wins
            6, 6, 1, 1,  // Turn 4: hero wins
            6, 6, 1, 1   // Turn 5: hero wins (demon dies)
        );
        
        Enemy demon = new Enemy("Demon", 8, 10);
        Battle battle = new Battle(hero, java.util.Arrays.asList(demon), fixedRandom, 0);
        
        // Execute battle turns
        while (!battle.isOver()) {
            battle.executeTurn();
        }
        
        assertTrue(battle.heroWon());
        assertEquals(9, hero.getSkill()); // Still reduced
        
        // Chapter 2: Restore SKILL to saved value
        controller.goToChapter(2);
        assertEquals(12, hero.getSkill()); // SKILL restored to 12
        
        // Chapter 3: Lose 10 STAMINA
        controller.goToChapter(3);
        assertEquals(14, hero.getStamina()); // STAMINA reduced to 14
        
        // Chapter 4: Restore STAMINA to initial value
        controller.goToChapter(4);
        assertEquals(24, hero.getStamina()); // STAMINA restored to max (24)
    }
}
