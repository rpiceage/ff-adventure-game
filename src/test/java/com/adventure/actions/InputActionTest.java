package com.adventure.actions;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class InputActionTest {
    
    @Test
    public void testCanHandleInputAction() {
        InputAction action = new InputAction();
        Map<String, Object> actionData = Map.of(
            "input", Map.of(
                "answers", List.of(
                    Map.of("int", 42, "chapter", 1)
                )
            )
        );
        assertTrue(action.canHandle(actionData));
    }
    
    @Test
    public void testCannotHandleOtherActions() {
        InputAction action = new InputAction();
        Map<String, Object> actionData = Map.of("goto", List.of());
        assertFalse(action.canHandle(actionData));
    }
    
    @Test
    public void testGetActionType() {
        InputAction action = new InputAction();
        assertEquals(ActionType.INPUT, action.getActionType());
    }
    
    @Test
    public void testGetAnswersWithIntAnswer() {
        InputAction action = new InputAction();
        Map<String, Object> actionData = Map.of(
            "input", Map.of(
                "answers", List.of(
                    Map.of("int", 42, "chapter", 1)
                )
            )
        );
        
        List<Map<String, Object>> answers = action.getAnswers(actionData);
        assertEquals(1, answers.size());
        assertEquals(42, answers.get(0).get("int"));
        assertEquals(1, answers.get(0).get("chapter"));
    }
    
    @Test
    public void testGetAnswersWithStringAnswer() {
        InputAction action = new InputAction();
        Map<String, Object> actionData = Map.of(
            "input", Map.of(
                "answers", List.of(
                    Map.of("string", "password", "chapter", 5)
                )
            )
        );
        
        List<Map<String, Object>> answers = action.getAnswers(actionData);
        assertEquals(1, answers.size());
        assertEquals("password", answers.get(0).get("string"));
        assertEquals(5, answers.get(0).get("chapter"));
    }
    
    @Test
    public void testGetAnswersWithMultipleAnswers() {
        InputAction action = new InputAction();
        Map<String, Object> actionData = Map.of(
            "input", Map.of(
                "answers", List.of(
                    Map.of("int", 249, "chapter", 249),
                    Map.of("string", "secret", "chapter", 100)
                )
            )
        );
        
        List<Map<String, Object>> answers = action.getAnswers(actionData);
        assertEquals(2, answers.size());
    }
}
