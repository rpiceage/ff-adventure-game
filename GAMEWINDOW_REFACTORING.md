# GameWindow Refactoring Plan

## Current State Analysis

**File**: `GameWindow.java`  
**Lines**: 1,004  
**Fields**: 43 private fields  
**Methods**: 20+ methods  
**Longest Method**: `updateDisplay()` - 248 lines

## Critical Issues

### 1. God Class Anti-Pattern

GameWindow violates Single Responsibility Principle by handling:
- Window frame management
- Menu bar creation
- Text display with parchment background
- Illustration loading and transparency processing
- Stats panel (skill, stamina, luck, gold, provisions)
- Items panel and inventory management
- Button panel with dynamic height calculation
- Notification popups
- Battle UI coordination
- Luck test UI coordination
- Random action UI coordination
- Attribute test UI coordination
- Save/load game dialogs
- Animation logic (labels and buttons)
- Chapter state tracking
- Dice panel management

**Impact**: This makes the class:
- Impossible to unit test effectively
- Difficult to understand and modify
- Prone to bugs when adding features
- Violates Open/Closed Principle

### 2. The 248-Line Monster Method: `updateDisplay()`

**Lines 215-462**: This method does EVERYTHING:

```java
public void updateDisplay() {
    updateHeroStats();                           // Line 216
    // ... 30 lines of setup ...
    if (controller.isGameOver()) {               // Line 230
        // ... 30 lines of game over logic ...
    } else if (battleUI != null) {               // Line 260
        // ... battle UI logic ...
    } else if (randomModifyUI != null) {         // Line 262
        // ... random modify logic ...
    } else {                                     // Line 270
        // ... 180+ lines of chapter display logic ...
        // - Random action tracking
        // - Action iteration and filtering
        // - AddItemAction handling (40 lines)
        // - CheckEventAction handling (20 lines)
        // - CheckParameterAction handling (20 lines)
        // - GotoAction handling (15 lines)
        // - Single button action handling
    }
    recalculateButtonPanelHeight();              // Line 458
}
```

**Problems**:
- Cyclomatic complexity: ~20+ (should be < 10)
- Nested conditionals 4-5 levels deep
- Mixes high-level orchestration with low-level details
- Impossible to test individual behaviors
- Hard to understand control flow

### 3. Excessive Field Count (43 fields)

```java
// UI Components (11 fields)
private JTextArea textArea;
private JEditorPane displayPane;
private JPanel buttonPanel;
private JScrollPane buttonScrollPane;
private JPanel statsPanel;
private JPanel itemsPanel;
private JLabel skillLabel, staminaLabel, luckLabel, goldLabel;
private JButton provisionsButton;

// UI State (5 fields)
private JWindow notificationWindow;
private JScrollPane textScrollPane;
private JScrollPane textWithIllustrationPanel;
private JPanel currentCenterPanel;
private JPanel currentDicePanel;

// Feature UIs (5 fields)
private BattleUI battleUI;
private LuckUI luckUI;
private RandomModifyUI randomModifyUI;
private RandomGotoUI randomGotoUI;
private AttributeTestUI attributeTestUI;

// Game State (8 fields)
private GameController controller;
private int prevSkill, prevStamina, prevLuck, prevGold, prevProvisions;
private Set<Integer> chaptersWithExecutedRandomModify;
private Set<Integer> chaptersWithExecutedRandomGoto;
private int lastDisplayedChapter;
private int soldItemsCount;
private int takenItemsCount;
private String gameYamlPath;
private JLabel illustrationLabel;
```

**Problem**: Too many responsibilities tracked in one class.

### 4. Magic Numbers Everywhere

```java
setSize(1200, 800);                              // Line 54
textArea.setFont(new Font("Arial", Font.BOLD, 24));  // Line 94
textArea.setMargin(new Insets(10, 10, 10, 320));     // Line 96
statsPanel.setPreferredSize(new Dimension(300, 0)); // Line 119
buttonScrollPane.setPreferredSize(new Dimension(0, 80)); // Line 189
Timer timer = new Timer(3000, ...);              // Line 698 (notification)
final float[] sizes = {24f, 25f, 26f, 27f, 26f, 25f, 24f}; // Line 553
int targetWidth = 300;                           // Line 826 (illustration)
```

### 5. Code Duplication

**Font Creation** (7 occurrences):
```java
new Font("Arial", Font.BOLD, 24)  // Lines 94, 117, 505
new Font("Arial", Font.BOLD, 18)  // Lines 142, 681
new Font("Arial", Font.BOLD, 16)  // Line 718
new Font("Arial", Font.BOLD, 20)  // Line 165
```

**Background Painting** (5 similar paintComponent methods):
- Lines 74-84: textArea with parchment
- Lines 107-112: statsPanel with wall
- Lines 134-141: provisionsButton with rounded rect
- Lines 157-164: itemsTitle with rounded rect
- Lines 173-181: itemsPanel with rounded rect
- Lines 495-509: createStyledLabel with rounded rect

**Animation Logic** (duplicated):
- `animateLabel()` (lines 550-564)
- `animateButton()` (lines 566-580)
- Nearly identical code, only font size differs

### 6. Complex Conditional Logic in updateDisplay()

**Lines 295-450**: Nested action handling with multiple instanceof checks disguised as action type checks:

```java
if (action instanceof AddItemAction) {
    // 40 lines of item handling
    for (int i = 0; i < choices.size(); i++) {
        // Button creation
        btn.addActionListener(e -> {
            // Complex logic with maxItems checking
            if (maxItems != null && takenItemsCount >= maxItems) {
                // Disable all buttons
                for (Component c : buttonPanel.getComponents()) {
                    // More nested logic
                }
            }
        });
    }
} else if (action instanceof CheckEventAction) {
    // 20 lines
} else if (action instanceof CheckParameterAction) {
    // 20 lines
} else {
    // GotoAction handling
}
```

### 7. Illustration Processing (Lines 800-880)

80 lines of complex image processing logic:
- Resource loading
- Path construction
- Image scaling
- Pixel-by-pixel transparency adjustment
- Reflection-based method invocation

**Problem**: This belongs in a separate `IllustrationManager` class.

### 8. Poor Separation of Concerns

**UI State vs Game State**:
```java
// Game state (belongs in GameController)
private Set<Integer> chaptersWithExecutedRandomModify;
private Set<Integer> chaptersWithExecutedRandomGoto;
private int soldItemsCount;
private int takenItemsCount;

// UI state (belongs in GameWindow)
private JPanel currentCenterPanel;
private JPanel currentDicePanel;
```

These are mixed together, making it unclear where state belongs.

## Refactoring Plan

### Phase 1: Extract Constants (2 hours)

**Create `UIConstants.java`**:
```java
public class UIConstants {
    // Window dimensions
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 800;
    
    // Panel dimensions
    public static final int STATS_PANEL_WIDTH = 300;
    public static final int BUTTON_PANEL_HEIGHT = 80;
    public static final int ILLUSTRATION_WIDTH = 300;
    public static final int TEXT_RIGHT_MARGIN = 320; // ILLUSTRATION_WIDTH + 20
    
    // Font sizes
    public static final int FONT_SIZE_TITLE = 24;
    public static final int FONT_SIZE_LARGE = 20;
    public static final int FONT_SIZE_MEDIUM = 18;
    public static final int FONT_SIZE_SMALL = 16;
    
    // Fonts
    public static final Font FONT_TITLE = new Font("Arial", Font.BOLD, FONT_SIZE_TITLE);
    public static final Font FONT_LARGE = new Font("Arial", Font.BOLD, FONT_SIZE_LARGE);
    public static final Font FONT_MEDIUM = new Font("Arial", Font.BOLD, FONT_SIZE_MEDIUM);
    public static final Font FONT_SMALL = new Font("Arial", Font.BOLD, FONT_SIZE_SMALL);
    
    // Timing
    public static final int NOTIFICATION_DURATION_MS = 3000;
    public static final int ANIMATION_STEP_MS = 50;
    
    // Colors
    public static final Color SEMI_TRANSPARENT_BLACK = new Color(0, 0, 0, 100);
    public static final Color NOTIFICATION_BG = new Color(255, 255, 200);
    public static final Color BUTTON_GREEN = new Color(0, 100, 0);
    
    // Margins
    public static final Insets TEXT_MARGINS = new Insets(10, 10, 10, TEXT_RIGHT_MARGIN);
}
```

**Impact**: Eliminates all magic numbers, makes values configurable.

### Phase 2: Extract StatsPanel (4 hours)

**Create `HeroStatsPanel.java`**:
```java
public class HeroStatsPanel extends JPanel {
    private JLabel skillLabel;
    private JLabel staminaLabel;
    private JLabel luckLabel;
    private JLabel goldLabel;
    private JButton provisionsButton;
    private Hero hero;
    private Consumer<Void> onProvisionsConsumed;
    
    public HeroStatsPanel(Hero hero, Consumer<Void> onProvisionsConsumed) {
        this.hero = hero;
        this.onProvisionsConsumed = onProvisionsConsumed;
        initializeUI();
    }
    
    private void initializeUI() {
        // Move all stats panel setup from GameWindow
        // Lines 103-183 from GameWindow
    }
    
    public void updateStats() {
        // Move updateHeroStats logic
        // Lines 511-547 from GameWindow
    }
    
    public void enableProvisionsButton(boolean enabled) {
        provisionsButton.setEnabled(enabled);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        // Wall background painting
    }
}
```

**Benefits**:
- Removes 11 fields from GameWindow
- Removes 70+ lines from constructor
- Removes updateHeroStats() method
- Self-contained stats display logic

### Phase 3: Extract ItemsPanel (3 hours)

**Create `InventoryPanel.java`**:
```java
public class InventoryPanel extends JPanel {
    private Hero hero;
    private GameController controller;
    private Consumer<String> onItemUsed;
    private int soldItemsCount = 0;
    
    public InventoryPanel(Hero hero, GameController controller, Consumer<String> onItemUsed) {
        this.hero = hero;
        this.controller = controller;
        this.onItemUsed = onItemUsed;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
    }
    
    public void updateItems() {
        // Move updateItemButtons logic
        // Lines 710-758 from GameWindow
    }
    
    public void resetSoldItemsCount() {
        soldItemsCount = 0;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        // Semi-transparent background
    }
}
```

**Benefits**:
- Removes 2 fields from GameWindow
- Removes updateItemButtons() method (48 lines)
- Encapsulates item display logic

### Phase 4: Extract NotificationManager (2 hours)

**Create `NotificationManager.java`**:
```java
public class NotificationManager {
    private JWindow notificationWindow;
    private JFrame parent;
    
    public NotificationManager(JFrame parent) {
        this.parent = parent;
    }
    
    public void show(String message) {
        // Move showNotification logic
        // Lines 674-708 from GameWindow
    }
    
    public void hide() {
        if (notificationWindow != null) {
            notificationWindow.dispose();
            notificationWindow = null;
        }
    }
}
```

**Benefits**:
- Removes 1 field from GameWindow
- Removes showNotification() method (34 lines)
- Reusable notification system

### Phase 5: Extract IllustrationManager (3 hours)

**Create `IllustrationManager.java`**:
```java
public class IllustrationManager {
    private String gameYamlPath;
    private Random random = new Random();
    
    public IllustrationManager(String gameYamlPath) {
        this.gameYamlPath = gameYamlPath;
    }
    
    public BufferedImage loadIllustration(int chapterIndex) {
        // Move updateIllustration logic
        // Lines 800-880 from GameWindow
        // Returns processed image or null
    }
    
    private BufferedImage processTransparency(BufferedImage image) {
        // Extract pixel processing logic
    }
    
    private BufferedImage scaleImage(BufferedImage image, int targetWidth) {
        // Extract scaling logic
    }
}
```

**Benefits**:
- Removes updateIllustration() method (80 lines)
- Testable image processing
- Can be reused for other image needs

### Phase 6: Extract ActionButtonFactory (4 hours)

**Create `ActionButtonFactory.java`**:
```java
public class ActionButtonFactory {
    private GameController controller;
    private Runnable onActionComplete;
    
    public ActionButtonFactory(GameController controller, Runnable onActionComplete) {
        this.controller = controller;
        this.onActionComplete = onActionComplete;
    }
    
    public List<JButton> createButtons(Action action, Map<String, Object> actionData) {
        if (action instanceof AddItemAction) {
            return createAddItemButtons(action, actionData);
        } else if (action instanceof CheckEventAction) {
            return createCheckEventButtons(action, actionData);
        }
        // ... etc
    }
    
    private List<JButton> createAddItemButtons(Action action, Map<String, Object> actionData) {
        // Extract lines 295-340 from updateDisplay
    }
    
    private List<JButton> createCheckEventButtons(Action action, Map<String, Object> actionData) {
        // Extract lines 341-365 from updateDisplay
    }
    
    // ... other button creation methods
}
```

**Benefits**:
- Removes 100+ lines from updateDisplay()
- Testable button creation logic
- Easier to add new action types

### Phase 7: Refactor updateDisplay() (6 hours)

**Break into smaller methods**:

```java
public void updateDisplay() {
    updateHeroStats();
    handleModificationNotifications();
    
    if (controller.isGameOver()) {
        displayGameOver();
    } else if (isSpecialUIActive()) {
        updateSpecialUI();
    } else {
        displayChapter();
    }
    
    recalculateButtonPanelHeight();
}

private void handleModificationNotifications() {
    List<String> mods = controller.getHero().getLastModifications();
    if (!mods.isEmpty()) {
        notificationManager.show(String.join("\n", mods));
        controller.getHero().clearModifications();
    }
}

private void displayGameOver() {
    String deathText = findDeathText();
    textArea.setText(deathText != null ? deathText : Messages.get(Messages.Key.GAME_OVER));
    textArea.setCaretPosition(0);
    buttonPanel.removeAll();
    clearSpecialUIs();
    displaySkullImage();
}

private boolean isSpecialUIActive() {
    return battleUI != null || randomModifyUI != null || 
           randomGotoUI != null || luckUI != null || attributeTestUI != null;
}

private void updateSpecialUI() {
    if (battleUI != null && battleUI.isActive()) {
        battleUI.updateDisplay();
    }
    // ... other special UIs
}

private void displayChapter() {
    textArea.setText(controller.getDisplayText());
    textArea.setCaretPosition(0);
    
    handleChapterChange();
    createActionButtons();
}

private void handleChapterChange() {
    int currentChapter = controller.getCurrentChapter().index;
    if (currentChapter != lastDisplayedChapter) {
        resetChapterState();
        illustrationManager.updateIllustration(currentChapter, textArea);
        removeDicePanel();
        lastDisplayedChapter = currentChapter;
    }
}

private void createActionButtons() {
    buttonPanel.removeAll();
    
    List<Map<String, Object>> actions = filterActions();
    for (Map<String, Object> actionData : actions) {
        Action action = controller.getActionForData(actionData);
        if (action != null) {
            List<JButton> buttons = actionButtonFactory.createButtons(action, actionData);
            buttons.forEach(buttonPanel::add);
        }
    }
}

private List<Map<String, Object>> filterActions() {
    // Extract action filtering logic
    // Lines 285-294 from current updateDisplay
}
```

**Benefits**:
- Each method < 20 lines
- Clear responsibilities
- Testable in isolation
- Reduced cyclomatic complexity

### Phase 8: Extract ChapterStateManager (3 hours)

**Create `ChapterStateManager.java`**:
```java
public class ChapterStateManager {
    private Set<Integer> chaptersWithExecutedRandomModify = new HashSet<>();
    private Set<Integer> chaptersWithExecutedRandomGoto = new HashSet<>();
    private int lastDisplayedChapter = -1;
    private int soldItemsCount = 0;
    private int takenItemsCount = 0;
    
    public boolean hasExecutedRandomModify(int chapter) {
        return chaptersWithExecutedRandomModify.contains(chapter);
    }
    
    public void markRandomModifyExecuted(int chapter) {
        chaptersWithExecutedRandomModify.add(chapter);
    }
    
    public boolean hasExecutedRandomGoto(int chapter) {
        return chaptersWithExecutedRandomGoto.contains(chapter);
    }
    
    public void markRandomGotoExecuted(int chapter) {
        chaptersWithExecutedRandomGoto.add(chapter);
    }
    
    public void resetForNewChapter() {
        soldItemsCount = 0;
        takenItemsCount = 0;
    }
    
    public int getSoldItemsCount() { return soldItemsCount; }
    public void incrementSoldItems() { soldItemsCount++; }
    
    public int getTakenItemsCount() { return takenItemsCount; }
    public void incrementTakenItems() { takenItemsCount++; }
    
    public int getLastDisplayedChapter() { return lastDisplayedChapter; }
    public void setLastDisplayedChapter(int chapter) { lastDisplayedChapter = chapter; }
}
```

**Benefits**:
- Removes 5 fields from GameWindow
- Clear state management
- Can be saved/loaded with game state

### Phase 9: Extract AnimationHelper (2 hours)

**Create `AnimationHelper.java`**:
```java
public class AnimationHelper {
    private static final int ANIMATION_STEP_MS = 50;
    private static final float[] LABEL_SIZES = {24f, 25f, 26f, 27f, 26f, 25f, 24f};
    private static final float[] BUTTON_SIZES = {18f, 19f, 20f, 21f, 20f, 19f, 18f};
    
    public static void animateLabel(JLabel label) {
        animate(label, LABEL_SIZES, 24f);
    }
    
    public static void animateButton(JButton button) {
        animate(button, BUTTON_SIZES, 18f);
    }
    
    private static void animate(JComponent component, float[] sizes, float finalSize) {
        Timer timer = new Timer(ANIMATION_STEP_MS, null);
        final int[] step = {0};
        timer.addActionListener(e -> {
            if (step[0] < sizes.length) {
                component.setFont(component.getFont().deriveFont(sizes[step[0]]));
                step[0]++;
            } else {
                component.setFont(component.getFont().deriveFont(finalSize));
                timer.stop();
            }
        });
        timer.start();
    }
}
```

**Benefits**:
- Removes 2 methods (30 lines)
- Eliminates duplication
- Reusable animation system

## Final Architecture

After refactoring, GameWindow becomes:

```java
public class GameWindow extends JFrame {
    // Core components (8 fields)
    private JTextArea textArea;
    private JPanel buttonPanel;
    private JScrollPane buttonScrollPane;
    private GameController controller;
    
    // Extracted managers (6 fields)
    private HeroStatsPanel statsPanel;
    private InventoryPanel itemsPanel;
    private NotificationManager notificationManager;
    private IllustrationManager illustrationManager;
    private ActionButtonFactory actionButtonFactory;
    private ChapterStateManager chapterState;
    
    // Feature UIs (5 fields)
    private BattleUI battleUI;
    private LuckUI luckUI;
    private RandomModifyUI randomModifyUI;
    private RandomGotoUI randomGotoUI;
    private AttributeTestUI attributeTestUI;
    
    // UI state (2 fields)
    private JPanel currentCenterPanel;
    private JPanel currentDicePanel;
    
    // Constructor: ~100 lines (down from 210)
    // updateDisplay(): ~50 lines (down from 248)
    // Total: ~400 lines (down from 1004)
}
```

## Metrics After Refactoring

| Metric | Before | After | Improvement |
|--------|--------|-------|-------------|
| Total Lines | 1,004 | ~400 | 60% reduction |
| Fields | 43 | 21 | 51% reduction |
| Longest Method | 248 | ~50 | 80% reduction |
| Constructor | 210 | ~100 | 52% reduction |
| Responsibilities | 16+ | 4 | 75% reduction |
| Testability | Low | High | ✅ |

## New Classes Created

1. `UIConstants.java` (~50 lines)
2. `HeroStatsPanel.java` (~150 lines)
3. `InventoryPanel.java` (~100 lines)
4. `NotificationManager.java` (~50 lines)
5. `IllustrationManager.java` (~120 lines)
6. `ActionButtonFactory.java` (~200 lines)
7. `ChapterStateManager.java` (~80 lines)
8. `AnimationHelper.java` (~40 lines)

**Total new code**: ~790 lines  
**Net reduction**: 1,004 - 400 - 790 = -186 lines (slightly more code, but MUCH better organized)

## Implementation Order

1. **Day 1**: UIConstants, AnimationHelper (4 hours)
2. **Day 2**: NotificationManager, ChapterStateManager (5 hours)
3. **Day 3**: HeroStatsPanel (4 hours)
4. **Day 4**: InventoryPanel, IllustrationManager (6 hours)
5. **Day 5**: ActionButtonFactory, refactor updateDisplay() (10 hours)

**Total effort**: ~5 days (29 hours)

## Testing Strategy

After refactoring, each component can be unit tested:

```java
// Test stats panel
@Test
public void testStatsUpdate() {
    Hero hero = new Hero(10, 20, 10, 5, 3);
    HeroStatsPanel panel = new HeroStatsPanel(hero, null);
    panel.updateStats();
    // Assert labels show correct values
}

// Test notification manager
@Test
public void testNotificationDisplay() {
    NotificationManager mgr = new NotificationManager(mockFrame);
    mgr.show("Test message");
    // Assert window is visible
}

// Test illustration manager
@Test
public void testIllustrationLoading() {
    IllustrationManager mgr = new IllustrationManager("books/test.yaml");
    BufferedImage img = mgr.loadIllustration(1);
    // Assert image loaded and processed
}
```

## Risk Assessment

**Low Risk**:
- UIConstants extraction
- AnimationHelper extraction
- NotificationManager extraction

**Medium Risk**:
- HeroStatsPanel extraction (UI layout changes)
- InventoryPanel extraction (item interaction logic)
- ChapterStateManager extraction (state management)

**High Risk**:
- ActionButtonFactory extraction (complex action handling)
- updateDisplay() refactoring (core game loop)

**Mitigation**:
- Refactor incrementally
- Test after each extraction
- Keep old code commented until verified
- Create integration tests for critical paths

## Conclusion

GameWindow is a classic God Class that has accumulated too many responsibilities. The 248-line updateDisplay() method is the most critical issue. By extracting 8 new classes and refactoring the core method, we can:

- Reduce complexity by 60-80%
- Make code testable
- Improve maintainability
- Enable parallel development
- Reduce bug risk for new features

**Recommendation**: Start with low-risk extractions (UIConstants, AnimationHelper) to build confidence, then tackle the high-risk refactorings with proper testing.
