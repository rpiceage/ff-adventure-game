package com.adventure;

public class Item {
    private String name;
    private boolean useAnyTime;
    private ItemEffect effect;
    
    public Item(String name) {
        this(name, false, null);
    }
    
    public Item(String name, boolean useAnyTime, ItemEffect effect) {
        this.name = name;
        this.useAnyTime = useAnyTime;
        this.effect = effect;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean canUseAnyTime() {
        return useAnyTime;
    }
    
    public boolean hasEffect() {
        return effect != null;
    }
    
    public void use(Hero hero) {
        if (effect != null) {
            effect.apply(hero);
        }
    }
    
    public static Item createPotion(String potionName) {
        ItemEffect effect = null;
        
        if (potionName.equals(Messages.get(Messages.Key.POTION_SKILL))) {
            effect = hero -> hero.setSkill(hero.getInitialSkill());
        } else if (potionName.equals(Messages.get(Messages.Key.POTION_STAMINA))) {
            effect = hero -> hero.setStamina(hero.getInitialStamina());
        } else if (potionName.equals(Messages.get(Messages.Key.POTION_LUCK))) {
            effect = hero -> hero.setLuck(hero.getInitialLuck());
        }
        
        return new Item(potionName, true, effect);
    }
    
    @FunctionalInterface
    public interface ItemEffect {
        void apply(Hero hero);
    }
}
