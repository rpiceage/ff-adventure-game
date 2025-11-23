package com.adventure;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;
import java.io.FileInputStream;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws Exception {
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
