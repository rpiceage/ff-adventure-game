package com.adventure;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class NumberOfItemChoicesUITest {
    
    @Test
    public void testNumberOfItemChoicesButtonClickWithThreeRings() throws Exception {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-number-of-item-choices.yaml");
        Adventure adventure = yaml.load(input);
        
        GameWindow[] windowHolder = new GameWindow[1];
        
        SwingUtilities.invokeAndWait(() -> {
            windowHolder[0] = new GameWindow(adventure);
        });
        
        GameWindow window = windowHolder[0];
        
        try {
            // Add three rings
            window.getController().getHero().addItem("Gold Ring");
            window.getController().getHero().addItem("Gold Ring");
            window.getController().getHero().addItem("Gold Ring");
            
            // Navigate to chapter 1
            SwingUtilities.invokeAndWait(() -> {
                window.getController().goToChapter(1);
                window.updateDisplay();
            });
            
            Thread.sleep(100);
            
            // Verify we're at chapter 1
            SwingUtilities.invokeAndWait(() -> {
                assertEquals(1, window.getController().getCurrentChapter().index);
            });
            
            // Find and click the "I have three rings" button
            JButton threeRingsButton = findButton(window, "I have three rings");
            assertNotNull(threeRingsButton, "Three rings button should be present");
            
            SwingUtilities.invokeAndWait(() -> {
                threeRingsButton.doClick();
            });
            
            Thread.sleep(100);
            
            // Verify navigation to chapter 30
            SwingUtilities.invokeAndWait(() -> {
                assertEquals(30, window.getController().getCurrentChapter().index);
                assertTrue(window.getController().getCurrentChapter().actions.get(0).get("display").toString()
                    .contains("Correct! You have three rings"));
            });
            
        } finally {
            SwingUtilities.invokeAndWait(() -> window.dispose());
        }
    }
    
    @Test
    public void testNumberOfItemChoicesButtonClickWithTwoRings() throws Exception {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-number-of-item-choices.yaml");
        Adventure adventure = yaml.load(input);
        
        GameWindow[] windowHolder = new GameWindow[1];
        
        SwingUtilities.invokeAndWait(() -> {
            windowHolder[0] = new GameWindow(adventure);
        });
        
        GameWindow window = windowHolder[0];
        
        try {
            // Add two rings
            window.getController().getHero().addItem("Gold Ring");
            window.getController().getHero().addItem("Gold Ring");
            
            // Navigate to chapter 1
            SwingUtilities.invokeAndWait(() -> {
                window.getController().goToChapter(1);
                window.updateDisplay();
            });
            
            Thread.sleep(100);
            
            // Verify we're at chapter 1
            SwingUtilities.invokeAndWait(() -> {
                assertEquals(1, window.getController().getCurrentChapter().index);
            });
            
            // Find and click the "I have two rings" button
            JButton twoRingsButton = findButton(window, "I have two rings");
            assertNotNull(twoRingsButton, "Two rings button should be present");
            
            SwingUtilities.invokeAndWait(() -> {
                twoRingsButton.doClick();
            });
            
            Thread.sleep(100);
            
            // Verify navigation to chapter 20
            SwingUtilities.invokeAndWait(() -> {
                assertEquals(20, window.getController().getCurrentChapter().index);
                assertTrue(window.getController().getCurrentChapter().actions.get(0).get("display").toString()
                    .contains("Wrong! You had two rings"));
            });
            
        } finally {
            SwingUtilities.invokeAndWait(() -> window.dispose());
        }
    }
    
    @Test
    public void testOnlyMatchingButtonIsShown() throws Exception {
        Yaml yaml = new Yaml(new Constructor(Adventure.class, new LoaderOptions()));
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-number-of-item-choices.yaml");
        Adventure adventure = yaml.load(input);
        
        GameWindow[] windowHolder = new GameWindow[1];
        
        SwingUtilities.invokeAndWait(() -> {
            windowHolder[0] = new GameWindow(adventure);
        });
        
        GameWindow window = windowHolder[0];
        
        try {
            // Add one ring
            window.getController().getHero().addItem("Gold Ring");
            
            // Navigate to chapter 1
            SwingUtilities.invokeAndWait(() -> {
                window.getController().goToChapter(1);
                window.updateDisplay();
            });
            
            Thread.sleep(100);
            
            // Verify only the "I have one ring" button is shown
            SwingUtilities.invokeAndWait(() -> {
                JButton oneRingButton = findButton(window, "I have one ring");
                assertNotNull(oneRingButton, "One ring button should be present");
                
                JButton twoRingsButton = findButton(window, "I have two rings");
                assertNull(twoRingsButton, "Two rings button should NOT be present");
                
                JButton threeRingsButton = findButton(window, "I have three rings");
                assertNull(threeRingsButton, "Three rings button should NOT be present");
            });
            
        } finally {
            SwingUtilities.invokeAndWait(() -> window.dispose());
        }
    }
    
    private JButton findButton(Container container, String text) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (button.getText().equals(text)) {
                    return button;
                }
            }
            if (comp instanceof Container) {
                JButton found = findButton((Container) comp, text);
                if (found != null) return found;
            }
        }
        return null;
    }
}
