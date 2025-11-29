package com.adventure.actions;

import com.adventure.GameController;
import com.adventure.Item;
import com.adventure.Messages;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddItemAction implements Action {
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("addItem");
    }

    @Override
    public ActionType getActionType() {
        return ActionType.MULTIPLE_BUTTONS;
    }

    @Override
    public List<Choice> getChoices(Map<String, Object> actionData) {
        Map<String, Object> addItemData = (Map<String, Object>) actionData.get("addItem");
        List<Map<String, Object>> items = (List<Map<String, Object>>) addItemData.get("items");
        
        List<Choice> choices = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            String itemName = (String) items.get(i).get("name");
            String buttonText = Messages.get(Messages.Key.ADD_ITEM) + " " + itemName;
            choices.add(new Choice(i, buttonText));
        }
        return choices;
    }

    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        // Execution happens when a specific item button is clicked
        // This is handled by GameWindow
    }

    public void addItem(GameController controller, Map<String, Object> actionData, int itemIndex) {
        Map<String, Object> addItemData = (Map<String, Object>) actionData.get("addItem");
        List<Map<String, Object>> items = (List<Map<String, Object>>) addItemData.get("items");
        Map<String, Object> itemData = items.get(itemIndex);
        
        String itemName = (String) itemData.get("name");
        boolean useAnyTime = itemData.containsKey("useAnyTime") && (Boolean) itemData.get("useAnyTime");
        
        if (useAnyTime) {
            // Check if it's a potion-like item with restore effect
            String effectType = itemData.containsKey("effect") ? (String) itemData.get("effect") : null;
            
            if ("restoreSkill".equals(effectType)) {
                Item item = new Item(itemName, true, hero -> hero.setSkill(hero.getInitialSkill()));
                controller.getHero().addItem(item);
            } else if ("restoreStamina".equals(effectType)) {
                Item item = new Item(itemName, true, hero -> hero.setStamina(hero.getInitialStamina()));
                controller.getHero().addItem(item);
            } else if ("restoreLuck".equals(effectType)) {
                Item item = new Item(itemName, true, hero -> hero.setLuck(hero.getInitialLuck()));
                controller.getHero().addItem(item);
            } else {
                // useAnyTime but no effect defined - just a flag for future use
                Item item = new Item(itemName, true, null);
                controller.getHero().addItem(item);
            }
        } else {
            // Regular item
            controller.getHero().addItem(itemName);
        }
    }
}
