package com.adventure;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class PotionSelectionWindow extends JFrame {
    private String selectedPotion = null;
    
    public PotionSelectionWindow(Consumer<String> onConfirm) {
        setTitle(Messages.get(Messages.Key.POTION_SELECTION_TITLE));
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel(Messages.get(Messages.Key.POTION_SELECTION_LABEL));
        label.setFont(UIConstants.FONT_LARGE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(20));
        
        ButtonGroup group = new ButtonGroup();
        
        JRadioButton skillButton = new JRadioButton(Messages.get(Messages.Key.POTION_SKILL));
        skillButton.setFont(UIConstants.FONT_MEDIUM);
        skillButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        skillButton.addActionListener(e -> selectedPotion = Messages.get(Messages.Key.POTION_SKILL));
        group.add(skillButton);
        panel.add(skillButton);
        panel.add(Box.createVerticalStrut(10));
        
        JRadioButton staminaButton = new JRadioButton(Messages.get(Messages.Key.POTION_STAMINA));
        staminaButton.setFont(UIConstants.FONT_MEDIUM);
        staminaButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        staminaButton.addActionListener(e -> selectedPotion = Messages.get(Messages.Key.POTION_STAMINA));
        group.add(staminaButton);
        panel.add(staminaButton);
        panel.add(Box.createVerticalStrut(10));
        
        JRadioButton luckButton = new JRadioButton(Messages.get(Messages.Key.POTION_LUCK));
        luckButton.setFont(UIConstants.FONT_MEDIUM);
        luckButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        luckButton.addActionListener(e -> selectedPotion = Messages.get(Messages.Key.POTION_LUCK));
        group.add(luckButton);
        panel.add(luckButton);
        panel.add(Box.createVerticalStrut(20));
        
        JButton confirmButton = new JButton(Messages.get(Messages.Key.POTION_SELECTION_CONFIRM));
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmButton.addActionListener(e -> {
            if (selectedPotion != null) {
                dispose();
                onConfirm.accept(selectedPotion);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a potion!", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        panel.add(confirmButton);
        
        add(panel);
        setVisible(true);
    }
}
