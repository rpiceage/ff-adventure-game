package com.adventure;

import java.util.ArrayList;
import java.util.List;

public class Hero {
    private int skill;
    private int stamina;
    private int luck;
    private int gold;
    private final int maxSkill;
    private final int maxStamina;
    private final int maxLuck;
    private List<String> lastModifications;

    public Hero(int skill, int stamina, int luck) {
        this(skill, stamina, luck, 0);
    }

    public Hero(int skill, int stamina, int luck, int gold) {
        this.skill = skill;
        this.stamina = stamina;
        this.luck = luck;
        this.gold = gold;
        this.maxSkill = skill;
        this.maxStamina = stamina;
        this.maxLuck = luck;
        this.lastModifications = new ArrayList<>();
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
}
