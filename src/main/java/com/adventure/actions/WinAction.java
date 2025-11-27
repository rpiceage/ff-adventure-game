package com.adventure.actions;

import com.adventure.GameController;
import java.util.List;
import java.util.Map;

public class WinAction implements Action {
    
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("win") && actionData.get("win") instanceof String;
    }
    
    @Override
    public ActionType getActionType() {
        return ActionType.DISPLAY;
    }
    
    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        // Display action - text is shown by GameWindow
    }
    
    @Override
    public String getButtonText() {
        return null;
    }
    
    @Override
    public List<Choice> getChoices(Map<String, Object> actionData) {
        return null;
    }
    
    public String getWinText(Map<String, Object> actionData) {
        return actionData.get("win").toString().trim();
    }
}
