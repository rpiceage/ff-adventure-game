# Create new ff application

## Requirements
- Desktop application in Java using Swing
- Handles a YAML document as input, like the requirements/sample.yaml
- Displays adventure text in a scrollable text area
- Handles navigation choices with buttons at the bottom
- Shows hero statistics in a panel on the right side
- Displays temporary notifications for attribute changes in the lower left corner

## Hero Attributes
- Hero has three attributes: SKILL, STAMINA, and LUCK
- Default initial values: SKILL: 12, STAMINA: 24, LUCK: 12
- Attributes displayed in a stats panel on the right side of the window
- Attributes can be modified through YAML chapter actions using the `modify` action
- Attribute values cannot exceed their initial values (capped at max)
- Attribute values cannot go below 0 (capped at min)
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
  ```
- Modifications are applied when entering a chapter
- A notification popup appears in the lower left corner showing all attribute changes
- Notification auto-dismisses after 3 seconds
- Notification messages:
  - Normal: "SKILL +1" or "STAMINA -5"
  - Capped: "STAMINA +3 (capped at 24)"
  - Blocked: "LUCK would have been modified but initial value cannot be exceeded"