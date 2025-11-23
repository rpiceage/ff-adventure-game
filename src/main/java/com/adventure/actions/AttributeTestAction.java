package com.adventure.actions;

import com.adventure.GameController;
import com.adventure.Messages;
import java.util.Map;

public class AttributeTestAction implements Action {
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("attributeTest");
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SINGLE_BUTTON;
    }

    @Override
    public String getButtonText() {
        return Messages.get(Messages.Key.ATTRIBUTE_TEST_BUTTON);
    }

    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        // Execution is handled by GameWindow.startAttributeTest()
    }
}
