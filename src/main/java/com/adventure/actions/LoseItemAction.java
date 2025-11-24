package com.adventure.actions;

import com.adventure.GameController;
import com.adventure.Hero;
import java.util.List;
import java.util.Map;

public class LoseItemAction implements Action {
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("loseItem");
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
        Map<String, Object> loseItemData = (Map<String, Object>) actionData.get("loseItem");
        Hero hero = controller.getHero();
        
        if (loseItemData.containsKey("lose")) {
            // Lose specific items
            List<String> itemsToLose = (List<String>) loseItemData.get("lose");
            for (String item : itemsToLose) {
                hero.removeItem(item);
            }
        } else if (loseItemData.containsKey("all")) {
            // Lose all items except specified ones
            Map<String, Object> allData = (Map<String, Object>) loseItemData.get("all");
            List<String> except = allData.containsKey("except") ? 
                (List<String>) allData.get("except") : List.of();
            
            hero.removeAllItemsExcept(except);
        }
    }
}
