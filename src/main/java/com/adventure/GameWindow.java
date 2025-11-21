package com.adventure;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class GameWindow extends JFrame {
    private JTextArea textArea;
    private JPanel buttonPanel;
    private JPanel statsPanel;
    private JLabel skillLabel;
    private JLabel staminaLabel;
    private JLabel luckLabel;
    private GameController controller;

    public GameWindow(Adventure adventure) {
        this.controller = new GameController(adventure);
        setTitle(adventure.title);
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Hero Stats"));
        skillLabel = new JLabel();
        staminaLabel = new JLabel();
        luckLabel = new JLabel();
        statsPanel.add(skillLabel);
        statsPanel.add(staminaLabel);
        statsPanel.add(luckLabel);
        add(statsPanel, BorderLayout.EAST);

        buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        updateDisplay();
        setVisible(true);
    }

    private void updateDisplay() {
        textArea.setText(controller.getDisplayText());
        
        Hero hero = controller.getHero();
        skillLabel.setText("SKILL: " + hero.getSkill());
        staminaLabel.setText("STAMINA: " + hero.getStamina());
        luckLabel.setText("LUCK: " + hero.getLuck());
        
        List<String> mods = hero.getLastModifications();
        if (!mods.isEmpty()) {
            String message = String.join("\n", mods);
            JOptionPane.showMessageDialog(this, message, "Attribute Changes", JOptionPane.INFORMATION_MESSAGE);
            hero.clearModifications();
        }
        
        buttonPanel.removeAll();
        
        List<Map<String, Object>> choices = controller.getChoices();
        for (int i = 0; i < choices.size(); i++) {
            JButton btn = new JButton(choices.get(i).get("text").toString());
            int choiceIndex = i;
            btn.addActionListener(e -> {
                controller.selectChoice(choiceIndex);
                updateDisplay();
            });
            buttonPanel.add(btn);
        }
        
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }
}
