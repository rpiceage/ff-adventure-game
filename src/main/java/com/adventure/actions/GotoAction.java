package com.adventure.actions;

import com.adventure.GameController;
import java.util.List;
import java.util.Map;

public class GotoAction implements Action {
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("goto");
    }

    @Override
    public ActionType getActionType() {
        return ActionType.MULTIPLE_BUTTONS;
    }

    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        // Execution is handled by choice selection in GameWindow
    }
    
    @Override
    public List<Map<String, Object>> getChoices(Map<String, Object> actionData) {
        return (List<Map<String, Object>>) actionData.get("goto");
    }
}
