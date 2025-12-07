package com.adventure.actions;

import com.adventure.*;
import com.adventure.ui.AttributeTestUI;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class AttributeTestActionTest {
    
    @Test
    public void testCanHandle() {
        AttributeTestAction action = new AttributeTestAction();
        assertTrue(action.canHandle(Map.of("attributeTest", Map.of())));
        assertFalse(action.canHandle(Map.of("goto", "data")));
    }
    
    @Test
    public void testGetActionType() {
        AttributeTestAction action = new AttributeTestAction();
        assertEquals(ActionType.SINGLE_BUTTON, action.getActionType());
    }
    
    @Test
    public void testGetButtonText() {
        AttributeTestAction action = new AttributeTestAction();
        assertNotNull(action.getButtonText());
    }
    
    @Test
    public void testAttributeTestWithTarget() {
        GameController controller = createTestController();
        
        JTextArea textArea = new JTextArea();
        JPanel buttonPanel = new JPanel();
        
        AttributeTestUI ui = new AttributeTestUI(textArea, buttonPanel, controller, () -> {});
        
        Map<String, Object> testData = Map.of(
            "attributeTest", Map.of(
                "dice", 2,
                "target", 9,
                "success", 100,
                "fail", 200
            )
        );
        
        JPanel panel = ui.start(testData);
        assertNotNull(panel);
        assertTrue(textArea.getText().contains("Target: 9"));
    }
    
    @Test
    public void testAttributeTestWithTargetSuccess() {
        GameController controller = createTestController();
        
        JTextArea textArea = new JTextArea();
        JPanel buttonPanel = new JPanel();
        Random fixedRandom = new Random() {
            @Override
            public int nextInt(int bound) {
                return 2;
            }
        };
        
        AttributeTestUI ui = new AttributeTestUI(textArea, buttonPanel, controller, () -> {}, fixedRandom);
        
        Map<String, Object> testData = Map.of(
            "attributeTest", Map.of(
                "dice", 2,
                "target", 9,
                "success", 100,
                "fail", 200
            )
        );
        
        ui.start(testData);
        JButton testButton = (JButton) buttonPanel.getComponent(0);
        testButton.doClick();
        
        try { Thread.sleep(1200); } catch (InterruptedException e) {}
        
        String text = textArea.getText().toLowerCase();
        assertTrue(text.contains("success") || text.contains("passed") || text.contains("sikeres"), 
            "Expected success message but got: " + textArea.getText());
    }
    
    @Test
    public void testAttributeTestWithTargetFail() {
        GameController controller = createTestController();
        
        JTextArea textArea = new JTextArea();
        JPanel buttonPanel = new JPanel();
        Random fixedRandom = new Random() {
            @Override
            public int nextInt(int bound) {
                return 5;
            }
        };
        
        AttributeTestUI ui = new AttributeTestUI(textArea, buttonPanel, controller, () -> {}, fixedRandom);
        
        Map<String, Object> testData = Map.of(
            "attributeTest", Map.of(
                "dice", 2,
                "target", 9,
                "success", 100,
                "fail", 200
            )
        );
        
        ui.start(testData);
        JButton testButton = (JButton) buttonPanel.getComponent(0);
        testButton.doClick();
        
        try { Thread.sleep(1200); } catch (InterruptedException e) {}
        
        String text = textArea.getText().toLowerCase();
        assertTrue(text.contains("fail") || text.contains("not") || text.contains("sikertelen"),
            "Expected fail message but got: " + textArea.getText());
    }
    
    @Test
    public void testAttributeTestWithHeroAttribute() {
        GameController controller = createTestController();
        
        JTextArea textArea = new JTextArea();
        JPanel buttonPanel = new JPanel();
        
        AttributeTestUI ui = new AttributeTestUI(textArea, buttonPanel, controller, () -> {});
        
        Map<String, Object> testData = Map.of(
            "attributeTest", Map.of(
                "dice", 2,
                "attribute", List.of("SKILL"),
                "success", 100,
                "fail", 200
            )
        );
        
        JPanel panel = ui.start(testData);
        assertNotNull(panel);
        assertFalse(textArea.getText().contains("Target:"));
        assertTrue(textArea.getText().contains("SKILL") || textArea.getText().contains("ÜGYESSÉG"));
    }
    
    private GameController createTestController() {
        Adventure adventure = new Adventure();
        adventure.chapters = new ArrayList<>();
        Adventure.Chapter chapter = new Adventure.Chapter();
        chapter.index = 0;
        chapter.actions = new ArrayList<>();
        adventure.chapters.add(chapter);
        return new GameController(adventure);
    }
}
