package com.adventure;

import javax.swing.*;
import java.awt.*;

public class WelcomeWindow extends JFrame {
    
    public WelcomeWindow() {
        setTitle("Fighting Fantasy");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JLabel titleLabel = new JLabel("Fighting Fantasy");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        
        panel.add(Box.createVerticalStrut(50));
        
        JButton newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.PLAIN, 24));
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGameButton.setMaximumSize(new Dimension(300, 60));
        newGameButton.addActionListener(e -> showGameSelection());
        panel.add(newGameButton);
        
        panel.add(Box.createVerticalStrut(20));
        
        JButton loadGameButton = new JButton("Load Game");
        loadGameButton.setFont(new Font("Arial", Font.PLAIN, 24));
        loadGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadGameButton.setMaximumSize(new Dimension(300, 60));
        loadGameButton.addActionListener(e -> loadGame());
        panel.add(loadGameButton);
        
        add(panel);
        setVisible(true);
    }
    
    private void showGameSelection() {
        new GameSelectionWindow(this);
        setVisible(false);
    }
    
    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser(SaveGameManager.getDefaultSaveDirectory());
        fileChooser.setDialogTitle("Load Game");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("FF Save Files (*.ffsave)", "ffsave"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                SaveGameManager.loadAndStartGame(fileChooser.getSelectedFile());
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading game: " + ex.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
