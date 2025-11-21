package com.adventure;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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
    private JWindow notificationWindow;

    public GameWindow(Adventure adventure) {
        this.controller = new GameController(adventure);
        setTitle(adventure.title);
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        try {
            BufferedImage bgImage = ImageIO.read(new File("src/resources/pergament.jpg"));
            textArea = new JTextArea() {
                @Override
                protected void paintComponent(Graphics g) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                    super.paintComponent(g);
                }
            };
            textArea.setOpaque(false);
        } catch (Exception e) {
            textArea = new JTextArea();
        }
        
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.BOLD, 16));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createTitledBorder(Messages.get(Messages.Key.HERO_STATS_TITLE)));
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
        Hero hero = controller.getHero();
        skillLabel.setText(Messages.get(Messages.Key.SKILL) + ": " + hero.getSkill());
        staminaLabel.setText(Messages.get(Messages.Key.STAMINA) + ": " + hero.getStamina());
        luckLabel.setText(Messages.get(Messages.Key.LUCK) + ": " + hero.getLuck());
        
        List<String> mods = hero.getLastModifications();
        if (!mods.isEmpty()) {
            showNotification(String.join("\n", mods));
            hero.clearModifications();
        }
        
        if (controller.isGameOver()) {
            textArea.setText(Messages.get(Messages.Key.GAME_OVER));
            buttonPanel.removeAll();
        } else {
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
        }
        
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void showNotification(String message) {
        if (notificationWindow != null) {
            notificationWindow.dispose();
        }
        
        notificationWindow = new JWindow(this);
        JLabel label = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        label.setBackground(new Color(255, 255, 200));
        label.setOpaque(true);
        notificationWindow.add(label);
        notificationWindow.pack();
        
        Point location = getLocation();
        Dimension size = getSize();
        Dimension notifSize = notificationWindow.getSize();
        notificationWindow.setLocation(
            location.x + 10,
            location.y + size.height - notifSize.height - 50
        );
        
        notificationWindow.setVisible(true);
        
        Timer timer = new Timer(3000, e -> {
            notificationWindow.dispose();
            notificationWindow = null;
        });
        timer.setRepeats(false);
        timer.start();
    }
}
