package com.adventure.actions;

import com.adventure.GameController;
import com.adventure.Hero;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckParameterAction implements Action {
    
    @Override
    public boolean canHandle(Map<String, Object> actionData) {
        return actionData.containsKey("checkParameter");
    }
    
    @Override
    public ActionType getActionType() {
        return ActionType.MULTIPLE_BUTTONS;
    }
    
    @Override
    public void execute(GameController controller, Map<String, Object> actionData) {
        // Not used - buttons handle navigation directly
    }
    
    @Override
    public String getButtonText() {
        return null;
    }
    
    @Override
    public List<Choice> getChoices(Map<String, Object> actionData) {
        Map<String, Object> checkData = (Map<String, Object>) actionData.get("checkParameter");
        Map<String, Object> greaterThanOrEquals = (Map<String, Object>) checkData.get("greaterThanOrEquals");
        Map<String, Object> lessThan = (Map<String, Object>) checkData.get("lessThan");
        
        List<Choice> choices = new ArrayList<>();
        choices.add(new Choice(0, (String) greaterThanOrEquals.get("text")));
        choices.add(new Choice(1, (String) lessThan.get("text")));
        
        return choices;
    }
    
    public boolean meetsThreshold(GameController controller, Map<String, Object> actionData) {
        Map<String, Object> checkData = (Map<String, Object>) actionData.get("checkParameter");
        String parameter = (String) checkData.get("parameter");
        int threshold = (Integer) checkData.get("threshold");
        
        Hero hero = controller.getHero();
        int value = getParameterValue(hero, parameter);
        
        return value >= threshold;
    }
    
    public int getGreaterThanOrEqualsChapter(Map<String, Object> actionData) {
        Map<String, Object> checkData = (Map<String, Object>) actionData.get("checkParameter");
        Map<String, Object> greaterThanOrEquals = (Map<String, Object>) checkData.get("greaterThanOrEquals");
        return (Integer) greaterThanOrEquals.get("chapter");
    }
    
    public int getLessThanChapter(Map<String, Object> actionData) {
        Map<String, Object> checkData = (Map<String, Object>) actionData.get("checkParameter");
        Map<String, Object> lessThan = (Map<String, Object>) checkData.get("lessThan");
        return (Integer) lessThan.get("chapter");
    }
    
    private int getParameterValue(Hero hero, String parameter) {
        switch (parameter) {
            case "SKILL": return hero.getSkill();
            case "STAMINA": return hero.getStamina();
            case "LUCK": return hero.getLuck();
            case "GOLD": return hero.getGold();
            case "PROVISIONS": return hero.getProvisions();
            default: throw new IllegalArgumentException("Unknown parameter: " + parameter);
        }
    }
}
