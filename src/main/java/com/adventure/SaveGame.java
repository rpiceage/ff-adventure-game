package com.adventure;

import java.util.List;

public class SaveGame {
    private String gameTitle;
    private String gameYamlPath;
    private int currentChapterIndex;
    
    // Hero attributes
    private int skill;
    private int stamina;
    private int luck;
    private int gold;
    private int provisions;
    private int maxSkill;
    private int maxStamina;
    private int maxLuck;
    
    // Hero collections
    private List<String> inventory;
    private List<String> events;
    private List<Integer> visitedChapters;
    
    public SaveGame() {
        // Default constructor for JSON deserialization
    }
    
    public SaveGame(String gameTitle, String gameYamlPath, int currentChapterIndex, Hero hero) {
        this.gameTitle = gameTitle;
        this.gameYamlPath = gameYamlPath;
        this.currentChapterIndex = currentChapterIndex;
        
        this.skill = hero.getSkill();
        this.stamina = hero.getStamina();
        this.luck = hero.getLuck();
        this.gold = hero.getGold();
        this.provisions = hero.getProvisions();
        this.maxSkill = hero.getInitialSkill();
        this.maxStamina = hero.getInitialStamina();
        this.maxLuck = hero.getInitialLuck();
        
        this.inventory = hero.getInventory();
        this.events = hero.getEvents();
        this.visitedChapters = hero.getVisitedChapters();
    }
    
    // Getters and setters
    public String getGameTitle() { return gameTitle; }
    public void setGameTitle(String gameTitle) { this.gameTitle = gameTitle; }
    
    public String getGameYamlPath() { return gameYamlPath; }
    public void setGameYamlPath(String gameYamlPath) { this.gameYamlPath = gameYamlPath; }
    
    public int getCurrentChapterIndex() { return currentChapterIndex; }
    public void setCurrentChapterIndex(int currentChapterIndex) { this.currentChapterIndex = currentChapterIndex; }
    
    public int getSkill() { return skill; }
    public void setSkill(int skill) { this.skill = skill; }
    
    public int getStamina() { return stamina; }
    public void setStamina(int stamina) { this.stamina = stamina; }
    
    public int getLuck() { return luck; }
    public void setLuck(int luck) { this.luck = luck; }
    
    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }
    
    public int getProvisions() { return provisions; }
    public void setProvisions(int provisions) { this.provisions = provisions; }
    
    public int getMaxSkill() { return maxSkill; }
    public void setMaxSkill(int maxSkill) { this.maxSkill = maxSkill; }
    
    public int getMaxStamina() { return maxStamina; }
    public void setMaxStamina(int maxStamina) { this.maxStamina = maxStamina; }
    
    public int getMaxLuck() { return maxLuck; }
    public void setMaxLuck(int maxLuck) { this.maxLuck = maxLuck; }
    
    public List<String> getInventory() { return inventory; }
    public void setInventory(List<String> inventory) { this.inventory = inventory; }
    
    public List<String> getEvents() { return events; }
    public void setEvents(List<String> events) { this.events = events; }
    
    public List<Integer> getVisitedChapters() { return visitedChapters; }
    public void setVisitedChapters(List<Integer> visitedChapters) { this.visitedChapters = visitedChapters; }
}
