package com.adventure;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GameSelectionWindow extends JFrame {
    private WelcomeWindow welcomeWindow;
    
    public GameSelectionWindow(WelcomeWindow welcomeWindow) {
        this.welcomeWindow = welcomeWindow;
        
        setTitle("Select Game");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Select a Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        
        panel.add(Box.createVerticalStrut(20));
        
        // List available games from resources/books/
        List<String> gameFiles = findGameFiles();
        
        for (String gameFile : gameFiles) {
            JButton gameButton = new JButton(getGameTitle(gameFile));
            gameButton.setFont(new Font("Arial", Font.PLAIN, 18));
            gameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            gameButton.setMaximumSize(new Dimension(500, 50));
            gameButton.addActionListener(e -> startGame(gameFile));
            panel.add(gameButton);
            panel.add(Box.createVerticalStrut(10));
        }
        
        panel.add(Box.createVerticalStrut(20));
        
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 18));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setMaximumSize(new Dimension(200, 40));
        backButton.addActionListener(e -> goBack());
        panel.add(backButton);
        
        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane);
        setVisible(true);
    }
    
    private List<String> findGameFiles() {
        List<String> files = new ArrayList<>();
        try {
            URL booksUrl = getClass().getClassLoader().getResource("books/");
            if (booksUrl != null) {
                java.io.File booksDir = new java.io.File(booksUrl.toURI());
                if (booksDir.exists() && booksDir.isDirectory()) {
                    java.io.File[] yamlFiles = booksDir.listFiles((dir, name) -> name.endsWith(".yaml"));
                    if (yamlFiles != null) {
                        for (java.io.File file : yamlFiles) {
                            files.add("books/" + file.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }
    
    private String getGameTitle(String gameFile) {
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream(gameFile);
            if (input != null) {
                Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
                Adventure adventure = yaml.load(input);
                return adventure.title != null ? adventure.title : gameFile;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameFile;
    }
    
    private void startGame(String gameFile) {
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream(gameFile);
            if (input != null) {
                Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
                Adventure adventure = yaml.load(input);
                
                if (adventure.language != null) {
                    Messages.setLanguage(adventure.language);
                }
                
                new GameWindow(adventure, gameFile);
                dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Failed to load game: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void goBack() {
        welcomeWindow.setVisible(true);
        dispose();
    }
}
