package com.adventure;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;
import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws Exception {
        // Set dark theme
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            
            // Dark theme colors
            Color darkBg = new Color(43, 43, 43);
            Color darkFg = new Color(187, 187, 187);
            Color darkPanel = new Color(60, 63, 65);
            Color darkButton = new Color(75, 110, 175);
            
            UIManager.put("Panel.background", darkBg);
            UIManager.put("Button.background", darkButton);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Label.foreground", darkFg);
            UIManager.put("List.background", darkPanel);
            UIManager.put("List.foreground", darkFg);
            UIManager.put("ScrollPane.background", darkBg);
            UIManager.put("OptionPane.background", darkBg);
            UIManager.put("OptionPane.messageForeground", darkFg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (args.length > 0) {
            // Load from file path argument
            Adventure adventure;
            Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
            adventure = yaml.load(new FileInputStream(args[0]));
            
            if (adventure.language != null) {
                Messages.setLanguage(adventure.language);
            }
            
            new GameWindow(adventure);
        } else {
            // Show welcome menu
            new WelcomeWindow();
        }
    }
}
