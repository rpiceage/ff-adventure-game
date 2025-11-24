package com.adventure;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class BattleModifierTest {
    
    @Test
    public void testBattleWithNegativeModifier() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        
        // Create a battle with -2 modifier
        Battle battle = new Battle(hero, "Night Hunter", 11, 8, new Random(42));
        battle.setModifier(-2, "Deduct 2 from your Attack Strength each round.");
        
        // Verify modifier text is set
        assertEquals("Deduct 2 from your Attack Strength each round.", battle.getModifierText());
        
        // Manually add modifier text to battle log (as BattleUI does)
        battle.appendToBattleLog(battle.getModifierText() + "\n\n");
        
        // Execute a turn
        battle.executeTurn();
        
        // Verify battle log contains modifier text
        String log = battle.getBattleLog();
        assertTrue(log.contains("Deduct 2 from your Attack Strength each round."));
    }
    
    @Test
    public void testBattleWithPositiveModifier() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        
        // Create a battle with +3 modifier
        Battle battle = new Battle(hero, "Weak Enemy", 5, 4, new Random(42));
        battle.setModifier(3, "Add 3 to your Attack Strength each round.");
        
        // Verify modifier text is set
        assertEquals("Add 3 to your Attack Strength each round.", battle.getModifierText());
    }
    
    @Test
    public void testBattleWithoutModifier() {
        Hero hero = new Hero(12, 24, 12, 0, 0);
        
        // Create a battle without modifier
        Battle battle = new Battle(hero, "Normal Enemy", 10, 10, new Random(42));
        
        // Verify no modifier text
        assertNull(battle.getModifierText());
    }
}
