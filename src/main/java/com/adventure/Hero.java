package com.adventure;

import java.util.ArrayList;
import java.util.List;

public class Hero {
    private int skill;
    private int stamina;
    private int luck;
    private List<String> lastModifications;

    public Hero(int skill, int stamina, int luck) {
        this.skill = skill;
        this.stamina = stamina;
        this.luck = luck;
        this.lastModifications = new ArrayList<>();
    }

    public int getSkill() { return skill; }
    public int getStamina() { return stamina; }
    public int getLuck() { return luck; }

    public void modifySkill(int delta) { 
        this.skill += delta;
        lastModifications.add("SKILL " + (delta > 0 ? "+" : "") + delta);
    }
    
    public void modifyStamina(int delta) { 
        this.stamina += delta;
        lastModifications.add("STAMINA " + (delta > 0 ? "+" : "") + delta);
    }
    
    public void modifyLuck(int delta) { 
        this.luck += delta;
        lastModifications.add("LUCK " + (delta > 0 ? "+" : "") + delta);
    }

    public List<String> getLastModifications() {
        return new ArrayList<>(lastModifications);
    }

    public void clearModifications() {
        lastModifications.clear();
    }
}
