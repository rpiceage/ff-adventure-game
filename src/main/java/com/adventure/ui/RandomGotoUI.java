package com.adventure.ui;

import com.adventure.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomGotoUI {
    private JPanel dicePanel;
    private GameController controller;
    private GameWindow gameWindow;
    private Random random;
    private Container parentContainer;

    public RandomGotoUI(GameController controller, GameWindow gameWindow) {
        this(controller, gameWindow, new Random());
    }

    public RandomGotoUI(GameController controller, GameWindow gameWindow, Random random) {
        this.controller = controller;
        this.gameWindow = gameWindow;
        this.random = random;
    }

    public void rollDice(Map<String, Object> randomGotoAction, Runnable onComplete) {
        Map<String, Object> randomGoto = (Map<String, Object>) randomGotoAction.get("randomGoto");
        List<Map<String, Object>> choices = (List<Map<String, Object>>) randomGoto.get("choices");
        
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
        
        // Roll one die
        int diceValue = random.nextInt(6) + 1;
        
        DiceAnimator.DiceGroup[] groups = new DiceAnimator.DiceGroup[1];
        groups[0] = new DiceAnimator.DiceGroup("", diceValue);
        
        DiceAnimator.animateDice(dicePanel, groups, () -> {
            // After animation, show the button for the rolled value
            // Die shows 1-6, array index is 0-5
            int choiceIndex = diceValue - 1;
            if (choiceIndex >= 0 && choiceIndex < choices.size()) {
                Map<String, Object> choice = choices.get(choiceIndex);
                int targetChapter = (Integer) choice.get("chapter");
                String buttonText = (String) choice.get("text");
                
                // Callback with chapter and button text
                onComplete.run();
                gameWindow.showRandomGotoButton(targetChapter, buttonText);
            }
        });
    }
}
