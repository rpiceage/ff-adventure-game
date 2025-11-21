package com.adventure;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;
import java.io.FileInputStream;

public class Main {
    public static void main(String[] args) throws Exception {
        String yamlFile = args.length > 0 ? args[0] : ".amazonq/resources/sample.yaml";
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        Adventure adventure = yaml.load(new FileInputStream(yamlFile));
        
        if (adventure.language != null) {
            Messages.setLanguage(adventure.language);
        }
        
        new GameWindow(adventure);
    }
}
