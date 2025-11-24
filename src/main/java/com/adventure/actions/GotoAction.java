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
            int chapter = (Integer) gotoData.get(i).get("chapter");
            String text = (String) gotoData.get(i).get("text");
            
            // Filter out visited chapters
            if (controller != null && controller.getHero().hasVisitedChapter(chapter)) {
                continue;
            }
            
            choices.add(new Choice(i, text));
        }
        return choices;
    }
}
