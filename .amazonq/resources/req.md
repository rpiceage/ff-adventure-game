# Create new ff application

## Requirements
- Desktop application in Java using Swing
- Welcome menu with game selection when started without arguments
- Handles a YAML document as input, like the requirements/sample.yaml
- Displays adventure text in a scrollable text area with parchment background
- Handles navigation choices with buttons at the bottom
- Shows hero statistics in a panel on the right side with wall background
- Displays temporary notifications for attribute changes in the lower left corner
- Supports internationalization (English and Hungarian)

## Welcome Menu
- Shown when application starts without command-line arguments
- Menu options:
  - New Game: Opens game selection screen
  - Load Game: Opens file chooser to load a saved game
- Game Selection Screen:
  - Dynamically scans resources/books/ directory for YAML files
  - Displays game titles from YAML metadata
  - Clicking a game starts it
  - Back button returns to welcome menu
- Command-line argument still supported: `java -jar ff.jar path/to/game.yaml`

## Save/Load Game
- Save game via File → Save Game menu in game window
- Load game via:
  - Welcome menu "Load Game" button
  - File → Load Game menu in game window
- Save file format:
  - JSON format with `.ffsave` extension
  - Default location: `~/.ff/saves/`
  - Default filename: `{game-title}_{timestamp}.ffsave`
  - User can choose any location via file chooser
- Saved state includes:
  - Game title and YAML resource path
  - Current chapter index
  - All hero attributes (skill, stamina, luck, gold, provisions)
  - Max attributes (initial values)
  - Complete inventory (items)
  - Recorded events
  - Visited chapters list
- Loading a save:
  - Restores complete game state
  - Loads correct game YAML from resources
  - Applies saved language setting
  - If loading different game from menu, creates new window
  - If loading same game from menu, updates current window
- SaveGameManager handles all save/load operations
- Uses Jackson library for JSON serialization

## Internationalization
- Optional `language` field in YAML (defaults to "en")
- Supported languages: "en" (English), "hu" (Hungarian)
- All UI strings must be added to the Messages class with translations
- All UI strings are translated including:
  - Game over message
  - Adventure sheet title
  - Attribute names (SKILL/ÜGYESSÉG, STAMINA/ÉLETERŐ, LUCK/SZERENCSE, GOLD/ARANY, PROVISIONS/ÉLELEM)
  - Notification messages
  - Battle UI strings
  - Provisions messages

## Hero Attributes
- Hero has five attributes: SKILL, STAMINA, LUCK, GOLD, and PROVISIONS
- Default initial values: SKILL: 12, STAMINA: 24, LUCK: 12, GOLD: 0, PROVISIONS: 0
- Initial GOLD and PROVISIONS can be set in YAML with `init: gold: 10, provisions: 3`
- Attributes displayed in a stats panel on the right side of the window with wall.jpg background
- SKILL, STAMINA, LUCK show current value and initial value in parentheses
- GOLD and PROVISIONS show only current value (no initial value displayed)
- All text in character sheet is white with semi-transparent background for readability
- Attributes can be modified through YAML chapter actions using the `modify` action
- SKILL, STAMINA, LUCK values cannot exceed their initial values (capped at max)
- SKILL, STAMINA, LUCK, GOLD values cannot go below 0 (capped at min)
- GOLD can exceed initial value (no upper cap)
- Game ends immediately when STAMINA reaches 0, displaying "Your adventure ends here."

## Provisions System
- Hero can carry provisions to restore stamina
- Provisions count set in YAML init section (defaults to 0)
- YAML format for initial provisions:
  ```yaml
  init:
    provisions: 3
  ```
- Provisions mechanics:
  - Each provision restores 4 STAMINA when consumed
  - STAMINA restoration is capped at maximum value
  - Cannot consume when STAMINA is at maximum (shows "You are not hungry right now :)" popup)
  - Cannot consume when no provisions left
  - Provisions can be consumed at any time except during battle
- Provisions UI:
  - "Provisions: X" button displayed in character sheet
  - Button shows current provisions count
  - Button disabled during battle
  - Button disabled when provisions = 0
  - Clicking button consumes one provision and restores 4 STAMINA

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
        - field: PROVISIONS
          value: 2
  ```
- Valid attributes: SKILL, STAMINA, LUCK, GOLD, PROVISIONS
- Unknown attributes throw IllegalArgumentException with helpful error message
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
      mode: 0  # optional: 0=simultaneous (default), 1=sequential
      enemies:
        - enemy: Cave Man
          skill: 12
          stamina: 20
      modifier:  # optional: modify hero's attack strength
        attribute: SKILL
        value: -2
        text: Deduct 2 from your Attack Strength each round.
      interrupt:  # optional: end battle when enemy reaches this stamina
        stamina: 3
      win: 1  # chapter to go to after victory
      winText: The enemy surrenders!  # optional: custom victory message
  ```
- Battle mechanics (single enemy):
  - Each turn: both hero and enemy roll 2d6 and add their SKILL value
  - Higher attack value wins the turn
  - Loser takes 2 STAMINA damage
  - Draw results in no damage
- Battle mechanics (multiple enemies - simultaneous mode, mode: 0):
  - Hero rolls 2d6 separately against EACH alive enemy
  - Hero selects ONE enemy as target using radio buttons
  - If hero's roll beats selected enemy: that enemy takes 2 STAMINA damage
  - For each enemy that beats hero in their roll: hero takes 2 STAMINA damage
  - Hero can take 0, 2, 4, 6... damage per turn (2 per enemy that wins)
  - Battle ends when all enemies dead or hero dies
- Battle mechanics (multiple enemies - sequential mode, mode: 1):
  - Hero fights enemies one at a time
  - Only the first alive enemy attacks each turn
  - Hero takes 0 or 2 damage per turn (from current enemy only)
  - When an enemy dies, next enemy becomes active
  - Battle ends when all enemies dead or hero dies
- Battle interrupt:
  - Optional `interrupt.stamina` threshold
  - Battle ends when first enemy's stamina reaches or goes below threshold
  - Counts as hero victory (enemy surrenders/flees)
  - Enemy remains alive but battle is over
- Custom victory text:
  - Optional `winText` field for custom victory message
  - Replaces default "You have defeated all enemies!" message
  - Can be used with or without interrupt
- Battle modifier:
  - Optional `modifier` field to adjust hero's attack strength
  - `modifier.attribute` specifies which attribute (currently only SKILL supported)
  - `modifier.value` is added to hero's attack calculation (can be negative)
  - `modifier.text` explains the modifier to the player
  - Modifier text is displayed in battle log at start
  - Modifier applies to every turn throughout the battle
- Battle escape:
  - Optional `escape.turn` specifies after which turn escape becomes available
  - Optional `escape.chapter` specifies where to go when escaping
  - Escape button appears alongside "Next Turn" button when turn >= escape.turn
  - Clicking escape costs 2 STAMINA and navigates to escape chapter
  - Optional `battleText` to inform player about escape option (added to battle log)
- Battle UI:
  - Enemy stats panel at top showing all enemies with SKILL and STAMINA
  - Simultaneous mode: Radio buttons to select target enemy (bold text shows selected)
  - Sequential mode: Labels only (no target selection), current enemy is bolded
  - Dead enemies have disabled radio buttons
  - Hero stats continuously updated in side panel during battle
  - Animated dice panel with table.jpg background showing dice rolls
  - Scrollable battle log in center showing all turn results
  - "Next Turn" button to execute each turn
- Dice animation:
  - Dice displayed as: Hero: [dice][dice] Enemy: [dice][dice] in one row per enemy
  - Dice drawn with dots (not Unicode characters) for cross-platform compatibility
  - Dice panel height scales dynamically: 100px per enemy
  - Dice spin independently at different speeds for 1 second
  - After animation, dice show actual rolled values
  - Next Turn button disabled during animation
  - Dice animation shows for killing blow
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

## Item System
- Hero has inventory to collect items
- Items displayed as buttons in stats panel
- YAML format for adding items:
  ```yaml
  - addItem:
      items:
        - name: Aranygyűrű
        - name: Sword
        - name: Potion
  ```
- Item mechanics:
  - Each item in addItem action creates a "Take [ItemName]" button
  - Clicking "Take" button adds item to hero's inventory
  - Button becomes disabled after taking item (prevents duplicates)
  - Items persist across chapters
  - Duplicate items allowed (can take same item from different chapters)
- Item UI:
  - Items section in stats panel below attributes
  - Each collected item shown as a button
  - Clicking item button shows popup: "This item can't be used right now"
  - Items displayed in order collected
- Items are stored as strings (item names)

## Lose Item System
- Items can be removed from hero's inventory
- YAML format for losing items:
  ```yaml
  - loseItem:
      lose:
        - Shield
        - Sword
  ```
- YAML format for losing all items except specific ones:
  ```yaml
  - loseItem:
      all:
        except:
          - Sword
  ```
- Lose item mechanics:
  - `lose` field: removes specific items from inventory
  - `all` field: removes all items, optionally keeping items in `except` list
  - Applied automatically when entering chapter (PASSIVE action)
  - No notification shown (silent operation)
  - Items not in inventory are silently ignored
- Lose item UI:
  - No UI interaction required
  - Item buttons disappear when items are removed
  - Useful for story events (losing backpack, theft, etc.)

## Use Item System
- Items can be used in specific chapters to navigate to different chapters
- YAML format for using items:
  ```yaml
  - useItem:
      - item: Dagger
        chapter: 234
      - item: Key
        chapter: 100
  ```
- Use item mechanics:
  - When a chapter contains a useItem action, item buttons become active for specified items
  - Clicking an active item button navigates to the specified chapter
  - Items only usable in chapters that have useItem action for them
  - In other chapters, item buttons show "This item can't be used right now" popup
  - Multiple items can be made usable in the same chapter
- Use item UI:
  - Item buttons in stats panel change behavior based on current chapter
  - Active items navigate when clicked
  - Inactive items show popup message
  - No visual distinction between active/inactive (discovered through interaction)

## Event System
- Events track significant story moments that affect later choices
- Hero maintains a list of recorded events
- Events persist throughout the game
- YAML format for recording events:
  ```yaml
  - newEvent:
      name: Djinn
      description: Hero freed a Djinn, and was granted a wish.
  ```
- Event mechanics:
  - Events are automatically recorded when entering a chapter with newEvent action
  - Events are stored by name (description is for documentation only)
  - Multiple events can be tracked simultaneously
  - Events cannot be removed once recorded

## Check Event System
- Conditional navigation based on recorded events or possessed items
- Checks for BOTH events AND items (either triggers the "existing" path)
- YAML format for checking events:
  ```yaml
  - checkEvent:
      name:
        - Elf-boots
      existing:
        chapter: 128
        text: As you are wearing elf-boots...
      missing:
        chapter: 374
        text: As you are wearing normal boots...
  ```
- Check event mechanics:
  - Checks if hero has ANY of the listed events OR items
  - If found: shows "existing" button that navigates to existing chapter
  - If not found: shows "missing" button that navigates to missing chapter
  - Only ONE button is shown based on the check result
  - Multiple names can be checked (OR logic - any match triggers existing path)
- Check event UI:
  - Single button displayed based on event/item check
  - Button text comes from existing or missing text field
  - Seamlessly integrates with other navigation options

## Set Value System
- Sets hero attribute to a specific value
- YAML format for setting values:
  ```yaml
  - setValue:
      values:
        - field: GOLD
          value: 0
        - field: PROVISIONS
          value: 0
  ```
- Set value mechanics:
  - Sets attribute to exact value (not relative modification)
  - Valid attributes: SKILL, STAMINA, LUCK, GOLD, PROVISIONS
  - Multiple attributes can be set in one action
  - Applied automatically when entering chapter (PASSIVE action)
  - No notification shown (silent operation)
- Set value UI:
  - No UI interaction required
  - Changes applied immediately
  - Useful for story events that reset attributes (losing items, near-death experiences)

## Check Parameter System
- Conditional navigation based on hero attribute values
- Compares attribute against a threshold value
- YAML format for checking parameters:
  ```yaml
  - checkParameter:
      parameter: SKILL
      threshold: 10
      greaterThanOrEquals:
        chapter: 1
        text: You managed to break the door.
      lessThan:
        chapter: 2
        text: The door stays closed.
  ```
- Check parameter mechanics:
  - Compares hero attribute (SKILL, STAMINA, LUCK, GOLD, PROVISIONS) to threshold
  - If value >= threshold: shows "greaterThanOrEquals" button
  - If value < threshold: shows "lessThan" button
  - Only ONE button is shown based on the comparison result
  - No dice rolling - direct attribute comparison
- Check parameter UI:
  - Single button displayed based on attribute check
  - Button text comes from greaterThanOrEquals or lessThan text field
  - Useful for skill checks, stat requirements, etc.

## Death System
- Instant death action for wrong choices or traps
- YAML format for death:
  ```yaml
  - death: |
      You choose wrongly and cannot escape this room. Your adventure ends here.
  ```
- Death mechanics:
  - Instantly sets hero STAMINA to 0 (game over)
  - Works regardless of current stamina level
  - Displays the death message from YAML
  - Shows skull.jpg background (same as stamina death)
  - No way to continue after death

## Visited Chapter Tracking
- Prevents revisiting chapters to avoid loops and maintain game progression
- Hero tracks all visited chapters in order
- Chapter 0 marked as visited at game start
- Visited chapter mechanics:
  - Each chapter visit is recorded when navigating via goToChapter or selectChoice
  - GotoAction automatically filters out visited chapters from available choices
  - Filtered choices maintain original YAML indices for correct navigation
  - Chapters can be visited multiple times (recorded each time)
  - Visit order preserved in list for potential future use
- Visited chapter UI:
  - No visual indication - filtered choices simply don't appear
  - Seamless filtering in button panel
  - Useful for preventing backtracking in linear story progression

## Architecture
- Action-based system for extensibility
- All action types implement `Action` interface:
  - `canHandle(actionData)` - checks if action can handle the data
  - `getActionType()` - returns ActionType enum
  - `execute(controller, actionData)` - performs the action
  - `getButtonText()` - for SINGLE_BUTTON actions
  - `getChoices(actionData)` - for MULTIPLE_BUTTONS actions (returns Choice objects)
- Choice class: type-safe wrapper for button choices (index, text)
- ActionType enum: SINGLE_BUTTON, MULTIPLE_BUTTONS, PASSIVE, DISPLAY
- Implemented actions:
  - `DisplayAction` (DISPLAY) - shows chapter text
  - `ModifyAction` (PASSIVE) - auto-applies attribute modifications
  - `NewEventAction` (PASSIVE) - records events automatically
  - `DeathAction` (PASSIVE) - instant death (sets stamina to 0)
  - `BattleAction` (SINGLE_BUTTON) - triggers battle encounters
  - `LuckAction` (SINGLE_BUTTON) - triggers luck tests
  - `AddItemAction` (MULTIPLE_BUTTONS) - provides item pickup choices
  - `UseItemAction` (DISPLAY) - enables item buttons for navigation
  - `CheckEventAction` (MULTIPLE_BUTTONS) - conditional navigation based on events/items
  - `GotoAction` (MULTIPLE_BUTTONS) - provides navigation choices
- DiceAnimator class handles all dice animations (battle and luck)
- GameController manages action registry and execution
- GameWindow handles UI based on ActionType generically
- GameWindow shows buttons for ALL actions in chapter (not just first one)
- No instanceof checks - uses ActionType for behavior
- Easy to add new action types without modifying existing code

## UI Architecture
- Separation of concerns between main window and feature-specific UI
- GameWindow - main window, stats panel, navigation, items panel
- BattleUI - self-contained battle UI logic
- LuckUI - self-contained luck test UI logic
- Feature UIs are independently testable and maintainable
- GameWindow provides updateHeroStats() for real-time stat updates
- GameWindow provides updateItemButtons() for inventory display updates
- Proper component lifecycle management with panel tracking
