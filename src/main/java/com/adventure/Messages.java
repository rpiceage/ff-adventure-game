package com.adventure;

import java.util.Map;

public class Messages {
    private static String currentLanguage = "en";
    
    public enum Key {
        GAME_OVER,
        HERO_STATS_TITLE,
        SKILL,
        STAMINA,
        LUCK,
        GOLD,
        ATTRIBUTE_CAPPED,
        ATTRIBUTE_BLOCKED,
        BATTLE_TITLE,
        BATTLE_BEGIN,
        BATTLE_NEXT_TURN,
        BATTLE_CLOSE,
        BATTLE_HERO,
        BATTLE_VICTORY,
        BATTLE_DEFEAT,
        BATTLE_VICTORY_ALL,
        BATTLE_DEFEAT_GENERAL,
        LUCK_TEST_TITLE,
        LUCK_TEST_BUTTON,
        LUCK_LUCKY,
        LUCK_UNLUCKY,
        LUCK_CONTINUE,
        ITEMS_TITLE,
        ITEM_CANT_USE,
        ADD_ITEM,
        PROVISIONS,
        PROVISIONS_NOT_HUNGRY,
        BATTLE_VS,
        BATTLE_LOSES_STAMINA,
        BATTLE_WINS_NOT_TARGETING,
        BATTLE_HERO_LOSES,
        BATTLE_DRAW,
        BATTLE_HERO_TAKES_DAMAGE,
        BATTLE_ESCAPE,
        ATTRIBUTE_TEST_TITLE,
        ATTRIBUTE_TEST_BUTTON,
        ATTRIBUTE_TEST_SUCCESS,
        ATTRIBUTE_TEST_FAIL,
        ROLL_DICE,
        ROLL_LABEL,
        CONTINUE
    }
    
    private static final Map<Key, Map<String, String>> translations = Map.ofEntries(
        Map.entry(Key.GAME_OVER, Map.of(
            "en", "Your adventure ends here.",
            "hu", "A kalandod itt véget ér."
        )),
        Map.entry(Key.HERO_STATS_TITLE, Map.of(
            "en", "Adventure sheet",
            "hu", "Kalandlap"
        )),
        Map.entry(Key.SKILL, Map.of(
            "en", "SKILL",
            "hu", "ÜGYESSÉG"
        )),
        Map.entry(Key.STAMINA, Map.of(
            "en", "STAMINA",
            "hu", "ÉLETERŐ"
        )),
        Map.entry(Key.LUCK, Map.of(
            "en", "LUCK",
            "hu", "SZERENCSE"
        )),
        Map.entry(Key.GOLD, Map.of(
            "en", "GOLD",
            "hu", "ARANY"
        )),
        Map.entry(Key.ATTRIBUTE_CAPPED, Map.of(
            "en", "capped at",
            "hu", "korlátozva"
        )),
        Map.entry(Key.ATTRIBUTE_BLOCKED, Map.of(
            "en", "would have been modified but initial value cannot be exceeded",
            "hu", "módosítva lett volna, de a kezdeti érték nem léphető túl"
        )),
        Map.entry(Key.BATTLE_TITLE, Map.of(
            "en", "Battle",
            "hu", "Harc"
        )),
        Map.entry(Key.BATTLE_BEGIN, Map.of(
            "en", "Let's begin",
            "hu", "Kezdjük"
        )),
        Map.entry(Key.BATTLE_NEXT_TURN, Map.of(
            "en", "Next turn",
            "hu", "Következő kör"
        )),
        Map.entry(Key.BATTLE_CLOSE, Map.of(
            "en", "Close",
            "hu", "Bezár"
        )),
        Map.entry(Key.BATTLE_HERO, Map.of(
            "en", "Hero",
            "hu", "Hős"
        )),
        Map.entry(Key.BATTLE_VICTORY, Map.of(
            "en", "Victory! You defeated",
            "hu", "Győzelem! Legyőzted:"
        )),
        Map.entry(Key.BATTLE_DEFEAT, Map.of(
            "en", "Defeat! %s has defeated you!",
            "hu", "Vereség! %s legyőzött téged!"
        )),
        Map.entry(Key.BATTLE_VICTORY_ALL, Map.of(
            "en", "Victory! You defeated all enemies!",
            "hu", "Győzelem! Legyőztél minden ellenséget!"
        )),
        Map.entry(Key.BATTLE_DEFEAT_GENERAL, Map.of(
            "en", "Defeat! You have been defeated!",
            "hu", "Vereség! Legyőztek!"
        )),
        Map.entry(Key.LUCK_TEST_TITLE, Map.of(
            "en", "Test your luck",
            "hu", "Tedd próbára szerencséd"
        )),
        Map.entry(Key.LUCK_TEST_BUTTON, Map.of(
            "en", "Test your luck!",
            "hu", "Tedd próbára szerencséd!"
        )),
        Map.entry(Key.LUCK_LUCKY, Map.of(
            "en", "You were lucky!",
            "hu", "Szerencséd volt!"
        )),
        Map.entry(Key.LUCK_UNLUCKY, Map.of(
            "en", "You were unlucky!",
            "hu", "Nem volt szerencséd!"
        )),
        Map.entry(Key.LUCK_CONTINUE, Map.of(
            "en", "Continue",
            "hu", "Tovább"
        )),
        Map.entry(Key.ITEMS_TITLE, Map.of(
            "en", "Items",
            "hu", "Tárgyak"
        )),
        Map.entry(Key.ITEM_CANT_USE, Map.of(
            "en", "This item can't be used right now",
            "hu", "Ez a tárgy most nem használható"
        )),
        Map.entry(Key.ADD_ITEM, Map.of(
            "en", "Take",
            "hu", "Felvesz"
        )),
        Map.entry(Key.PROVISIONS, Map.of(
            "en", "Provisions",
            "hu", "Élelem"
        )),
        Map.entry(Key.PROVISIONS_NOT_HUNGRY, Map.of(
            "en", "You are not hungry right now :)",
            "hu", "Most nem vagy éhes :)"
        )),
        Map.entry(Key.BATTLE_VS, Map.of(
            "en", "vs",
            "hu", "vs"
        )),
        Map.entry(Key.BATTLE_LOSES_STAMINA, Map.of(
            "en", "loses 2 STAMINA",
            "hu", "veszít 2 ÉLETERŐT"
        )),
        Map.entry(Key.BATTLE_WINS_NOT_TARGETING, Map.of(
            "en", "Hero wins but not targeting this enemy",
            "hu", "Hős nyer, de nem ezt az ellenséget célozza"
        )),
        Map.entry(Key.BATTLE_HERO_LOSES, Map.of(
            "en", "Hero loses",
            "hu", "Hős veszít"
        )),
        Map.entry(Key.BATTLE_DRAW, Map.of(
            "en", "Draw",
            "hu", "Döntetlen"
        )),
        Map.entry(Key.BATTLE_HERO_TAKES_DAMAGE, Map.of(
            "en", "Hero takes %d STAMINA damage total",
            "hu", "Hős összesen %d ÉLETERŐ sebzést kap"
        )),
        Map.entry(Key.ATTRIBUTE_TEST_TITLE, Map.of(
            "en", "Test your attribute",
            "hu", "Tedd próbára képességed"
        )),
        Map.entry(Key.ATTRIBUTE_TEST_BUTTON, Map.of(
            "en", "Test attribute!",
            "hu", "Tedd próbára képességed!"
        )),
        Map.entry(Key.ATTRIBUTE_TEST_SUCCESS, Map.of(
            "en", "Success!",
            "hu", "Sikeres!"
        )),
        Map.entry(Key.ATTRIBUTE_TEST_FAIL, Map.of(
            "en", "Failed!",
            "hu", "Sikertelen!"
        )),
        Map.entry(Key.BATTLE_ESCAPE, Map.of(
            "en", "Escape (-2 STAMINA)",
            "hu", "Menekülés (-2 ÉLETERŐ)"
        )),
        Map.entry(Key.ROLL_DICE, Map.of(
            "en", "Roll dice",
            "hu", "Dobj kockával"
        )),
        Map.entry(Key.ROLL_LABEL, Map.of(
            "en", "Roll!",
            "hu", "Dobj!"
        )),
        Map.entry(Key.CONTINUE, Map.of(
            "en", "Continue",
            "hu", "Tovább"
        ))
    );
    
    public static void setLanguage(String lang) {
        currentLanguage = lang;
    }
    
    public static String get(Key key) {
        return translations.get(key).getOrDefault(currentLanguage, translations.get(key).get("en"));
    }
}
