package com.adventure;

public class Hero {
    private int skill;
    private int stamina;
    private int luck;

    public Hero(int skill, int stamina, int luck) {
        this.skill = skill;
        this.stamina = stamina;
        this.luck = luck;
    }

    public int getSkill() { return skill; }
    public int getStamina() { return stamina; }
    public int getLuck() { return luck; }

    public void modifySkill(int delta) { this.skill += delta; }
    public void modifyStamina(int delta) { this.stamina += delta; }
    public void modifyLuck(int delta) { this.luck += delta; }
}
