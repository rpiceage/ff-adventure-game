# Multiple Enemies Battle System - Implementation Plan

## Overview
Extend battle system to support multiple enemies fighting simultaneously against the hero.

## YAML Format
```yaml
- battle:
    enemies:
      - enemy: Első Dombi Troll
        skill: 9
        stamina: 10
      - enemy: Második Dombi Troll
        skill: 9
        stamina: 9
    win: 211
```

## Battle Mechanics
- Hero rolls 2d6 separately for EACH alive enemy
- Hero selects ONE enemy as target (using radio buttons)
- Hero attacks selected enemy: if hero's roll vs that enemy wins, enemy takes 2 damage
- Hero defends against ALL alive enemies: for each enemy that beats hero in their roll, hero takes 2 damage
- Hero can take 0, 2, 4, 6... damage per turn (2 per enemy that beats hero)
- Battle ends when all enemies dead or hero dies

## Implementation Steps

### 1. Create Enemy.java
- Data class with: name, skill, stamina
- Track hero dice rolls against this enemy (heroDice1, heroDice2)
- Track enemy dice rolls (enemyDice1, enemyDice2)
- Method: `isAlive()` returns stamina > 0

### 2. Update Battle.java
- Replace single enemy fields with `List<Enemy> enemies`
- Add `int selectedEnemyIndex` (default 0)
- Add `setSelectedEnemy(int index)` method
- Update constructor to accept List<Enemy>
- Update `executeTurn()`:
  - For each alive enemy:
    - Roll 2d6 for hero
    - Roll 2d6 for enemy
    - Store rolls in Enemy object
    - Compare attacks
    - If this is selected enemy and hero wins: enemy takes 2 damage
    - If enemy wins: hero takes 2 damage
  - Update battle log with all results
- Update `getEnemyName()`, `getEnemySkill()`, `getEnemyStamina()` to return selected enemy's values
- Add `getEnemies()` to return full list
- Add `getAliveEnemies()` to return only alive enemies
- Update `isOver()`: all enemies dead or hero dead
- Update `heroWon()`: all enemies dead and hero alive

### 3. Update BattleUI.java
- Create radio button group for enemy selection
- Display all enemies in stats panel with their current stats
- Highlight/bold the selected enemy
- Update dice display to show multiple hero+enemy pairs (one per alive enemy)
- Update battle log to show results against all enemies
- Update enemy stats display after each turn
- Disable radio buttons for dead enemies

### 4. Update DiceAnimator.java (if needed)
- Verify it can handle multiple DiceGroup entries (should already support this)
- May need to adjust layout for more dice groups

### 5. Update BattleTest.java
- Add test: `testMultipleEnemiesBattle()`
- Add test: `testHeroSelectsTarget()`
- Add test: `testHeroTakesDamageFromMultipleEnemies()`
- Add test: `testHeroRollsSeparatelyAgainstEachEnemy()`
- Add test: `testBattleEndsWhenAllEnemiesDead()`
- Keep existing single enemy tests working

### 6. Update req.md
- Add "Multiple Enemies" section under Battle System
- Document mechanics: separate rolls, target selection, simultaneous defense
- Document UI: radio buttons for target selection
- Document that existing single-enemy battles still work

## Backward Compatibility
- Single enemy battles (existing YAML) continue to work
- When enemies list has 1 enemy, no radio buttons needed (or show but disabled)
- All existing tests must pass
