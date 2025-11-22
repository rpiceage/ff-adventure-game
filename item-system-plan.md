# Item System Implementation Plan

## Overview
Add inventory system where hero can collect items displayed as buttons on character sheet.

## YAML Format
```yaml
- addItem:
    items:
      - name: Aranygyűrű
      - name: Sword
      - name: Potion
```

## Requirements
- Hero has a list of items (inventory)
- Items displayed as buttons on character sheet (stats panel)
- Clicking item button shows popup: "This item can't be used right now"
- Multiple items can be added in one action
- Each item in addItem action gets a button on main panel to add it to inventory

## Implementation Steps

### 1. Update Hero class
- Add `List<String> inventory` field
- Add `addItem(String itemName)` method
- Add `getInventory()` method
- Add `hasItem(String itemName)` method

### 2. Create AddItemAction class
- Implements Action interface
- ActionType: MULTIPLE_BUTTONS (one button per item to add)
- `canHandle()`: checks for "addItem" key
- `getChoices()`: returns list of items as choices
- `execute()`: adds selected item to hero inventory

### 3. Update GameWindow
- Add items panel to stats panel (below attributes)
- Create item buttons dynamically based on hero.getInventory()
- Item buttons show item name
- Clicking item button shows popup with "This item can't be used right now"
- Update `updateDisplay()` to refresh item buttons

### 4. Update Messages class
- Add ITEMS_TITLE key ("Items" / "Tárgyak")
- Add ITEM_CANT_USE key ("This item can't be used right now" / "Ez a tárgy most nem használható")
- Add ADD_ITEM key ("Take" / "Felvesz") for button text

### 5. Update GameController
- Register AddItemAction in registerActions()
- Handle MULTIPLE_BUTTONS action type for items

### 6. Tests
- Add ItemTest for Hero inventory methods
- Add test for AddItemAction
- Add game flow test with item collection
- Create sample YAML with items

## UI Layout
```
Stats Panel:
  SKILL: 12 (12)
  STAMINA: 24 (24)
  LUCK: 12 (12)
  GOLD: 10
  
  Items:
  [Aranygyűrű] [Sword] [Potion]
```

## Behavior
1. Chapter has addItem action with 2 items
2. Main panel shows 2 buttons: "Take Aranygyűrű", "Take Sword"
3. Click "Take Aranygyűrű" → item added to inventory
4. Stats panel updates, shows [Aranygyűrű] button
5. Click [Aranygyűrű] button → popup "This item can't be used right now"
6. Click "Take Sword" → [Sword] button appears in stats panel

## Notes
- Items are just strings (names) for now
- No item removal yet
- No item usage logic yet (just popup)
- Items persist across chapters
- Duplicate items allowed (can have 2 potions)
