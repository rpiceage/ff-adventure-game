package com.adventure;

import java.util.ArrayList;
import java.util.List;

public class Hero {
    private int skill;
    private int stamina;
    private int luck;
    private final int maxSkill;
    private final int maxStamina;
    private final int maxLuck;
    private List<String> lastModifications;

    public Hero(int skill, int stamina, int luck) {
        this.skill = skill;
        this.stamina = stamina;
        this.luck = luck;
        this.maxSkill = skill;
        this.maxStamina = stamina;
        this.maxLuck = luck;
        this.lastModifications = new ArrayList<>();
    }

    public int getSkill() { return skill; }
    public int getStamina() { return stamina; }
    public int getLuck() { return luck; }

    public void modifySkill(int delta) { 
        int oldValue = this.skill;
        this.skill = Math.min(this.skill + delta, maxSkill);
        int actualChange = this.skill - oldValue;
        if (actualChange == 0 && delta > 0) {
            lastModifications.add("SKILL would have been modified but initial value cannot be exceeded");
        } else if (actualChange != delta && delta > 0) {
            lastModifications.add("SKILL +" + actualChange + " (capped at " + maxSkill + ")");
        } else {
            lastModifications.add("SKILL " + (delta > 0 ? "+" : "") + delta);
        }
    }
    
    public void modifyStamina(int delta) { 
        int oldValue = this.stamina;
        this.stamina = Math.min(this.stamina + delta, maxStamina);
        int actualChange = this.stamina - oldValue;
        if (actualChange == 0 && delta > 0) {
            lastModifications.add("STAMINA would have been modified but initial value cannot be exceeded");
        } else if (actualChange != delta && delta > 0) {
            lastModifications.add("STAMINA +" + actualChange + " (capped at " + maxStamina + ")");
        } else {
            lastModifications.add("STAMINA " + (delta > 0 ? "+" : "") + delta);
        }
    }
    
    public void modifyLuck(int delta) { 
        int oldValue = this.luck;
        this.luck = Math.min(this.luck + delta, maxLuck);
        int actualChange = this.luck - oldValue;
        if (actualChange == 0 && delta > 0) {
            lastModifications.add("LUCK would have been modified but initial value cannot be exceeded");
        } else if (actualChange != delta && delta > 0) {
            lastModifications.add("LUCK +" + actualChange + " (capped at " + maxLuck + ")");
        } else {
            lastModifications.add("LUCK " + (delta > 0 ? "+" : "") + delta);
        }
    }

    public List<String> getLastModifications() {
        return new ArrayList<>(lastModifications);
    }

    public void clearModifications() {
        lastModifications.clear();
    }
}
