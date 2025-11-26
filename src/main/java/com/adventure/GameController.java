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
    private AdventureLog adventureLog;
    private String gameYamlPath;

    public GameController(Adventure adventure) {
        this(adventure, null);
    }
    
    public GameController(Adventure adventure, String gameYamlPath) {
        this.adventure = adventure;
        this.gameYamlPath = gameYamlPath;
        this.adventureLog = new AdventureLog();
        this.currentChapter = getChapter(0);
        int initialGold = (adventure.init != null) ? adventure.init.gold : 0;
        int initialProvisions = (adventure.init != null) ? adventure.init.provisions : 0;
        this.hero = new Hero(12, 24, 12, initialGold, initialProvisions);
        this.hero.visitChapter(0); // Mark starting chapter as visited
        this.actions = new ArrayList<>();
        registerActions();
        applyModifiers(); // Apply modifiers for initial chapter
        adventureLog.log(String.format(Messages.get(Messages.Key.LOG_ADVENTURE_STARTED), adventure.title));
        String displayText = getDisplayText();
        adventureLog.log(String.format(Messages.get(Messages.Key.LOG_CHAPTER), 0));
        adventureLog.log(displayText.replaceAll("\\n", " "));
    }

    private void registerActions() {
        actions.add(new DisplayAction());
        actions.add(new ModifyAction());
        actions.add(new RandomModifyAction());
        actions.add(new RandomGotoAction());
        actions.add(new SetValueAction());
        actions.add(new NewEventAction());
        actions.add(new DeathAction());
        actions.add(new BattleAction());
        actions.add(new LuckAction());
        actions.add(new AttributeTestAction());
        actions.add(new AddItemAction());
        actions.add(new LoseItemAction());
        actions.add(new UseItemAction());
        actions.add(new SellItemAction());
        actions.add(new CheckEventAction());
        actions.add(new CheckParameterAction());
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
    
    public Adventure getAdventure() {
        return adventure;
    }
    
    public String getGameYamlPath() {
        return gameYamlPath;
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
            hero.visitChapter(targetChapter);
            applyModifiers();
            String displayText = getDisplayText();
            adventureLog.log(String.format(Messages.get(Messages.Key.LOG_CHAPTER), targetChapter));
            adventureLog.log(displayText.replaceAll("\\n", " "));
        }
    }

    public boolean isGameOver() {
        return hero.getStamina() == 0;
    }

    public void goToChapter(int chapterIndex) {
        currentChapter = getChapter(chapterIndex);
        hero.visitChapter(chapterIndex);
        applyModifiers();
        String displayText = getDisplayText();
        adventureLog.log(String.format(Messages.get(Messages.Key.LOG_CHAPTER), chapterIndex));
        adventureLog.log(displayText.replaceAll("\\n", " "));
    }
    
    public AdventureLog getAdventureLog() {
        return adventureLog;
    }

    private void applyModifiers() {
        for (Map<String, Object> actionData : currentChapter.actions) {
            for (Action action : actions) {
                if (action.getActionType() == ActionType.PASSIVE && action.canHandle(actionData)) {
                    action.execute(this, actionData);
                }
            }
        }
        // Log any modifications that occurred
        List<String> mods = hero.getLastModifications();
        if (!mods.isEmpty()) {
            for (String mod : mods) {
                adventureLog.log("  " + mod);
            }
            // Don't clear here - let GameWindow handle it
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
    
    public SaveGame createSaveGame() {
        return new SaveGame(adventure.title, gameYamlPath, currentChapter.index, hero, adventureLog);
    }
    
    public void loadSaveGame(SaveGame saveGame) {
        // Restore hero state
        hero.setSkill(saveGame.getSkill());
        hero.setStamina(saveGame.getStamina());
        hero.setLuck(saveGame.getLuck());
        hero.setGold(saveGame.getGold());
        hero.setProvisions(saveGame.getProvisions());
        hero.setMaxSkill(saveGame.getMaxSkill());
        hero.setMaxStamina(saveGame.getMaxStamina());
        hero.setMaxLuck(saveGame.getMaxLuck());
        hero.setInventory(saveGame.getInventory());
        hero.setEvents(saveGame.getEvents());
        hero.setVisitedChapters(saveGame.getVisitedChapters());
        
        // Restore adventure log
        adventureLog.restoreEntries(saveGame.getLogEntries());
        adventureLog.log(Messages.get(Messages.Key.LOG_GAME_LOADED));
        
        // Clear modification history
        hero.clearModifications();
        
        // Go to saved chapter
        goToChapter(saveGame.getCurrentChapterIndex());
    }
}
