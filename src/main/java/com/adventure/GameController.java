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
    private Integer nextBattleAttackModifier;
    private String nextBattleEffectText;
    private Integer returnChapter; // Chapter to return to after interrupt
    private Battle savedBattle; // Saved battle state for return
    private Map<String, Object> savedBattleActionData; // Saved battle action data
    private boolean battleActive; // Track if battle is currently active

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
        actions.add(new WinAction());
        actions.add(new InterruptAction());
        actions.add(new ModifyAction());
        actions.add(new RandomModifyAction());
        actions.add(new RandomGotoAction());
        actions.add(new SetValueAction());
        actions.add(new RecordAction());
        actions.add(new NewEventAction());
        actions.add(new DeathAction());
        actions.add(new BattleAction());
        actions.add(new LuckAction());
        actions.add(new AttributeTestAction());
        actions.add(new EffectAction());
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
            if (actionData.containsKey("display")) {
                return (String) actionData.get("display");
            }
            if (actionData.containsKey("win")) {
                return (String) actionData.get("win");
            }
            if (actionData.containsKey("interrupt")) {
                return (String) actionData.get("interrupt");
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
        return new SaveGame(adventure.title, gameYamlPath, currentChapter.index, hero, adventureLog, nextBattleAttackModifier, nextBattleEffectText);
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
        
        // Restore battle effect
        this.nextBattleAttackModifier = saveGame.getNextBattleAttackModifier();
        this.nextBattleEffectText = saveGame.getNextBattleEffectText();
        
        // Clear modification history
        hero.clearModifications();
        
        // Go to saved chapter
        goToChapter(saveGame.getCurrentChapterIndex());
    }
    
    public void setNextBattleEffect(int attackModifier, String text) {
        this.nextBattleAttackModifier = attackModifier;
        this.nextBattleEffectText = text;
    }
    
    public Integer getNextBattleAttackModifier() {
        return nextBattleAttackModifier;
    }
    
    public String getNextBattleEffectText() {
        return nextBattleEffectText;
    }
    
    public void clearNextBattleEffect() {
        this.nextBattleAttackModifier = null;
        this.nextBattleEffectText = null;
    }
    
    public void setReturnChapter(int chapter) {
        this.returnChapter = chapter;
    }
    
    public Integer getReturnChapter() {
        return returnChapter;
    }
    
    public void clearReturnChapter() {
        this.returnChapter = null;
    }
    
    public void saveBattleState(Battle battle, Map<String, Object> actionData) {
        this.savedBattle = battle;
        this.savedBattleActionData = actionData;
    }
    
    public Battle getSavedBattle() {
        return savedBattle;
    }
    
    public Map<String, Object> getSavedBattleActionData() {
        return savedBattleActionData;
    }
    
    public void clearSavedBattle() {
        this.savedBattle = null;
        this.savedBattleActionData = null;
    }
    
    public boolean isBattleActive() {
        return battleActive;
    }
    
    public void setBattleActive(boolean active) {
        this.battleActive = active;
    }
}
