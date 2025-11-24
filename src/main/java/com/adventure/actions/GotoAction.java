package com.adventure.actions;

import com.adventure.GameController;
import java.util.ArrayList;
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
    public List<Choice> getChoices(Map<String, Object> actionData) {
        return getChoices(null, actionData);
    }
    
    public List<Choice> getChoices(GameController controller, Map<String, Object> actionData) {
        List<Map<String, Object>> gotoData = (List<Map<String, Object>>) actionData.get("goto");
        List<Choice> choices = new ArrayList<>();
        for (int i = 0; i < gotoData.size(); i++) {
            Map<String, Object> choice = gotoData.get(i);
            int chapter = (Integer) choice.get("chapter");
            String text = (String) choice.get("text");
            
            // Filter out visited chapters
            if (controller != null && controller.getHero().hasVisitedChapter(chapter)) {
                continue;
            }
            
            // Check condition if present
            boolean enabled = true;
            if (controller != null && choice.containsKey("condition")) {
                Map<String, Object> condition = (Map<String, Object>) choice.get("condition");
                if (condition.containsKey("parameter")) {
                    Map<String, Object> parameter = (Map<String, Object>) condition.get("parameter");
                    String paramName = (String) parameter.get("name");
                    int threshold = (Integer) parameter.get("greaterThanOrEquals");
                    
                    int value = getAttributeValue(controller, paramName);
                    enabled = value >= threshold;
                }
            }
            
            choices.add(new Choice(i, text, enabled));
        }
        return choices;
    }
    
    private int getAttributeValue(GameController controller, String attribute) {
        try {
            String methodName = "get" + attribute.charAt(0) + attribute.substring(1).toLowerCase();
            return (int) controller.getHero().getClass().getMethod(methodName).invoke(controller.getHero());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown attribute: " + attribute, e);
        }
    }
}
