package com.adventure.actions;

import com.adventure.GameController;
import java.util.List;
import java.util.Map;

public class InterruptAction implements Action {
    
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("interrupt");
    }
    
    @Override
    public ActionType getActionType() {
        return ActionType.SINGLE_BUTTON;
    }
    
    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        // Return to previous chapter if set
        Integer returnChapter = controller.getReturnChapter();
        if (returnChapter != null) {
            // Don't clear returnChapter here - let the auto-resume logic clear it
            controller.goToChapter(returnChapter);
        }
    }
    
    @Override
    public String getButtonText() {
        return "Continue";
    }
    
    @Override
    public List<Choice> getChoices(Map<String, Object> actionData) {
        return null;
    }
}
