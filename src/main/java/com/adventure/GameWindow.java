package com.adventure;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class GameWindow extends JFrame {
    private JTextArea textArea;
    private JPanel buttonPanel;
    private GameController controller;

    public GameWindow(Adventure adventure) {
        this.controller = new GameController(adventure);
        setTitle(adventure.title);
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        updateDisplay();
        setVisible(true);
    }

    private void updateDisplay() {
        textArea.setText(controller.getDisplayText());
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
