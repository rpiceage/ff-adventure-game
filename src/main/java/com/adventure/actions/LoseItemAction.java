package com.adventure.actions;

import com.adventure.GameController;
import com.adventure.Hero;
import com.adventure.Messages;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                if (hero.hasItem(item)) {
                    controller.getAdventureLog().log(String.format(Messages.get(Messages.Key.LOG_LOST_ITEM), item));
                }
                hero.removeItem(item);
            }
        } else if (loseItemData.containsKey("all")) {
            // Lose all items except specified ones
            Map<String, Object> allData = (Map<String, Object>) loseItemData.get("all");
            List<String> except = allData.containsKey("except") ? 
                (List<String>) allData.get("except") : List.of();
            
            List<String> lostItems = hero.getInventory().stream()
                .filter(item -> !except.contains(item))
                .collect(Collectors.toList());
            
            if (!lostItems.isEmpty()) {
                controller.getAdventureLog().log(String.format(Messages.get(Messages.Key.LOG_LOST_ALL_ITEMS_EXCEPT), String.join(", ", except)));
            }
            
            hero.removeAllItemsExcept(except);
        }
    }
}
