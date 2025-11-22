package com.adventure.actions;

import com.adventure.GameController;
import java.util.List;
import java.util.Map;

public class UseItemAction implements Action {
    
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("useItem");
    }
    
    @Override
    public ActionType getActionType() {
        return ActionType.DISPLAY;
    }
    
    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        // Does nothing - item buttons handle navigation directly
    }
    
    @Override
    public String getButtonText() {
        return null;
    }
    
    @Override
    public List<Choice> getChoices(Map<String, Object> actionData) {
        return null;
    }
}
