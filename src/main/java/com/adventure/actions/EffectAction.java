package com.adventure.actions;

import com.adventure.GameController;
import java.util.List;
import java.util.Map;

public class EffectAction implements Action {
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("effect");
    }

    @Override
    public ActionType getActionType() {
        return ActionType.PASSIVE;
    }

    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        Map<String, Object> effectData = (Map<String, Object>) actionData.get("effect");
        Map<String, Object> nextData = (Map<String, Object>) effectData.get("next");
        
        int attackModifier = (Integer) nextData.get("attackModifier");
        String text = (String) nextData.get("text");
        
        controller.setNextBattleEffect(attackModifier, text);
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
