package com.adventure.ui;

import com.adventure.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AttributeTestUI {
    private JPanel centerPanel;
    private JPanel dicePanel;
    private JTextArea textArea;
    private JPanel buttonPanel;
    private GameController controller;
    private Runnable onComplete;
    private Random random;

    public AttributeTestUI(JTextArea textArea, JPanel buttonPanel, GameController controller, Runnable onComplete) {
        this(textArea, buttonPanel, controller, onComplete, new Random());
    }
    
    public AttributeTestUI(JTextArea textArea, JPanel buttonPanel, GameController controller, Runnable onComplete, Random random) {
        this.textArea = textArea;
        this.buttonPanel = buttonPanel;
        this.controller = controller;
        this.onComplete = onComplete;
        this.random = random;
    }

    public JPanel start(Map<String, Object> attributeTestAction) {
        Map<String, Object> testData = (Map<String, Object>) attributeTestAction.get("attributeTest");
        int numDice = (Integer) testData.get("dice");
        int modifier = testData.containsKey("modifier") ? (Integer) testData.get("modifier") : 0;
        List<String> attributes = (List<String>) testData.get("attribute");
        String attributeName = attributes.get(0);
        int successChapter = (Integer) testData.get("success");
        int failChapter = (Integer) testData.get("fail");
        
        centerPanel = new JPanel(new BorderLayout());
        
        dicePanel = DiceAnimator.createDicePanel("src/resources/table.jpg");
        dicePanel.setPreferredSize(new Dimension(400, 150));
        
        centerPanel.add(dicePanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        textArea.setText(Messages.get(Messages.Key.ATTRIBUTE_TEST_TITLE));
        buttonPanel.removeAll();
        
        JButton testButton = new JButton(Messages.get(Messages.Key.ATTRIBUTE_TEST_BUTTON));
        testButton.addActionListener(e -> {
            testButton.setEnabled(false);
            
            int total = modifier;
            int[] diceValues = new int[numDice];
            for (int i = 0; i < numDice; i++) {
                diceValues[i] = random.nextInt(6) + 1;
                total += diceValues[i];
            }
            
            int heroAttribute = getHeroAttribute(attributeName);
            boolean success = total <= heroAttribute;
            
            DiceAnimator.DiceGroup[] groups = {
                new DiceAnimator.DiceGroup("", diceValues)
            };
            
            DiceAnimator.animateDice(dicePanel, groups, () -> {
                textArea.setText(success ? Messages.get(Messages.Key.ATTRIBUTE_TEST_SUCCESS) : Messages.get(Messages.Key.ATTRIBUTE_TEST_FAIL));
                
                buttonPanel.removeAll();
                JButton continueButton = new JButton(Messages.get(Messages.Key.LUCK_CONTINUE));
                continueButton.addActionListener(ev -> {
                    controller.goToChapter(success ? successChapter : failChapter);
                    onComplete.run();
                });
                buttonPanel.add(continueButton);
                buttonPanel.revalidate();
                buttonPanel.repaint();
            });
        });
        buttonPanel.add(testButton);
        buttonPanel.revalidate();
        buttonPanel.repaint();
        
        return centerPanel;
    }
    
    private int getHeroAttribute(String attributeName) {
        Hero hero = controller.getHero();
        switch (attributeName) {
            case "SKILL": return hero.getSkill();
            case "STAMINA": return hero.getStamina();
            case "LUCK": return hero.getLuck();
            default: throw new IllegalArgumentException("Unknown attribute: " + attributeName);
        }
    }
}
