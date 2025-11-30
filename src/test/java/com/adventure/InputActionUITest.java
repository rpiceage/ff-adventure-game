package com.adventure;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

public class InputActionUITest {
    private GameWindow window;

    @AfterEach
    public void cleanup() throws Exception {
        if (window != null) {
            SwingUtilities.invokeAndWait(() -> window.dispose());
        }
    }
    
    private JTextField findTextField(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JTextField) {
                return (JTextField) comp;
            }
            if (comp instanceof Container) {
                JTextField found = findTextField((Container) comp);
                if (found != null) return found;
            }
        }
        return null;
    }
    
    private JButton findButton(Container container, String text) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (btn.getText().equals(text)) {
                    return btn;
                }
            }
            if (comp instanceof Container) {
                JButton found = findButton((Container) comp, text);
                if (found != null) return found;
            }
        }
        return null;
    }
    
    @Test
    public void testInputFieldAndSubmitButtonAppear() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-input.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
        });
        
        JTextField inputField = findTextField(window);
        assertNotNull(inputField, "Input field should be present");
        
        JButton submitButton = findButton(window, Messages.get(Messages.Key.SUBMIT));
        assertNotNull(submitButton, "Submit button should be present");
    }
    
    @Test
    public void testCorrectIntAnswerNavigatesToChapter() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-input.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
        });
        
        JTextField inputField = findTextField(window);
        JButton submitButton = findButton(window, Messages.get(Messages.Key.SUBMIT));
        
        SwingUtilities.invokeAndWait(() -> {
            inputField.setText("42");
            submitButton.doClick();
        });
        
        Thread.sleep(100);
        
        assertEquals(1, window.getController().getCurrentChapter().index);
        assertTrue(window.getController().getDisplayText().contains("Correct!"));
    }
    
    @Test
    public void testWrongAnswerShowsNotificationAndRemovesInput() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-input.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
        });
        
        JTextField inputField = findTextField(window);
        JButton submitButton = findButton(window, Messages.get(Messages.Key.SUBMIT));
        
        SwingUtilities.invokeAndWait(() -> {
            inputField.setText("99");
            submitButton.doClick();
        });
        
        Thread.sleep(100);
        
        assertEquals(0, window.getController().getCurrentChapter().index);
        assertNull(findTextField(window), "Input field should be removed after wrong answer");
        assertNull(findButton(window, Messages.get(Messages.Key.SUBMIT)), "Submit button should be removed after wrong answer");
    }
    
    @Test
    public void testEnterKeyTriggersSubmit() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-input.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
        });
        
        JTextField inputField = findTextField(window);
        
        SwingUtilities.invokeAndWait(() -> {
            inputField.setText("42");
            inputField.postActionEvent();
        });
        
        Thread.sleep(100);
        
        assertEquals(1, window.getController().getCurrentChapter().index);
    }
    
    @Test
    public void testGotoButtonStillAvailable() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-input.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
        });
        
        JButton gotoButton = findButton(window, "I don't know the number");
        assertNotNull(gotoButton, "Goto button should be available alongside input");
        
        SwingUtilities.invokeAndWait(() -> {
            gotoButton.doClick();
        });
        
        Thread.sleep(100);
        
        assertEquals(2, window.getController().getCurrentChapter().index);
    }
}
