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
            String gameTitle = getGameTitle(gameFile);
            ImageIcon coverIcon = getCoverImage(gameFile);
            
            JButton gameButton = new JButton();
            gameButton.setLayout(new BorderLayout());
            gameButton.setPreferredSize(new Dimension(500, 150));
            gameButton.setMaximumSize(new Dimension(500, 150));
            gameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            if (coverIcon != null) {
                JLabel imageLabel = new JLabel(coverIcon);
                gameButton.add(imageLabel, BorderLayout.WEST);
            }
            
            JLabel gameTitleLabel = new JLabel(gameTitle);
            gameTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            gameTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gameButton.add(gameTitleLabel, BorderLayout.CENTER);
            
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
            // Try to list files from JAR or filesystem
            URL booksUrl = getClass().getClassLoader().getResource("books/");
            if (booksUrl != null) {
                if (booksUrl.getProtocol().equals("jar")) {
                    // Running from JAR - use JAR file system
                    String jarPath = booksUrl.getPath().substring(5, booksUrl.getPath().indexOf("!"));
                    try (java.util.jar.JarFile jar = new java.util.jar.JarFile(java.net.URLDecoder.decode(jarPath, "UTF-8"))) {
                        java.util.Enumeration<java.util.jar.JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            java.util.jar.JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.startsWith("books/") && name.endsWith(".yaml") && !name.equals("books/")) {
                                files.add(name);
                            }
                        }
                    }
                } else {
                    // Running from filesystem
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
    
    private ImageIcon getCoverImage(String gameFile) {
        try {
            // Extract folder name from yaml file (e.g., "books/game.yaml" -> "game")
            String fileName = gameFile.substring(gameFile.lastIndexOf('/') + 1);
            String folderName = fileName.replace(".yaml", "");
            String coverPath = "books/" + folderName + "/cover.jpg";
            
            InputStream coverStream = getClass().getClassLoader().getResourceAsStream(coverPath);
            if (coverStream != null) {
                java.awt.image.BufferedImage coverImage = javax.imageio.ImageIO.read(coverStream);
                // Scale image to fit button (height 140px, maintain aspect ratio)
                int targetHeight = 140;
                int targetWidth = (int) (coverImage.getWidth() * ((double) targetHeight / coverImage.getHeight()));
                java.awt.Image scaledImage = coverImage.getScaledInstance(targetWidth, targetHeight, java.awt.Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
                
                // Show potion selection if enabled
                if (adventure.init != null && adventure.init.potions) {
                    new PotionSelectionWindow(selectedPotion -> {
                        GameWindow gameWindow = new GameWindow(adventure, gameFile);
                        if (selectedPotion != null) {
                            gameWindow.getController().getHero().addItem(Item.createPotion(selectedPotion));
                            gameWindow.updateInventory();
                        }
                    });
                } else {
                    new GameWindow(adventure, gameFile);
                }
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
