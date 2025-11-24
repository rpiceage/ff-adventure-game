package com.adventure.actions;

import com.adventure.GameController;
import com.adventure.Hero;
import java.util.List;
import java.util.Map;

public class SetValueAction implements Action {
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("setValue");
    }

    @Override
    public ActionType getActionType() {
        return ActionType.PASSIVE;
    }

    @Override
    public String getButtonText() {
        return null;
    }

    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        Map<String, Object> setValueData = (Map<String, Object>) actionData.get("setValue");
        List<Map<String, Object>> values = (List<Map<String, Object>>) setValueData.get("values");
        
        Hero hero = controller.getHero();
        
        for (Map<String, Object> valueData : values) {
            String field = (String) valueData.get("field");
            int value = (Integer) valueData.get("value");
            
            switch (field) {
                case "SKILL":
                    hero.setSkill(value);
                    break;
                case "STAMINA":
                    hero.setStamina(value);
                    break;
                case "LUCK":
                    hero.setLuck(value);
                    break;
                case "GOLD":
                    hero.setGold(value);
                    break;
                case "PROVISIONS":
                    hero.setProvisions(value);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown attribute: " + field);
            }
        }
    }
}
