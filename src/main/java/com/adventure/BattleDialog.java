package com.adventure;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class BattleDialog extends JDialog {
    private Battle battle;
    private JTextArea battleLog;
    private JLabel heroStaminaLabel;
    private JLabel enemyStaminaLabel;
    private JButton nextTurnButton;
    private boolean heroWon;

    public BattleDialog(JFrame parent, Battle battle) {
        super(parent, Messages.get(Messages.Key.BATTLE_TITLE), true);
        this.battle = battle;
        this.heroWon = false;

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JPanel statsPanel = new JPanel(new GridLayout(2, 1));
        heroStaminaLabel = new JLabel(Messages.get(Messages.Key.BATTLE_HERO) + " " + Messages.get(Messages.Key.SKILL) + ": " + battle.getHero().getSkill() + " " + Messages.get(Messages.Key.STAMINA) + ": " + battle.getHero().getStamina());
        enemyStaminaLabel = new JLabel(battle.getEnemyName() + " " + Messages.get(Messages.Key.SKILL) + ": " + battle.getEnemySkill() + " " + Messages.get(Messages.Key.STAMINA) + ": " + battle.getEnemyStamina());
        statsPanel.add(heroStaminaLabel);
        statsPanel.add(enemyStaminaLabel);
        add(statsPanel, BorderLayout.NORTH);

        try {
            BufferedImage bgImage = ImageIO.read(new File("src/resources/pergament.jpg"));
            battleLog = new JTextArea() {
                @Override
                protected void paintComponent(Graphics g) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                    super.paintComponent(g);
                }
            };
            battleLog.setOpaque(false);
        } catch (Exception e) {
            battleLog = new JTextArea();
        }
        
        battleLog.setEditable(false);
        battleLog.setLineWrap(true);
        battleLog.setWrapStyleWord(true);
        battleLog.setFont(new Font("Arial", Font.BOLD, 14));
        battleLog.setMargin(new Insets(10, 10, 10, 10));
        add(new JScrollPane(battleLog), BorderLayout.CENTER);

        nextTurnButton = new JButton(Messages.get(Messages.Key.BATTLE_NEXT_TURN));
        nextTurnButton.addActionListener(e -> executeTurn());
        add(nextTurnButton, BorderLayout.SOUTH);
    }

    private void executeTurn() {
        battle.executeTurn();
        battleLog.append(battle.getLastTurnResult() + "\n\n");
        heroStaminaLabel.setText(Messages.get(Messages.Key.BATTLE_HERO) + " " + Messages.get(Messages.Key.SKILL) + ": " + battle.getHero().getSkill() + " " + Messages.get(Messages.Key.STAMINA) + ": " + battle.getHero().getStamina());
        enemyStaminaLabel.setText(battle.getEnemyName() + " " + Messages.get(Messages.Key.SKILL) + ": " + battle.getEnemySkill() + " " + Messages.get(Messages.Key.STAMINA) + ": " + battle.getEnemyStamina());

        if (battle.isOver()) {
            nextTurnButton.setEnabled(false);
            if (battle.heroWon()) {
                battleLog.append("\n" + Messages.get(Messages.Key.BATTLE_VICTORY) + " " + battle.getEnemyName() + "!");
                heroWon = true;
            } else {
                battleLog.append("\n" + String.format(Messages.get(Messages.Key.BATTLE_DEFEAT), battle.getEnemyName()));
            }
            nextTurnButton.setText(Messages.get(Messages.Key.BATTLE_CLOSE));
            nextTurnButton.setEnabled(true);
            nextTurnButton.removeActionListener(nextTurnButton.getActionListeners()[0]);
            nextTurnButton.addActionListener(e -> dispose());
        }
    }

    public boolean heroWon() {
        return heroWon;
    }
}
