package com.adventure.actions;

import com.adventure.GameController;
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
        String itemName = (String) items.get(itemIndex).get("name");
        controller.getHero().addItem(itemName);
    }
}
