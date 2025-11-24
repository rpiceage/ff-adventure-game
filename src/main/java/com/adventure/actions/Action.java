package com.adventure.actions;

import com.adventure.GameController;
import java.util.List;
import java.util.Map;

public interface Action {
    class Choice {
        public final int index;
        public final String text;
        public final boolean enabled;
        
        public Choice(int index, String text) {
            this(index, text, true);
        }
        
        public Choice(int index, String text, boolean enabled) {
            this.index = index;
            this.text = text;
            this.enabled = enabled;
        }
    }
    
    boolean canHandle(Map<String, Object> actionData);
    ActionType getActionType();
    void execute(GameController controller, Map<String, Object> actionData);
    
    // For SINGLE_BUTTON actions
    default String getButtonText() {
        return "Action";
    }
    
    // For MULTIPLE_BUTTONS actions
    default List<Choice> getChoices(Map<String, Object> actionData) {
        return List.of();
    }
}
