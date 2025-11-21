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
        ATTRIBUTE_CAPPED,
        ATTRIBUTE_BLOCKED,
        BATTLE_TITLE,
        BATTLE_BEGIN,
        BATTLE_NEXT_TURN,
        BATTLE_CLOSE,
        BATTLE_HERO,
        BATTLE_VICTORY,
        BATTLE_DEFEAT,
        LUCK_TEST_TITLE,
        LUCK_TEST_BUTTON,
        LUCK_LUCKY,
        LUCK_UNLUCKY,
        LUCK_CONTINUE
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
        ))
    );
    
    public static void setLanguage(String lang) {
        currentLanguage = lang;
    }
    
    public static String get(Key key) {
        return translations.get(key).getOrDefault(currentLanguage, translations.get(key).get("en"));
    }
}
