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
        
        // Check if this is a numberOfItemChoices check
        if (checkData.containsKey("numberOfItemChoices")) {
            return getNumberOfItemChoices(actionData);
        }
        
        // Original event/item check
        List<String> names = (List<String>) checkData.get("name");
        Map<String, Object> existing = (Map<String, Object>) checkData.get("existing");
        Map<String, Object> missing = (Map<String, Object>) checkData.get("missing");
        
        List<Choice> choices = new ArrayList<>();
        choices.add(new Choice(0, (String) existing.get("text")));
        choices.add(new Choice(1, (String) missing.get("text")));
        
        return choices;
    }
    
    private List<Choice> getNumberOfItemChoices(Map<String, Object> actionData) {
        Map<String, Object> checkData = (Map<String, Object>) actionData.get("checkEvent");
        List<Map<String, Object>> numberOfItemChoices = (List<Map<String, Object>>) checkData.get("numberOfItemChoices");
        
        List<Choice> choices = new ArrayList<>();
        for (int i = 0; i < numberOfItemChoices.size(); i++) {
            Map<String, Object> choice = numberOfItemChoices.get(i);
            choices.add(new Choice(i, (String) choice.get("text")));
        }
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
    
    public boolean isNumberOfItemCheck(Map<String, Object> actionData) {
        Map<String, Object> checkData = (Map<String, Object>) actionData.get("checkEvent");
        return checkData.containsKey("numberOfItemChoices");
    }
    
    public int getChapterForItemCount(GameController controller, Map<String, Object> actionData, int choiceIndex) {
        Map<String, Object> checkData = (Map<String, Object>) actionData.get("checkEvent");
        List<String> names = (List<String>) checkData.get("name");
        String itemName = names.get(0); // Get the item to count
        
        // Count how many of this item the hero has
        long itemCount = controller.getHero().getInventory().stream()
            .filter(item -> item.getName().equals(itemName))
            .count();
        
        List<Map<String, Object>> numberOfItemChoices = (List<Map<String, Object>>) checkData.get("numberOfItemChoices");
        Map<String, Object> choice = numberOfItemChoices.get(choiceIndex);
        int expectedNumber = (Integer) choice.get("number");
        
        // Check if item count matches the expected number
        if (itemCount == expectedNumber) {
            return (Integer) choice.get("chapter");
        }
        
        // If no match, return the chapter anyway (shouldn't happen if YAML is correct)
        return (Integer) choice.get("chapter");
    }
}
