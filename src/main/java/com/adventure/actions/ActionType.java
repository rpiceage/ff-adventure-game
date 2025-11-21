package com.adventure.actions;

public enum ActionType {
    SINGLE_BUTTON,    // Battle, Luck - one button triggers action
    MULTIPLE_BUTTONS, // Goto - multiple choice buttons
    PASSIVE,          // Modify - auto-applied, no UI
    DISPLAY           // Display - just shows text, no interaction
}
