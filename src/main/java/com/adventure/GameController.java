package com.adventure;

import java.util.List;
import java.util.Map;

public class GameController {
    private Adventure adventure;
    private Adventure.Chapter currentChapter;

    public GameController(Adventure adventure) {
        this.adventure = adventure;
        this.currentChapter = getChapter(0);
    }

    public Adventure.Chapter getCurrentChapter() {
        return currentChapter;
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
        }
    }

    private Adventure.Chapter getChapter(int index) {
        return adventure.chapters.stream()
            .filter(c -> c.index == index)
            .findFirst().orElse(null);
    }
}
