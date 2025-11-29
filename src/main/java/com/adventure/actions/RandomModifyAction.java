package com.adventure.actions;

import com.adventure.GameController;
import com.adventure.Messages;
import java.util.Map;

public class RandomModifyAction implements Action {
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("randomModify");
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SINGLE_BUTTON;
    }

    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        // UI handles the execution
    }

    @Override
    public String getButtonText() {
        return Messages.get(Messages.Key.ROLL_DICE);
    }
}
