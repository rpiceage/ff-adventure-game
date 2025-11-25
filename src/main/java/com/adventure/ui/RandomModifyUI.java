package com.adventure.ui;

import com.adventure.*;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Random;

public class RandomModifyUI {
    private JPanel dicePanel;
    private GameController controller;
    private GameWindow gameWindow;
    private Runnable onComplete;
    private Random random;
    private Container parentContainer;

    public RandomModifyUI(GameController controller, GameWindow gameWindow, Runnable onComplete) {
        this(controller, gameWindow, onComplete, new Random());
    }

    public RandomModifyUI(GameController controller, GameWindow gameWindow, Runnable onComplete, Random random) {
        this.controller = controller;
        this.gameWindow = gameWindow;
        this.onComplete = onComplete;
        this.random = random;
    }

    public void rollDice(Map<String, Object> randomModifyAction) {
        Map<String, Object> randomModify = (Map<String, Object>) randomModifyAction.get("randomModify");
        String field = (String) randomModify.get("field");
        int type = (Integer) randomModify.get("type");
        int diceCount = (Integer) randomModify.get("dice");
        
        // Create dice panel
        dicePanel = DiceAnimator.createDicePanel("src/resources/table.jpg");
        dicePanel.setPreferredSize(new Dimension(400, 150));
        
        // Create wrapper panel with BorderLayout to contain dice and text area
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(dicePanel, BorderLayout.NORTH);
        
        // Get the current center component (text scroll pane) and add it to wrapper
        parentContainer = gameWindow.getContentPane();
        Component centerComponent = ((BorderLayout)parentContainer.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComponent != null) {
            parentContainer.remove(centerComponent);
            wrapper.add(centerComponent, BorderLayout.CENTER);
        }
        
        // Add wrapper to game window
        parentContainer.add(wrapper, BorderLayout.CENTER);
        gameWindow.setCurrentDicePanel(wrapper);
        parentContainer.revalidate();
        parentContainer.repaint();
        
        // Roll dice
        int[] diceValues = new int[diceCount];
        int total = 0;
        for (int i = 0; i < diceCount; i++) {
            diceValues[i] = random.nextInt(6) + 1;
            total += diceValues[i];
        }
        
        DiceAnimator.DiceGroup[] groups = new DiceAnimator.DiceGroup[1];
        if (diceCount == 1) {
            groups[0] = new DiceAnimator.DiceGroup("", diceValues[0]);
        } else if (diceCount == 2) {
            groups[0] = new DiceAnimator.DiceGroup("", diceValues[0], diceValues[1]);
        } else {
            groups[0] = new DiceAnimator.DiceGroup("", diceValues);
        }
        
        int finalTotal = total;
        int value = (type == 0) ? -finalTotal : finalTotal;
        
        DiceAnimator.animateDice(dicePanel, groups, () -> {
            Hero hero = controller.getHero();
            try {
                String methodName = "modify" + field.charAt(0) + field.substring(1).toLowerCase() + "Silent";
                Method method = Hero.class.getMethod(methodName, int.class);
                method.invoke(hero, value);
                gameWindow.updateHeroStats();
            } catch (Exception ex) {
                throw new RuntimeException("Error modifying attribute " + field, ex);
            }
            
            // Keep dice panel visible to show result
            // Callback to show goto buttons
            onComplete.run();
        });
    }
}
