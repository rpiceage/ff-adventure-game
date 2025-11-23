package com.adventure.actions;

import com.adventure.GameController;
import java.util.List;
import java.util.Map;

public class DeathAction implements Action {
    
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("death");
    }
    
    @Override
    public ActionType getActionType() {
        return ActionType.PASSIVE;
    }
    
    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        controller.getHero().modifyStaminaSilent(-controller.getHero().getStamina());
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
