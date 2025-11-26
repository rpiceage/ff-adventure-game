package com.adventure;

import com.adventure.actions.Action;
import com.adventure.actions.ActionType;
import com.adventure.actions.AddItemAction;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionButtonFactory {
    private final GameController controller;
    private final ChapterStateManager chapterState;
    private final Runnable onActionComplete;
    
    public ActionButtonFactory(GameController controller, ChapterStateManager chapterState, Runnable onActionComplete) {
        this.controller = controller;
        this.chapterState = chapterState;
        this.onActionComplete = onActionComplete;
    }
    
    public List<JButton> createButtons(Action action, Map<String, Object> actionData) {
        List<JButton> buttons = new ArrayList<>();
        
        if (action.getActionType() == ActionType.MULTIPLE_BUTTONS) {
            if (action instanceof AddItemAction) {
                buttons.addAll(createAddItemButtons((AddItemAction) action, actionData));
            } else {
                buttons.addAll(createChoiceButtons(action, actionData));
            }
        } else if (action.getActionType() == ActionType.SINGLE_BUTTON) {
            JButton btn = new JButton(action.getButtonText());
            btn.addActionListener(e -> {
                action.execute(controller, actionData);
                onActionComplete.run();
            });
            buttons.add(btn);
        }
        
        return buttons;
    }
    
    private List<JButton> createAddItemButtons(AddItemAction action, Map<String, Object> actionData) {
        List<JButton> buttons = new ArrayList<>();
        List<Action.Choice> choices = action.getChoices(actionData);
        Integer maxItems = (Integer) actionData.get("maxItems");
        
        for (int i = 0; i < choices.size(); i++) {
            final int itemIndex = i;
            Action.Choice choice = choices.get(i);
            String itemName = choice.text.replace("Take ", "");
            
            JButton btn = new JButton(choice.text);
            btn.setBackground(new Color(0, 100, 0));
            
            if (maxItems != null && chapterState.getTakenItemsCount() >= maxItems) {
                btn.setEnabled(false);
            }
            
            btn.addActionListener(e -> {
                action.addItem(controller, actionData, itemIndex);
                controller.getAdventureLog().log(String.format(Messages.get(Messages.Key.LOG_TOOK_ITEM), itemName));
                chapterState.incrementTakenItems();
                btn.setEnabled(false);
                onActionComplete.run();
            });
            
            buttons.add(btn);
        }
        
        return buttons;
    }
    
    private List<JButton> createChoiceButtons(Action action, Map<String, Object> actionData) {
        List<JButton> buttons = new ArrayList<>();
        List<Action.Choice> choices = action.getChoices(actionData);
        
        for (Action.Choice choice : choices) {
            JButton btn = new JButton(choice.text);
            btn.addActionListener(e -> {
                controller.selectChoice(choice.index);
                onActionComplete.run();
            });
            buttons.add(btn);
        }
        
        return buttons;
    }
}
