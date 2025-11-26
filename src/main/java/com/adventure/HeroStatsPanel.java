package com.adventure;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class HeroStatsPanel extends JPanel {
    private final JLabel skillLabel;
    private final JLabel staminaLabel;
    private final JLabel luckLabel;
    private final JLabel goldLabel;
    private final JButton provisionsButton;
    private final Hero hero;
    private final Runnable onProvisionsConsumed;
    private int prevSkill, prevStamina, prevLuck, prevGold, prevProvisions;
    private BufferedImage wallImage;
    
    public HeroStatsPanel(Hero hero, Runnable onProvisionsConsumed) {
        this.hero = hero;
        this.onProvisionsConsumed = onProvisionsConsumed;
        
        // Initialize previous values
        this.prevSkill = hero.getSkill();
        this.prevStamina = hero.getStamina();
        this.prevLuck = hero.getLuck();
        this.prevGold = hero.getGold();
        this.prevProvisions = hero.getProvisions();
        
        // Load wall background
        try {
            java.io.InputStream imgStream = getClass().getClassLoader().getResourceAsStream("wall.jpg");
            wallImage = ImageIO.read(imgStream);
        } catch (Exception ex) {
            wallImage = null;
        }
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        TitledBorder border = BorderFactory.createTitledBorder(Messages.get(Messages.Key.HERO_STATS_TITLE));
        border.setTitleFont(UIConstants.FONT_TITLE);
        border.setTitleColor(Color.WHITE);
        setBorder(border);
        setPreferredSize(new Dimension(UIConstants.STATS_PANEL_WIDTH, 0));
        
        // Create stat labels
        skillLabel = createStyledLabel();
        staminaLabel = createStyledLabel();
        luckLabel = createStyledLabel();
        goldLabel = createStyledLabel();
        add(skillLabel);
        add(staminaLabel);
        add(luckLabel);
        add(goldLabel);
        
        // Provisions button
        provisionsButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(UIConstants.SEMI_TRANSPARENT_BLACK);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        provisionsButton.setFont(UIConstants.FONT_MEDIUM);
        provisionsButton.setForeground(Color.WHITE);
        provisionsButton.setOpaque(false);
        provisionsButton.setContentAreaFilled(false);
        provisionsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        provisionsButton.addActionListener(e -> onProvisionsConsumed.run());
        add(Box.createVerticalStrut(10));
        add(provisionsButton);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (wallImage != null) {
            g.drawImage(wallImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
    
    private JLabel createStyledLabel() {
        JLabel label = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(UIConstants.SEMI_TRANSPARENT_BLACK);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        label.setFont(UIConstants.FONT_TITLE);
        label.setForeground(Color.WHITE);
        label.setOpaque(false);
        return label;
    }
    
    public void updateStats(boolean battleActive) {
        // Check for changes and animate
        if (hero.getSkill() != prevSkill) {
            AnimationHelper.animateLabel(skillLabel);
            prevSkill = hero.getSkill();
        }
        if (hero.getStamina() != prevStamina) {
            AnimationHelper.animateLabel(staminaLabel);
            prevStamina = hero.getStamina();
        }
        if (hero.getLuck() != prevLuck) {
            AnimationHelper.animateLabel(luckLabel);
            prevLuck = hero.getLuck();
        }
        if (hero.getGold() != prevGold) {
            AnimationHelper.animateLabel(goldLabel);
            prevGold = hero.getGold();
        }
        if (hero.getProvisions() != prevProvisions) {
            AnimationHelper.animateButton(provisionsButton);
            prevProvisions = hero.getProvisions();
        }
        
        skillLabel.setText(String.format("<html><div style='text-shadow: 2px 2px 4px black;'>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></div></html>", 
            Messages.get(Messages.Key.SKILL), hero.getSkill(), hero.getInitialSkill()));
        staminaLabel.setText(String.format("<html><div style='text-shadow: 2px 2px 4px black;'>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></div></html>", 
            Messages.get(Messages.Key.STAMINA), hero.getStamina(), hero.getInitialStamina()));
        luckLabel.setText(String.format("<html><div style='text-shadow: 2px 2px 4px black;'>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></div></html>", 
            Messages.get(Messages.Key.LUCK), hero.getLuck(), hero.getInitialLuck()));
        goldLabel.setText(String.format("<html><div style='text-shadow: 2px 2px 4px black;'>%s: <b><font color='red'>%d</font></b></div></html>", 
            Messages.get(Messages.Key.GOLD), hero.getGold()));
        
        provisionsButton.setText(Messages.get(Messages.Key.PROVISIONS) + ": " + hero.getProvisions());
        provisionsButton.setEnabled(hero.getProvisions() > 0 && !battleActive);
    }
    
    // Getters for testing
    public JLabel getSkillLabel() { return skillLabel; }
    public JLabel getStaminaLabel() { return staminaLabel; }
    public JLabel getLuckLabel() { return luckLabel; }
    public JLabel getGoldLabel() { return goldLabel; }
    public JButton getProvisionsButton() { return provisionsButton; }
}
