package com.adventure;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;
import java.io.FileInputStream;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws Exception {
        Adventure adventure;
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        
        if (args.length > 0) {
            // Load from file path argument
            adventure = yaml.load(new FileInputStream(args[0]));
        } else {
            // Load default from classpath
            InputStream input = Main.class.getClassLoader().getResourceAsStream("sample-complete.yaml");
            if (input == null) {
                throw new RuntimeException("Default adventure file not found");
            }
            adventure = yaml.load(input);
        }
        
        if (adventure.language != null) {
            Messages.setLanguage(adventure.language);
        }
        
        new GameWindow(adventure);
    }
}
