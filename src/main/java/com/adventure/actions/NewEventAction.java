package com.adventure.actions;

import com.adventure.GameController;
import java.util.List;
import java.util.Map;

public class NewEventAction implements Action {
    
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("newEvent");
    }
    
    @Override
    public ActionType getActionType() {
        return ActionType.PASSIVE;
    }
    
    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        Map<String, Object> eventData = (Map<String, Object>) actionData.get("newEvent");
        String eventName = (String) eventData.get("name");
        controller.getHero().addEvent(eventName);
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
