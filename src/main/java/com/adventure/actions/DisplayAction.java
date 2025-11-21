package com.adventure.actions;

import com.adventure.GameController;
import java.util.Map;

public class DisplayAction implements Action {
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("display");
    }

    @Override
    public ActionType getActionType() {
        return ActionType.DISPLAY;
    }

    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        // Display action is passive - text is shown by GameWindow
    }
    
    public String getDisplayText(Map<String, Object> actionData) {
        return actionData.get("display").toString().trim();
    }
}
