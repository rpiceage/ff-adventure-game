package com.adventure.actions;

import com.adventure.GameController;
import com.adventure.Hero;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class ModifyAction implements Action {
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("modify");
    }

    @Override
    public ActionType getActionType() {
        return ActionType.PASSIVE;
    }

    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        applyModifications(controller.getHero(), actionData);
    }
    
    public void applyModifications(Hero hero, Map<String, Object> actionData) {
        Map<String, Object> modify = (Map<String, Object>) actionData.get("modify");
        if (modify.containsKey("values")) {
            List<Map<String, Object>> values = (List<Map<String, Object>>) modify.get("values");
            for (Map<String, Object> mod : values) {
                String field = (String) mod.get("field");
                int value = (Integer) mod.get("value");
                try {
                    String methodName = "modify" + field.charAt(0) + field.substring(1).toLowerCase();
                    Method method = Hero.class.getMethod(methodName, int.class);
                    method.invoke(hero, value);
                } catch (Exception e) {
                    // Ignore invalid field names
                }
            }
        }
    }
}
