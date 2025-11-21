package com.adventure;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameWindow extends JFrame {
    private JTextArea textArea;
    private JPanel buttonPanel;
    private JPanel statsPanel;
    private JLabel skillLabel;
    private JLabel staminaLabel;
    private JLabel luckLabel;
    private GameController controller;
    private JWindow notificationWindow;
    private Battle currentBattle;
    private JPanel battleStatsPanel;
    private JLabel enemyStatsLabel;
    private JPanel centerPanel;
    private JPanel dicePanel;

    public GameWindow(Adventure adventure) {
        this.controller = new GameController(adventure);
        setTitle(adventure.title);
        setSize(1200, 800);
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
        textArea.setFont(new Font("Arial", Font.BOLD, 24));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        TitledBorder border = BorderFactory.createTitledBorder(Messages.get(Messages.Key.HERO_STATS_TITLE));
        border.setTitleFont(new Font("Arial", Font.BOLD, 24));
        statsPanel.setBorder(border);
        statsPanel.setPreferredSize(new Dimension(300, 0));
        skillLabel = new JLabel();
        skillLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        staminaLabel = new JLabel();
        staminaLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        luckLabel = new JLabel();
        luckLabel.setFont(new Font("Arial", Font.PLAIN, 24));
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
        skillLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.SKILL), hero.getSkill(), hero.getInitialSkill()));
        staminaLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.STAMINA), hero.getStamina(), hero.getInitialStamina()));
        luckLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.LUCK), hero.getLuck(), hero.getInitialLuck()));
        
        List<String> mods = hero.getLastModifications();
        if (!mods.isEmpty()) {
            showNotification(String.join("\n", mods));
            hero.clearModifications();
        }
        
        if (controller.isGameOver()) {
            textArea.setText(Messages.get(Messages.Key.GAME_OVER));
            buttonPanel.removeAll();
            currentBattle = null;
            
            try {
                BufferedImage skullImage = ImageIO.read(new File("src/resources/skull.jpg"));
                JLabel skullLabel = new JLabel(new ImageIcon(skullImage));
                remove(statsPanel);
                add(skullLabel, BorderLayout.EAST);
                revalidate();
                repaint();
            } catch (Exception ex) {
                // Keep stats panel if image fails to load
            }
        } else if (currentBattle != null) {
            updateBattleDisplay();
        } else {
            textArea.setText(controller.getDisplayText());
            buttonPanel.removeAll();
            
            Map<String, Object> battleAction = controller.getBattleAction();
            if (battleAction != null) {
                JButton battleButton = new JButton(Messages.get(Messages.Key.BATTLE_BEGIN));
                battleButton.addActionListener(e -> startBattle(battleAction));
                buttonPanel.add(battleButton);
            } else {
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
        }
        
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void startBattle(Map<String, Object> battleAction) {
        Map<String, Object> battleData = (Map<String, Object>) battleAction.get("battle");
        List<Map<String, Object>> enemies = (List<Map<String, Object>>) battleData.get("enemies");
        Map<String, Object> enemyData = enemies.get(0);
        
        String enemyName = (String) enemyData.get("enemy");
        int enemySkill = (Integer) enemyData.get("skill");
        int enemyStamina = (Integer) enemyData.get("stamina");
        
        currentBattle = new Battle(controller.getHero(), enemyName, enemySkill, enemyStamina);
        
        centerPanel = new JPanel(new BorderLayout());
        battleStatsPanel = new JPanel();
        battleStatsPanel.setLayout(new BoxLayout(battleStatsPanel, BoxLayout.Y_AXIS));
        TitledBorder battleBorder = BorderFactory.createTitledBorder(Messages.get(Messages.Key.BATTLE_TITLE));
        battleBorder.setTitleFont(new Font("Arial", Font.BOLD, 24));
        battleStatsPanel.setBorder(battleBorder);
        enemyStatsLabel = new JLabel();
        enemyStatsLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        battleStatsPanel.add(enemyStatsLabel);
        
        try {
            BufferedImage tableImage = ImageIO.read(new File("src/resources/table.jpg"));
            dicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20)) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(tableImage, 0, 0, getWidth(), getHeight(), this);
                }
            };
        } catch (Exception ex) {
            dicePanel = new JPanel();
        }
        dicePanel.setPreferredSize(new Dimension(400, 150));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(battleStatsPanel, BorderLayout.NORTH);
        topPanel.add(dicePanel, BorderLayout.CENTER);
        
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        getContentPane().remove(0); // Remove old scroll pane
        add(centerPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        
        updateBattleDisplay();
    }

    private void updateBattleDisplay() {
        Hero hero = controller.getHero();
        skillLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.SKILL), hero.getSkill(), hero.getInitialSkill()));
        staminaLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.STAMINA), hero.getStamina(), hero.getInitialStamina()));
        luckLabel.setText(String.format("<html>%s: <b><font color='red'>%d</font></b> <font size='5'>(%d)</font></html>", 
            Messages.get(Messages.Key.LUCK), hero.getLuck(), hero.getInitialLuck()));
        
        int enemyStam = currentBattle.getEnemyStamina();
        enemyStatsLabel.setText(String.format("%s %s: %d %s: %d", 
            currentBattle.getEnemyName(), 
            Messages.get(Messages.Key.SKILL), currentBattle.getEnemySkill(),
            Messages.get(Messages.Key.STAMINA), enemyStam));
        
        textArea.setText(currentBattle.getBattleLog());
        buttonPanel.removeAll();
        
        if (currentBattle.isOver()) {
            if (currentBattle.heroWon()) {
                textArea.append("\n" + Messages.get(Messages.Key.BATTLE_VICTORY) + " " + currentBattle.getEnemyName() + "!");
                Map<String, Object> battleAction = controller.getBattleAction();
                Map<String, Object> battleData = (Map<String, Object>) battleAction.get("battle");
                int winChapter = (Integer) battleData.get("win");
                
                JButton continueButton = new JButton(Messages.get(Messages.Key.BATTLE_CLOSE));
                continueButton.addActionListener(e -> {
                    currentBattle = null;
                    battleStatsPanel = null;
                    remove(centerPanel);
                    centerPanel = null;
                    add(new JScrollPane(textArea), BorderLayout.CENTER);
                    revalidate();
                    repaint();
                    controller.goToChapter(winChapter);
                    updateDisplay();
                });
                buttonPanel.add(continueButton);
            } else {
                textArea.append("\n" + String.format(Messages.get(Messages.Key.BATTLE_DEFEAT), currentBattle.getEnemyName()));
                currentBattle = null;
                battleStatsPanel = null;
                remove(centerPanel);
                centerPanel = null;
                add(new JScrollPane(textArea), BorderLayout.CENTER);
                revalidate();
                repaint();
                updateDisplay();
            }
        } else {
            JButton nextTurnButton = new JButton(Messages.get(Messages.Key.BATTLE_NEXT_TURN));
            nextTurnButton.addActionListener(e -> {
                nextTurnButton.setEnabled(false);
                animateDice(() -> {
                    currentBattle.executeTurn();
                    updateBattleDisplay();
                    nextTurnButton.setEnabled(true);
                });
            });
            buttonPanel.add(nextTurnButton);
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

    private void animateDice(Runnable onComplete) {
        dicePanel.removeAll();
        dicePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 20));
        
        final int[][] diceValues = {{1, 1}, {1, 1}}; // [hero/enemy][dice1/dice2]
        final double[] rotation = {0, 0, 0, 0}; // separate rotation for each die
        
        JLabel heroLabel = new JLabel("Hero:");
        heroLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JPanel heroDiceDisplay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                drawRotatedDice(g2d, 40, 40, diceValues[0][0], rotation[0]);
                drawRotatedDice(g2d, 120, 40, diceValues[0][1], rotation[1]);
            }
        };
        heroDiceDisplay.setPreferredSize(new Dimension(160, 80));
        heroDiceDisplay.setOpaque(false);
        
        JLabel enemyLabel = new JLabel(currentBattle.getEnemyName() + ":");
        enemyLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JPanel enemyDiceDisplay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                drawRotatedDice(g2d, 40, 40, diceValues[1][0], rotation[2]);
                drawRotatedDice(g2d, 120, 40, diceValues[1][1], rotation[3]);
            }
        };
        enemyDiceDisplay.setPreferredSize(new Dimension(160, 80));
        enemyDiceDisplay.setOpaque(false);
        
        dicePanel.add(heroLabel);
        dicePanel.add(heroDiceDisplay);
        dicePanel.add(enemyLabel);
        dicePanel.add(enemyDiceDisplay);
        dicePanel.revalidate();
        dicePanel.repaint();
        
        Random rand = new Random();
        Timer animTimer = new Timer(50, null);
        final int[] count = {0};
        
        animTimer.addActionListener(e -> {
            if (count[0] < 20) {
                rotation[0] += Math.PI / 4;
                rotation[1] += Math.PI / 3;
                rotation[2] += Math.PI / 5;
                rotation[3] += Math.PI / 6;
                diceValues[0][0] = rand.nextInt(6) + 1;
                diceValues[0][1] = rand.nextInt(6) + 1;
                diceValues[1][0] = rand.nextInt(6) + 1;
                diceValues[1][1] = rand.nextInt(6) + 1;
                heroDiceDisplay.repaint();
                enemyDiceDisplay.repaint();
                count[0]++;
            } else {
                animTimer.stop();
                onComplete.run();
                if (currentBattle != null) {
                    rotation[0] = rotation[1] = rotation[2] = rotation[3] = 0;
                    diceValues[0][0] = currentBattle.getLastHeroDice1();
                    diceValues[0][1] = currentBattle.getLastHeroDice2();
                    diceValues[1][0] = currentBattle.getLastEnemyDice1();
                    diceValues[1][1] = currentBattle.getLastEnemyDice2();
                    heroDiceDisplay.repaint();
                    enemyDiceDisplay.repaint();
                }
            }
        });
        animTimer.start();
    }

    private String getDiceDots(int value) {
        switch (value) {
            case 1: return "⚀";
            case 2: return "⚁";
            case 3: return "⚂";
            case 4: return "⚃";
            case 5: return "⚄";
            case 6: return "⚅";
            default: return "?";
        }
    }

    private void drawRotatedDice(Graphics2D g2d, int x, int y, int value, double angle) {
        Graphics2D g2 = (Graphics2D) g2d.create();
        g2.translate(x, y);
        g2.rotate(angle);
        
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        String dice = getDiceDots(value);
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(dice);
        
        // Draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(-w/2 + 3, -25, w - 6, 35);
        
        g2.setColor(Color.BLACK);
        g2.drawString(dice, -w/2, 10);
        g2.dispose();
    }
}
