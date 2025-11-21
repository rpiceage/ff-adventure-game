package com.adventure.ui;

import com.adventure.*;
import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Random;

public class LuckUI {
    private JPanel centerPanel;
    private JPanel dicePanel;
    private JTextArea textArea;
    private JPanel buttonPanel;
    private GameController controller;
    private Runnable onComplete;

    public LuckUI(JTextArea textArea, JPanel buttonPanel, GameController controller, Runnable onComplete) {
        this.textArea = textArea;
        this.buttonPanel = buttonPanel;
        this.controller = controller;
        this.onComplete = onComplete;
    }

    public JPanel start(Map<String, Object> luckAction) {
        Map<String, Object> luckData = (Map<String, Object>) luckAction.get("luck");
        int luckyChapter = (Integer) luckData.get("lucky");
        int unluckyChapter = (Integer) luckData.get("unlucky");
        
        centerPanel = new JPanel(new BorderLayout());
        
        dicePanel = DiceAnimator.createDicePanel("src/resources/table.jpg");
        dicePanel.setPreferredSize(new Dimension(400, 150));
        
        centerPanel.add(dicePanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        textArea.setText(Messages.get(Messages.Key.LUCK_TEST_TITLE));
        buttonPanel.removeAll();
        
        JButton testButton = new JButton(Messages.get(Messages.Key.LUCK_TEST_BUTTON));
        testButton.addActionListener(e -> {
            testButton.setEnabled(false);
            
            Random rand = new Random();
            int dice1 = rand.nextInt(6) + 1;
            int dice2 = rand.nextInt(6) + 1;
            int total = dice1 + dice2;
            int heroLuck = controller.getHero().getLuck();
            boolean lucky = total <= heroLuck;
            
            DiceAnimator.DiceGroup[] groups = {
                new DiceAnimator.DiceGroup("", dice1, dice2)
            };
            
            DiceAnimator.animateDice(dicePanel, groups, () -> {
                textArea.setText(lucky ? Messages.get(Messages.Key.LUCK_LUCKY) : Messages.get(Messages.Key.LUCK_UNLUCKY));
                
                buttonPanel.removeAll();
                JButton continueButton = new JButton(Messages.get(Messages.Key.LUCK_CONTINUE));
                continueButton.addActionListener(ev -> {
                    controller.goToChapter(lucky ? luckyChapter : unluckyChapter);
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
}
