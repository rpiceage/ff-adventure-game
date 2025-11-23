package com.adventure;

import java.util.ArrayList;
import java.util.List;

public class Hero {
    private int skill;
    private int stamina;
    private int luck;
    private int gold;
    private int provisions;
    private final int maxSkill;
    private final int maxStamina;
    private final int maxLuck;
    private List<String> lastModifications;
    private List<String> inventory;
    private List<String> events;

    public Hero(int skill, int stamina, int luck) {
        this(skill, stamina, luck, 0, 0);
    }

    public Hero(int skill, int stamina, int luck, int gold) {
        this(skill, stamina, luck, gold, 0);
    }

    public Hero(int skill, int stamina, int luck, int gold, int provisions) {
        this.skill = skill;
        this.stamina = stamina;
        this.luck = luck;
        this.gold = gold;
        this.provisions = provisions;
        this.maxSkill = skill;
        this.maxStamina = stamina;
        this.maxLuck = luck;
        this.lastModifications = new ArrayList<>();
        this.inventory = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    public int getSkill() { return skill; }
    public int getStamina() { return stamina; }
    public int getLuck() { return luck; }
    public int getGold() { return gold; }
    public int getInitialSkill() { return maxSkill; }
    public int getInitialStamina() { return maxStamina; }
    public int getInitialLuck() { return maxLuck; }

    public void modifySkill(int delta) { 
        int oldValue = this.skill;
        int newValue = this.skill + delta;
        this.skill = Math.max(0, Math.min(newValue, maxSkill));
        int actualChange = this.skill - oldValue;
        if (actualChange == 0 && delta > 0) {
            lastModifications.add(Messages.get(Messages.Key.SKILL) + " " + Messages.get(Messages.Key.ATTRIBUTE_BLOCKED));
        } else if (actualChange != delta && delta > 0) {
            lastModifications.add(Messages.get(Messages.Key.SKILL) + " +" + actualChange + " (" + Messages.get(Messages.Key.ATTRIBUTE_CAPPED) + " " + maxSkill + ")");
        } else {
            lastModifications.add(Messages.get(Messages.Key.SKILL) + " " + (delta > 0 ? "+" : "") + delta);
        }
    }
    
    public void modifyStamina(int delta) { 
        int oldValue = this.stamina;
        int newValue = this.stamina + delta;
        this.stamina = Math.max(0, Math.min(newValue, maxStamina));
        int actualChange = this.stamina - oldValue;
        if (actualChange == 0 && delta > 0) {
            lastModifications.add(Messages.get(Messages.Key.STAMINA) + " " + Messages.get(Messages.Key.ATTRIBUTE_BLOCKED));
        } else if (actualChange != delta && delta > 0) {
            lastModifications.add(Messages.get(Messages.Key.STAMINA) + " +" + actualChange + " (" + Messages.get(Messages.Key.ATTRIBUTE_CAPPED) + " " + maxStamina + ")");
        } else {
            lastModifications.add(Messages.get(Messages.Key.STAMINA) + " " + (delta > 0 ? "+" : "") + delta);
        }
    }
    
    public void modifyLuck(int delta) { 
        int oldValue = this.luck;
        int newValue = this.luck + delta;
        this.luck = Math.max(0, Math.min(newValue, maxLuck));
        int actualChange = this.luck - oldValue;
        if (actualChange == 0 && delta > 0) {
            lastModifications.add(Messages.get(Messages.Key.LUCK) + " " + Messages.get(Messages.Key.ATTRIBUTE_BLOCKED));
        } else if (actualChange != delta && delta > 0) {
            lastModifications.add(Messages.get(Messages.Key.LUCK) + " +" + actualChange + " (" + Messages.get(Messages.Key.ATTRIBUTE_CAPPED) + " " + maxLuck + ")");
        } else {
            lastModifications.add(Messages.get(Messages.Key.LUCK) + " " + (delta > 0 ? "+" : "") + delta);
        }
    }

    public void modifyGold(int delta) {
        int newValue = this.gold + delta;
        this.gold = Math.max(0, newValue);
        lastModifications.add(Messages.get(Messages.Key.GOLD) + " " + (delta > 0 ? "+" : "") + delta);
    }

    public void modifyStaminaSilent(int delta) {
        int newValue = this.stamina + delta;
        this.stamina = Math.max(0, Math.min(newValue, maxStamina));
    }

    public void modifyLuckSilent(int delta) {
        int newValue = this.luck + delta;
        this.luck = Math.max(0, Math.min(newValue, maxLuck));
    }

    public List<String> getLastModifications() {
        return new ArrayList<>(lastModifications);
    }

    public void clearModifications() {
        lastModifications.clear();
    }

    public void addItem(String itemName) {
        inventory.add(itemName);
    }

    public List<String> getInventory() {
        return new ArrayList<>(inventory);
    }

    public boolean hasItem(String itemName) {
        return inventory.contains(itemName);
    }

    public void addEvent(String eventName) {
        events.add(eventName);
    }

    public boolean hasEvent(String eventName) {
        return events.contains(eventName);
    }

    public List<String> getEvents() {
        return new ArrayList<>(events);
    }

    public int getProvisions() {
        return provisions;
    }

    public void modifyProvisions(int delta) {
        int newValue = this.provisions + delta;
        this.provisions = Math.max(0, newValue);
        lastModifications.add(Messages.get(Messages.Key.PROVISIONS) + " " + (delta > 0 ? "+" : "") + delta);
    }

    public boolean consumeProvision() {
        if (provisions <= 0) {
            return false;
        }
        if (stamina >= maxStamina) {
            return false;
        }
        provisions--;
        modifyStaminaSilent(4);
        return true;
    }
}
