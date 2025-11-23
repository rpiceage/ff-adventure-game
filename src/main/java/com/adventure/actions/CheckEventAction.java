package com.adventure.actions;

import com.adventure.GameController;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckEventAction implements Action {
    
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("checkEvent");
    }
    
    @Override
    public ActionType getActionType() {
        return ActionType.MULTIPLE_BUTTONS;
    }
    
    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        // Not used - buttons handle navigation directly
    }
    
    @Override
    public String getButtonText() {
        return null;
    }
    
    @Override
    public List<Choice> getChoices(Map<String, Object> actionData) {
        Map<String, Object> checkData = (Map<String, Object>) actionData.get("checkEvent");
        List<String> names = (List<String>) checkData.get("name");
        Map<String, Object> existing = (Map<String, Object>) checkData.get("existing");
        Map<String, Object> missing = (Map<String, Object>) checkData.get("missing");
        
        List<Choice> choices = new ArrayList<>();
        choices.add(new Choice(0, (String) existing.get("text")));
        choices.add(new Choice(1, (String) missing.get("text")));
        
        return choices;
    }
    
    public boolean hasEventOrItem(GameController controller, Map<String, Object> actionData) {
        Map<String, Object> checkData = (Map<String, Object>) actionData.get("checkEvent");
        List<String> names = (List<String>) checkData.get("name");
        
        for (String name : names) {
            if (controller.getHero().hasEvent(name) || controller.getHero().hasItem(name)) {
                return true;
            }
        }
        return false;
    }
    
    public int getExistingChapter(Map<String, Object> actionData) {
        Map<String, Object> checkData = (Map<String, Object>) actionData.get("checkEvent");
        Map<String, Object> existing = (Map<String, Object>) checkData.get("existing");
        return (Integer) existing.get("chapter");
    }
    
    public int getMissingChapter(Map<String, Object> actionData) {
        Map<String, Object> checkData = (Map<String, Object>) actionData.get("checkEvent");
        Map<String, Object> missing = (Map<String, Object>) checkData.get("missing");
        return (Integer) missing.get("chapter");
    }
}
