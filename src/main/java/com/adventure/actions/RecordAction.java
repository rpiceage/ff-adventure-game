package com.adventure.actions;

import com.adventure.GameController;

import java.util.List;
import java.util.Map;

public class RecordAction implements Action {
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("record");
    }

    @Override
    public ActionType getActionType() {
        return ActionType.PASSIVE;
    }

    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        Map<String, Object> recordData = (Map<String, Object>) actionData.get("record");
        
        if (recordData.containsKey("set")) {
            // Save current value
            Map<String, Object> setData = (Map<String, Object>) recordData.get("set");
            String field = (String) setData.get("field");
            controller.getHero().saveAttribute(field);
        } else if (recordData.containsKey("restore")) {
            // Restore saved value
            Map<String, Object> restoreData = (Map<String, Object>) recordData.get("restore");
            String field = (String) restoreData.get("field");
            boolean initial = restoreData.containsKey("initial") && (Boolean) restoreData.get("initial");
            controller.getHero().restoreAttribute(field, initial);
        }
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
