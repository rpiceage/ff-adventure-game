package com.adventure;

import com.adventure.actions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameController {
    private Adventure adventure;
    private Adventure.Chapter currentChapter;
    private Hero hero;
    private List<Action> actions;

    public GameController(Adventure adventure) {
        this.adventure = adventure;
        this.currentChapter = getChapter(0);
        int initialGold = (adventure.init != null) ? adventure.init.gold : 0;
        this.hero = new Hero(12, 24, 12, initialGold);
        this.actions = new ArrayList<>();
        registerActions();
        applyModifiers(); // Apply modifiers for initial chapter
    }

    private void registerActions() {
        actions.add(new DisplayAction());
        actions.add(new ModifyAction());
        actions.add(new BattleAction());
        actions.add(new LuckAction());
        actions.add(new AddItemAction());
        actions.add(new GotoAction());
    }

    public Action getCurrentAction() {
        for (Map<String, Object> actionData : currentChapter.actions) {
            for (Action action : actions) {
                if (action.canHandle(actionData)) {
                    ActionType type = action.getActionType();
                    // Return only interactive actions
                    if (type == ActionType.SINGLE_BUTTON || type == ActionType.MULTIPLE_BUTTONS) {
                        return action;
                    }
                }
            }
        }
        return null;
    }

    public Action getActionForData(Map<String, Object> actionData) {
        for (Action action : actions) {
            if (action.canHandle(actionData)) {
                return action;
            }
        }
        return null;
    }

    public Map<String, Object> getCurrentActionData() {
        for (Map<String, Object> actionData : currentChapter.actions) {
            for (Action action : actions) {
                if (action.canHandle(actionData)) {
                    ActionType type = action.getActionType();
                    // Return only interactive actions
                    if (type == ActionType.SINGLE_BUTTON || type == ActionType.MULTIPLE_BUTTONS) {
                        return actionData;
                    }
                }
            }
        }
        return null;
    }

    public Adventure.Chapter getCurrentChapter() {
        return currentChapter;
    }

    public Hero getHero() {
        return hero;
    }

    public String getDisplayText() {
        for (Map<String, Object> actionData : currentChapter.actions) {
            for (Action action : actions) {
                if (action instanceof DisplayAction && action.canHandle(actionData)) {
                    return ((DisplayAction) action).getDisplayText(actionData);
                }
            }
        }
        return "";
    }

    public void selectChoice(int choiceIndex) {
        selectChoice(choiceIndex, getCurrentActionData());
    }

    public void selectChoice(int choiceIndex, Map<String, Object> actionData) {
        // Only GotoAction uses selectChoice
        List<Map<String, Object>> gotoData = (List<Map<String, Object>>) actionData.get("goto");
        if (gotoData != null && choiceIndex >= 0 && choiceIndex < gotoData.size()) {
            int targetChapter = (Integer) gotoData.get(choiceIndex).get("chapter");
            currentChapter = getChapter(targetChapter);
            applyModifiers();
        }
    }

    public boolean isGameOver() {
        return hero.getStamina() == 0;
    }

    public void goToChapter(int chapterIndex) {
        currentChapter = getChapter(chapterIndex);
        applyModifiers();
    }

    private void applyModifiers() {
        for (Map<String, Object> actionData : currentChapter.actions) {
            for (Action action : actions) {
                if (action.getActionType() == ActionType.PASSIVE && action.canHandle(actionData)) {
                    action.execute(this, actionData);
                }
            }
        }
    }

    private Adventure.Chapter getChapter(int index) {
        if (adventure == null || adventure.chapters == null) {
            return null;
        }
        return adventure.chapters.stream()
            .filter(c -> c.index == index)
            .findFirst().orElse(null);
    }
}
