package com.adventure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SaveGameManager {
    private static final ObjectMapper mapper = new ObjectMapper();
    
    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    public static void save(SaveGame saveGame, File file) throws IOException {
        mapper.writeValue(file, saveGame);
    }
    
    public static SaveGame load(File file) throws IOException {
        return mapper.readValue(file, SaveGame.class);
    }
    
    public static File getDefaultSaveDirectory() {
        String userHome = System.getProperty("user.home");
        File saveDir = new File(userHome, ".ff/saves");
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        return saveDir;
    }
    
    public static GameWindow loadAndStartGame(File saveFile) throws Exception {
        SaveGame saveGame = load(saveFile);
        
        // Load the game YAML from resource path
        Yaml yaml = new Yaml();
        InputStream input = SaveGameManager.class.getClassLoader().getResourceAsStream(saveGame.getGameYamlPath());
        if (input == null) {
            throw new Exception("Game file not found: " + saveGame.getGameYamlPath());
        }
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        if (adventure.language != null) {
            Messages.setLanguage(adventure.language);
        }
        
        // Create game window and load save
        GameWindow gameWindow = new GameWindow(adventure, saveGame.getGameYamlPath());
        gameWindow.getController().loadSaveGame(saveGame);
        gameWindow.updateDisplay();
        
        return gameWindow;
    }
}
