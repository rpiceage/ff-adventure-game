package com.adventure;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class BattleInterruptReturnFullUITest {
    private GameWindow window;

    @AfterEach
    public void cleanup() throws Exception {
        if (window != null) {
            SwingUtilities.invokeAndWait(() -> window.dispose());
        }
    }

    @Test
    public void testInterruptReturnFullFlow() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-interrupt-return-ui-test.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);

        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
        });

        // Start battle
        SwingUtilities.invokeAndWait(() -> {
            JButton battleButton = findButton(window, Messages.get(Messages.Key.BATTLE_BEGIN));
            assertNotNull(battleButton, "Battle button should be present");
            battleButton.doClick();
        });

        Thread.sleep(100);

        // Execute first turn (hero should win against weak enemy)
        SwingUtilities.invokeAndWait(() -> {
            JButton nextTurnButton = findButton(window, Messages.get(Messages.Key.BATTLE_NEXT_TURN));
            assertNotNull(nextTurnButton, "Next turn button should be present");
            nextTurnButton.doClick();
        });

        // Wait for dice animation
        Thread.sleep(1500);

        // Click "Ask for help" button
        SwingUtilities.invokeAndWait(() -> {
            JButton helpButton = findButton(window, "Ask for help");
            assertNotNull(helpButton, "Ask for help button should be present");
            helpButton.doClick();
        });

        Thread.sleep(100);

        // Verify we're at interrupt chapter - check text content
        SwingUtilities.invokeAndWait(() -> {
            JTextArea textArea = findTextArea(window);
            assertNotNull(textArea, "Text area should be present");
            String text = textArea.getText();
            assertTrue(text.contains("You call for help"), "Should show interrupt text");
            
            JButton continueButton = findButton(window, "Continue");
            assertNotNull(continueButton, "Continue button should be present at interrupt chapter");
            continueButton.doClick();
        });

        Thread.sleep(100);

        // Verify battle is resumed - check for "Battle resumed!" message
        SwingUtilities.invokeAndWait(() -> {
            JTextArea textArea = findTextArea(window);
            assertNotNull(textArea, "Text area should be present");
            String battleLog = textArea.getText();
            assertTrue(battleLog.contains("Battle resumed!"), "Battle log should show 'Battle resumed!'");
            
            JButton nextTurnButton = findButton(window, Messages.get(Messages.Key.BATTLE_NEXT_TURN));
            assertNotNull(nextTurnButton, "Next turn button should be present after resume");
        });

        // Execute another turn to verify battle continues without errors
        SwingUtilities.invokeAndWait(() -> {
            JButton nextTurnButton = findButton(window, Messages.get(Messages.Key.BATTLE_NEXT_TURN));
            nextTurnButton.doClick();
        });

        Thread.sleep(1500);

        // Verify battle is still active
        SwingUtilities.invokeAndWait(() -> {
            JButton nextTurnButton = findButton(window, Messages.get(Messages.Key.BATTLE_NEXT_TURN));
            assertNotNull(nextTurnButton, "Battle should continue after resume");
        });
    }

    private JButton findButton(Container container, String text) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (button.getText() != null && button.getText().contains(text)) {
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
    
    private JTextArea findTextArea(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JTextArea) {
                return (JTextArea) comp;
            }
            if (comp instanceof Container) {
                JTextArea found = findTextArea((Container) comp);
                if (found != null) return found;
            }
        }
        return null;
    }
}
