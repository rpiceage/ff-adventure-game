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
        ATTRIBUTE_BLOCKED
    }
    
    private static final Map<Key, Map<String, String>> translations = Map.of(
        Key.GAME_OVER, Map.of(
            "en", "Your adventure ends here.",
            "hu", "A kalandod itt véget ér."
        ),
        Key.HERO_STATS_TITLE, Map.of(
            "en", "Adventure sheet",
            "hu", "Kalandlap"
        ),
        Key.SKILL, Map.of(
            "en", "SKILL",
            "hu", "ÜGYESSÉG"
        ),
        Key.STAMINA, Map.of(
            "en", "STAMINA",
            "hu", "ÉLETERŐ"
        ),
        Key.LUCK, Map.of(
            "en", "LUCK",
            "hu", "SZERENCSE"
        ),
        Key.ATTRIBUTE_CAPPED, Map.of(
            "en", "capped at",
            "hu", "korlátozva"
        ),
        Key.ATTRIBUTE_BLOCKED, Map.of(
            "en", "would have been modified but initial value cannot be exceeded",
            "hu", "módosítva lett volna, de a kezdeti érték nem léphető túl"
        )
    );
    
    public static void setLanguage(String lang) {
        currentLanguage = lang;
    }
    
    public static String get(Key key) {
        return translations.get(key).getOrDefault(currentLanguage, translations.get(key).get("en"));
    }
}
