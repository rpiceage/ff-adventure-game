# Code Review - Fighting Fantasy Game Engine

## Overview
This is a Java Swing-based gamebook engine with 4,764 lines of production code across 30+ classes. The architecture follows an action-based pattern with good separation of concerns in most areas.

## Architecture Assessment

### ✅ Strengths

1. **Action Pattern Implementation**
   - Clean interface-based design (`Action` interface)
   - 14 action types implementing consistent contract
   - Good use of enums (`ActionType`) for behavior classification
   - No instanceof checks - polymorphic dispatch

2. **Separation of Concerns**
   - UI logic separated into dedicated classes (`BattleUI`, `LuckUI`, `RandomModifyUI`, etc.)
   - Game logic in `GameController` and `Hero`
   - Battle mechanics isolated in `Battle` class
   - Save/load functionality in dedicated `SaveGameManager`

3. **Internationalization**
   - Centralized `Messages` class with enum-based keys
   - Support for English and Hungarian
   - Consistent message lookup pattern

4. **Resource Management**
   - YAML-based game data
   - Proper resource loading from classpath
   - Support for multiple game books

## ⚠️ Issues Identified

### 1. **God Class: GameWindow (1,004 lines)**

**Problem**: GameWindow is doing too much - it's a massive class handling:
- Window setup and layout
- Menu creation
- Text display with illustrations
- Stats panel management
- Item management
- Button panel management
- Notification system
- Multiple UI state machines (battle, luck, random actions)
- Save/load coordination
- Animation logic

**Impact**: 
- Hard to test
- Difficult to maintain
- High coupling
- Violates Single Responsibility Principle

**Specific Issues**:
- `updateDisplay()` method: **248 lines** - extremely long method
- Multiple responsibilities mixed together
- 15+ private fields tracking UI state
- Complex conditional logic for different action types

**Recommendation**:
```
Extract classes:
- StatsPanel (skill/stamina/luck/gold/provisions display)
- ItemsPanel (inventory management)
- NotificationManager (popup notifications)
- IllustrationManager (chapter illustrations)
- MenuBarBuilder (menu creation)
- ChapterDisplayPanel (text area with parchment background)
```

### 2. **Long Methods**

**GameWindow.updateDisplay()**: 248 lines
- Handles display logic, goto actions, battle, luck, random actions, items
- Should be split into smaller, focused methods
- Consider Strategy pattern for different display modes

**Recommendation**:
```java
// Instead of one giant method, use:
private void updateDisplay() {
    clearPreviousDisplay();
    displayChapterText();
    displayChapterIllustration();
    handleChapterActions();
    updateNavigationButtons();
}
```

### 3. **Magic Numbers**

Found throughout `GameWindow`:
- Font sizes: 24, 18, 16, 20
- Window size: 1200x800
- Margins: 10, 320
- Animation sizes: 24f, 18f
- Notification timing: 3000ms

**Recommendation**:
```java
// Create constants class
public class UIConstants {
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 800;
    public static final int FONT_SIZE_LARGE = 24;
    public static final int FONT_SIZE_MEDIUM = 18;
    public static final int FONT_SIZE_SMALL = 16;
    public static final int NOTIFICATION_DURATION_MS = 3000;
    public static final int ILLUSTRATION_WIDTH = 300;
    public static final int TEXT_RIGHT_MARGIN = 320; // ILLUSTRATION_WIDTH + 20
}
```

### 4. **Code Duplication**

**Font Creation Pattern** (repeated 7+ times):
```java
new Font("Arial", Font.BOLD, 24)
new Font("Arial", Font.BOLD, 18)
new Font("Arial", Font.BOLD, 16)
```

**Recommendation**:
```java
private static class Fonts {
    static final Font LARGE = new Font("Arial", Font.BOLD, 24);
    static final Font MEDIUM = new Font("Arial", Font.BOLD, 18);
    static final Font SMALL = new Font("Arial", Font.BOLD, 16);
}
```

**Background Image Pattern** (repeated in multiple paintComponent methods):
- Similar paintComponent overrides in 5+ places
- Could extract to reusable component

### 5. **Battle Class Complexity**

**Battle.java** (355 lines):
- 27 private fields (too many responsibilities)
- Handles: combat logic, multiple enemies, ally system, modifiers, extra damage, escape, interrupts
- Growing feature list making it harder to maintain

**Recommendation**:
- Extract `BattleModifiers` class (modifier value, text, extra damage config)
- Extract `BattleState` class (current turn, selected enemy, logs)
- Consider Builder pattern for Battle construction
- Split ally logic into separate `AllyBattle` subclass or strategy

### 6. **Inconsistent State Management**

Multiple sets tracking state in GameWindow:
```java
private java.util.Set<Integer> chaptersWithExecutedRandomModify
private java.util.Set<Integer> chaptersWithExecutedRandomGoto
private int soldItemsCount
private int takenItemsCount
private int lastDisplayedChapter
```

**Recommendation**:
- Create `ChapterState` class to encapsulate per-chapter state
- Move to GameController where game state belongs
- GameWindow should only handle UI state

### 7. **Missing Abstractions**

**Dice Rolling**:
- Random dice logic scattered across Battle, DiceAnimator, UI classes
- No central `DiceRoller` abstraction

**Recommendation**:
```java
public class DiceRoller {
    private Random random;
    
    public int roll() { return random.nextInt(6) + 1; }
    public int roll2d6() { return roll() + roll(); }
    public List<Integer> rollMultiple(int count) { ... }
}
```

### 8. **Testing Concerns**

**Hard to Test**:
- GameWindow tightly couples UI and logic
- No interfaces for key collaborators
- Static resource loading
- Direct Swing component creation

**Missing**:
- Unit tests for action classes
- Integration tests for game flow
- Tests for save/load functionality

**Recommendation**:
- Extract interfaces: `HeroDisplay`, `ChapterDisplay`, `BattleDisplay`
- Use dependency injection
- Create test fixtures for YAML game data

### 9. **Resource Management**

**Potential Issues**:
- Image loading in constructors (can fail silently)
- No explicit resource cleanup
- Multiple image loads for same resources

**Recommendation**:
- Create `ResourceCache` for images
- Lazy loading with proper error handling
- Resource cleanup in dispose() methods

### 10. **Error Handling**

**Weak Error Handling**:
```java
try {
    InputStream bgStream = getClass().getClassLoader().getResourceAsStream("pergament.jpg");
    BufferedImage bgImage = ImageIO.read(bgStream);
    // ...
} catch (Exception e) {
    textArea = new JTextArea(); // Silent fallback
}
```

**Recommendation**:
- Log errors properly
- Provide user feedback for critical failures
- Don't catch generic Exception
- Validate resources at startup

## Metrics Summary

| Metric | Value | Assessment |
|--------|-------|------------|
| Total LOC | 4,764 | Moderate |
| Largest Class | GameWindow (1,004) | ⚠️ Too large |
| Longest Method | updateDisplay (248) | ⚠️ Too long |
| Number of Classes | 30+ | ✅ Good |
| Action Classes | 14 | ✅ Good |
| UI Classes | 5 | ✅ Good |
| Cyclomatic Complexity | High in GameWindow | ⚠️ Needs refactoring |

## Positive Patterns

1. **No instanceof checks** - Good use of polymorphism
2. **Action interface** - Clean, extensible design
3. **Separate UI classes** - BattleUI, LuckUI, etc.
4. **Immutable Choice class** - Good value object
5. **Enum-based message keys** - Type-safe i18n
6. **YAML-based data** - Flexible game content

## Priority Refactoring Recommendations

### High Priority
1. **Split GameWindow** - Extract 5-6 smaller classes
2. **Refactor updateDisplay()** - Break into 10-15 smaller methods
3. **Extract constants** - Create UIConstants class
4. **Simplify Battle** - Extract modifiers and state

### Medium Priority
5. **Add unit tests** - Start with action classes
6. **Create DiceRoller** - Centralize random logic
7. **Improve error handling** - Add logging and user feedback
8. **Resource caching** - Avoid duplicate image loads

### Low Priority
9. **Extract font factory** - Reduce duplication
10. **Document public APIs** - Add Javadoc to key classes

## Conclusion

The codebase has a **solid architectural foundation** with the action pattern and good separation in most areas. However, **GameWindow has become a god class** that needs urgent refactoring. The 248-line `updateDisplay()` method is the biggest technical debt.

**Estimated Refactoring Effort**:
- GameWindow split: 2-3 days
- updateDisplay() refactoring: 1 day
- Constants extraction: 2-4 hours
- Battle simplification: 1 day
- **Total: ~1 week** for high-priority items

**Risk Assessment**: Medium
- Code works but is becoming harder to maintain
- Adding new features will increase complexity
- Testing is difficult without refactoring
- No critical bugs, but technical debt is accumulating

**Recommendation**: Schedule refactoring sprint before adding major new features.
