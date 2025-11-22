package com.adventure;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class DiceAnimator {
    
    public static class DiceGroup {
        public String label;
        public int[] finalValues;
        
        public DiceGroup(String label, int... finalValues) {
            this.label = label;
            this.finalValues = finalValues;
        }
    }
    
    public static JPanel createDicePanel(String tableImagePath) {
        try {
            java.io.InputStream imgStream = DiceAnimator.class.getClassLoader().getResourceAsStream("table.jpg");
            java.awt.image.BufferedImage tableImage = javax.imageio.ImageIO.read(imgStream);
            return new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(tableImage, 0, 0, getWidth(), getHeight(), this);
                }
            };
        } catch (Exception ex) {
            return new JPanel();
        }
    }
    
    public static void animateDice(JPanel dicePanel, DiceGroup[] groups, Runnable onComplete) {
        dicePanel.removeAll();
        dicePanel.setLayout(new BoxLayout(dicePanel, BoxLayout.Y_AXIS));
        
        JLabel[] labels = new JLabel[groups.length];
        JPanel[] displays = new JPanel[groups.length];
        int[][] diceValues = new int[groups.length][];
        double[][] rotations = new double[groups.length][];
        
        for (int g = 0; g < groups.length; g++) {
            final int groupIndex = g;
            DiceGroup group = groups[g];
            
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            rowPanel.setOpaque(false);
            
            if (group.label != null && !group.label.isEmpty()) {
                labels[g] = new JLabel(group.label);
                labels[g].setFont(new Font("Arial", Font.BOLD, 20));
                rowPanel.add(labels[g]);
            }
            
            diceValues[g] = new int[group.finalValues.length];
            rotations[g] = new double[group.finalValues.length];
            for (int i = 0; i < group.finalValues.length; i++) {
                diceValues[g][i] = 1;
                rotations[g][i] = 0;
            }
            
            final int[][] allDiceValues = diceValues;
            final double[][] allRotations = rotations;
            
            displays[g] = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    for (int i = 0; i < allDiceValues[groupIndex].length; i++) {
                        int x = 40 + (i * 80);
                        drawRotatedDice(g2d, x, 40, allDiceValues[groupIndex][i], allRotations[groupIndex][i]);
                    }
                }
            };
            displays[g].setPreferredSize(new Dimension(40 + group.finalValues.length * 80, 80));
            displays[g].setOpaque(false);
            rowPanel.add(displays[g]);
            
            dicePanel.add(rowPanel);
        }
        
        dicePanel.revalidate();
        dicePanel.repaint();
        
        Random rand = new Random();
        Timer animTimer = new Timer(50, null);
        final int[] count = {0};
        final double[] rotationSpeeds = {Math.PI / 4, Math.PI / 3, Math.PI / 5, Math.PI / 6};
        
        animTimer.addActionListener(e -> {
            if (count[0] < 20) {
                for (int g = 0; g < groups.length; g++) {
                    for (int i = 0; i < diceValues[g].length; i++) {
                        rotations[g][i] += rotationSpeeds[(g * 2 + i) % rotationSpeeds.length];
                        diceValues[g][i] = rand.nextInt(6) + 1;
                    }
                    displays[g].repaint();
                }
                count[0]++;
            } else {
                animTimer.stop();
                for (int g = 0; g < groups.length; g++) {
                    for (int i = 0; i < diceValues[g].length; i++) {
                        rotations[g][i] = 0;
                        diceValues[g][i] = groups[g].finalValues[i];
                    }
                    displays[g].repaint();
                }
                onComplete.run();
            }
        });
        animTimer.start();
    }
    
    private static void drawRotatedDice(Graphics2D g2d, int x, int y, int value, double angle) {
        Graphics2D g2 = (Graphics2D) g2d.create();
        g2.translate(x, y);
        g2.rotate(angle);
        
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(-25, -25, 50, 50, 10, 10);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(-25, -25, 50, 50, 10, 10);
        
        g2.setColor(Color.BLACK);
        int d = 8;
        if (value == 1) {
            g2.fillOval(-d/2, -d/2, d, d);
        } else if (value == 2) {
            g2.fillOval(-15, -15, d, d);
            g2.fillOval(7, 7, d, d);
        } else if (value == 3) {
            g2.fillOval(-15, -15, d, d);
            g2.fillOval(-d/2, -d/2, d, d);
            g2.fillOval(7, 7, d, d);
        } else if (value == 4) {
            g2.fillOval(-15, -15, d, d);
            g2.fillOval(7, -15, d, d);
            g2.fillOval(-15, 7, d, d);
            g2.fillOval(7, 7, d, d);
        } else if (value == 5) {
            g2.fillOval(-15, -15, d, d);
            g2.fillOval(7, -15, d, d);
            g2.fillOval(-d/2, -d/2, d, d);
            g2.fillOval(-15, 7, d, d);
            g2.fillOval(7, 7, d, d);
        } else if (value == 6) {
            g2.fillOval(-15, -15, d, d);
            g2.fillOval(7, -15, d, d);
            g2.fillOval(-15, -d/2, d, d);
            g2.fillOval(7, -d/2, d, d);
            g2.fillOval(-15, 7, d, d);
            g2.fillOval(7, 7, d, d);
        }
        g2.dispose();
    }
}
