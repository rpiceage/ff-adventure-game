package com.adventure.actions;

import com.adventure.GameController;
import java.util.List;
import java.util.Map;

public class InputAction implements Action {
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("input");
    }

    @Override
    public ActionType getActionType() {
        return ActionType.INPUT;
    }

    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        // Execution handled by GameWindow with user input
    }

    @Override
    public List<Choice> getChoices(Map<String, Object> actionData) {
        return null;
    }

    public List<Map<String, Object>> getAnswers(Map<String, Object> actionData) {
        Map<String, Object> inputData = (Map<String, Object>) actionData.get("input");
        return (List<Map<String, Object>>) inputData.get("answers");
    }
}
