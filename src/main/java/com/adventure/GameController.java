package com.adventure;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class GameController {
    private Adventure adventure;
    private Adventure.Chapter currentChapter;
    private Hero hero;

    public GameController(Adventure adventure) {
        this.adventure = adventure;
        this.currentChapter = getChapter(0);
        this.hero = new Hero(12, 24, 12);
    }

    public Adventure.Chapter getCurrentChapter() {
        return currentChapter;
    }

    public Hero getHero() {
        return hero;
    }

    public String getDisplayText() {
        for (Map<String, Object> action : currentChapter.actions) {
            if (action.containsKey("display")) {
                return action.get("display").toString().trim();
            }
        }
        return "";
    }

    public List<Map<String, Object>> getChoices() {
        for (Map<String, Object> action : currentChapter.actions) {
            if (action.containsKey("goto")) {
                return (List<Map<String, Object>>) action.get("goto");
            }
        }
        return List.of();
    }

    public void selectChoice(int choiceIndex) {
        List<Map<String, Object>> choices = getChoices();
        if (choiceIndex >= 0 && choiceIndex < choices.size()) {
            int targetChapter = (Integer) choices.get(choiceIndex).get("chapter");
            currentChapter = getChapter(targetChapter);
            applyModifiers();
        }
    }

    public boolean isGameOver() {
        return hero.getStamina() == 0;
    }

    public Map<String, Object> getBattleAction() {
        for (Map<String, Object> action : currentChapter.actions) {
            if (action.containsKey("battle")) {
                return action;
            }
        }
        return null;
    }

    public void goToChapter(int chapterIndex) {
        currentChapter = getChapter(chapterIndex);
        applyModifiers();
    }

    private void applyModifiers() {
        for (Map<String, Object> action : currentChapter.actions) {
            if (action.containsKey("modify")) {
                Map<String, Object> modify = (Map<String, Object>) action.get("modify");
                if (modify.containsKey("values")) {
                    List<Map<String, Object>> values = (List<Map<String, Object>>) modify.get("values");
                    for (Map<String, Object> mod : values) {
                        String field = (String) mod.get("field");
                        int value = (Integer) mod.get("value");
                        try {
                            String methodName = "modify" + field.charAt(0) + field.substring(1).toLowerCase();
                            Method method = Hero.class.getMethod(methodName, int.class);
                            method.invoke(hero, value);
                        } catch (Exception e) {
                            // Ignore invalid field names
                        }
                    }
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
