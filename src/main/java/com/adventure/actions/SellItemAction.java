package com.adventure.actions;

import com.adventure.GameController;

import java.util.List;
import java.util.Map;

public class SellItemAction implements Action {
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("sellItem");
    }

    @Override
    public ActionType getActionType() {
        return ActionType.DISPLAY;
    }

    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        // This is a DISPLAY action - actual selling happens via item button clicks
    }

    @Override
    public String getButtonText() {
        return null;
    }

    @Override
    public List<Choice> getChoices(Map<String, Object> actionData) {
        return null;
    }
    
    public int getGoldPerItem(Map<String, Object> actionData) {
        Map<String, Object> sellData = (Map<String, Object>) actionData.get("sellItem");
        return (Integer) sellData.get("all");
    }
    
    public int getMaxItemCount(Map<String, Object> actionData) {
        Map<String, Object> sellData = (Map<String, Object>) actionData.get("sellItem");
        return sellData.containsKey("maxItemCount") ? (Integer) sellData.get("maxItemCount") : Integer.MAX_VALUE;
    }
}
