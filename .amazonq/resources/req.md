# Create new ff application

## Requirements
- Desktop application in Java using Swing
- Handles a YAML document as input, like the requirements/sample.yaml
- Displays adventure text in a scrollable text area
- Handles navigation choices with buttons at the bottom
- Shows hero statistics in a panel on the right side
- Displays temporary notifications for attribute changes in the lower left corner
- Supports internationalization (English and Hungarian)

## Internationalization
- Optional `language` field in YAML (defaults to "en")
- Supported languages: "en" (English), "hu" (Hungarian)
- All UI strings must be added to the Messages class with translations
- All UI strings are translated including:
  - Game over message
  - Adventure sheet title
  - Attribute names (SKILL/ÜGYESSÉG, STAMINA/ÉLETERŐ, LUCK/SZERENCSE)
  - Notification messages
  - Battle UI strings

## Hero Attributes
- Hero has four attributes: SKILL, STAMINA, LUCK, and GOLD
- Default initial values: SKILL: 12, STAMINA: 24, LUCK: 12, GOLD: 0
- Initial GOLD can be set in YAML with `init: gold: 10`
- Attributes displayed in a stats panel on the right side of the window
- SKILL, STAMINA, LUCK show current value and initial value in parentheses
- GOLD shows only current value (no initial value displayed)
- Attributes can be modified through YAML chapter actions using the `modify` action
- SKILL, STAMINA, LUCK values cannot exceed their initial values (capped at max)
- SKILL, STAMINA, LUCK, GOLD values cannot go below 0 (capped at min)
- GOLD can exceed initial value (no upper cap)
- Game ends immediately when STAMINA reaches 0, displaying "Your adventure ends here."

## Attribute Modifications
- YAML format for modifications:
  ```yaml
  - modify:
      values:
        - field: STAMINA
          value: -3
        - field: LUCK
          value: 2
        - field: GOLD
          value: 10
  ```
- Modifications are applied when entering a chapter (including chapter 0)
- A notification popup appears in the lower left corner showing all attribute changes
- Notification auto-dismisses after 3 seconds
- Notification messages:
  - Normal: "SKILL +1" or "STAMINA -5" or "GOLD +10"
  - Capped: "STAMINA +3 (capped at 24)"
  - Blocked: "LUCK would have been modified but initial value cannot be exceeded"

## Initial Values
- Optional `init` section in YAML to set initial attribute values:
  ```yaml
  init:
    gold: 10
  ```
- If not specified, GOLD defaults to 0


## Battle System
- Turn-based combat system triggered by `battle` action in YAML
- YAML format for battles:
  ```yaml
  - battle:
      enemies:
        - enemy: Cave Man
          skill: 12
          stamina: 20
      win: 1  # chapter to go to after victory
  ```
- Battle mechanics (single enemy):
  - Each turn: both hero and enemy roll 2d6 and add their SKILL value
  - Higher attack value wins the turn
  - Loser takes 2 STAMINA damage
  - Draw results in no damage
- Battle mechanics (multiple enemies):
  - Hero rolls 2d6 separately against EACH alive enemy
  - Hero selects ONE enemy as target using radio buttons
  - If hero's roll beats selected enemy: that enemy takes 2 STAMINA damage
  - For each enemy that beats hero in their roll: hero takes 2 STAMINA damage
  - Hero can take 0, 2, 4, 6... damage per turn (2 per enemy that wins)
  - Battle ends when all enemies dead or hero dies
- Battle UI:
  - Enemy stats panel at top showing all enemies with SKILL and STAMINA
  - Radio buttons to select target enemy (bold text shows selected)
  - Dead enemies have disabled radio buttons
  - Hero stats continuously updated in side panel during battle
  - Animated dice panel with table.jpg background showing dice rolls
  - Scrollable battle log in center showing all turn results
  - "Next Turn" button to execute each turn
- Dice animation:
  - Single enemy: 4 dice (2 for hero, 2 for enemy) in one row
  - Multiple enemies: 4 dice per enemy (2 hero + 2 enemy) in separate rows
  - Dice panel height scales dynamically: 100px per enemy
  - Dice arranged in stable vertical layout using BoxLayout
  - Dice spin independently at different speeds for 1 second
  - Unicode dice characters (⚀-⚅) with white backgrounds
  - Graphics2D rotation for smooth spinning effect
  - After animation, dice show actual rolled values
  - Next Turn button disabled during animation
- Victory: displays victory message and "Continue" button to proceed to win chapter
- Defeat: hero STAMINA reaches 0, triggers game over
- Battle damage is applied silently (no notification popups during combat)

## Luck Test System
- Test your luck action triggered by `luck` action in YAML
- YAML format for luck tests:
  ```yaml
  - luck:
      lucky: 1    # chapter to go to if lucky
      unlucky: 2  # chapter to go to if unlucky
  ```
- Luck test mechanics:
  - Roll 2d6 and compare to hero's LUCK attribute
  - If roll ≤ LUCK: "You were lucky!" → go to lucky chapter
  - If roll > LUCK: "You were unlucky!" → go to unlucky chapter
  - Testing luck decreases LUCK by 1 (applied silently, no notification)
- Luck test UI:
  - "Test your luck!" button appears when luck action is present
  - Animated dice panel with table.jpg background (2 dice)
  - Result message displayed in text area with parchment background
  - "Continue" button to proceed to appropriate chapter
- Dice animation same as battle system (spinning, white backgrounds)

## Architecture
- Action-based system for extensibility
- All action types implement `Action` interface:
  - `canHandle(actionData)` - checks if action can handle the data
  - `getActionType()` - returns ActionType enum
  - `execute(controller, actionData)` - performs the action
  - `getButtonText()` - for SINGLE_BUTTON actions
  - `getChoices(actionData)` - for MULTIPLE_BUTTONS actions
- ActionType enum: SINGLE_BUTTON, MULTIPLE_BUTTONS, PASSIVE, DISPLAY
- Implemented actions:
  - `DisplayAction` (DISPLAY) - shows chapter text
  - `ModifyAction` (PASSIVE) - auto-applies attribute modifications
  - `BattleAction` (SINGLE_BUTTON) - triggers battle encounters
  - `LuckAction` (SINGLE_BUTTON) - triggers luck tests
  - `GotoAction` (MULTIPLE_BUTTONS) - provides navigation choices
- DiceAnimator class handles all dice animations (battle and luck)
- GameController manages action registry and execution
- GameWindow handles UI based on ActionType generically
- No instanceof checks - uses ActionType for behavior
- Easy to add new action types without modifying existing code

## UI Architecture
- Separation of concerns between main window and feature-specific UI
- GameWindow - main window, stats panel, navigation
- BattleUI - self-contained battle UI logic
- LuckUI - self-contained luck test UI logic
- Feature UIs are independently testable and maintainable
- GameWindow provides updateHeroStats() for real-time stat updates
- Proper component lifecycle management with panel tracking
