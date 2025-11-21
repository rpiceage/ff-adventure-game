package com.adventure.actions;

import com.adventure.GameController;
import com.adventure.Messages;
import java.util.Map;

public class BattleAction implements Action {
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("battle");
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SINGLE_BUTTON;
    }

    @Override
    public String getButtonText() {
        return Messages.get(Messages.Key.BATTLE_BEGIN);
    }

    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        // Execution is handled by GameWindow.startBattle()
        // This is called to trigger the UI
    }
}
