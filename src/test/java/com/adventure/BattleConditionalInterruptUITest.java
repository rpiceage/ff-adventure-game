package com.adventure;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class BattleConditionalInterruptUITest {
    private GameWindow window;

    @AfterEach
    public void cleanup() throws Exception {
        if (window != null) {
            SwingUtilities.invokeAndWait(() -> window.dispose());
        }
    }

    @Test
    public void testConditionalInterrupt_ButtonChanges() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-conditional-interrupt-ui.yaml");
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

        // Execute first turn (hero should win against weak enemy)
        SwingUtilities.invokeAndWait(() -> {
            JButton nextTurnButton = findButton(window, Messages.get(Messages.Key.BATTLE_NEXT_TURN));
            assertNotNull(nextTurnButton, "Next turn button should be present");
            nextTurnButton.doClick();
        });

        // Wait for dice animation
        Thread.sleep(1500);

        // Verify extra roll button appears
        SwingUtilities.invokeAndWait(() -> {
            JButton interruptButton = findButton(window, Messages.get(Messages.Key.BATTLE_INTERRUPT_ROLL));
            assertNotNull(interruptButton, "Extra roll button should appear after hero wins turn");
            
            // Click extra roll button
            interruptButton.doClick();
        });

        // Wait for interrupt dice animation
        Thread.sleep(1500);

        // Verify battle was interrupted and navigated to chapter 1
        SwingUtilities.invokeAndWait(() -> {
            JButton interruptButton = findButton(window, Messages.get(Messages.Key.BATTLE_INTERRUPT_ROLL));
            assertNull(interruptButton, "Extra roll button should be gone after interrupt");
            
            JButton nextTurnButton = findButton(window, Messages.get(Messages.Key.BATTLE_NEXT_TURN));
            assertNull(nextTurnButton, "Next turn button should be gone after interrupt");
        });
    }

    private JButton findButton(Container container, String text) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton && ((JButton) c).getText().equals(text)) {
                return (JButton) c;
            }
            if (c instanceof Container) {
                JButton found = findButton((Container) c, text);
                if (found != null) return found;
            }
        }
        return null;
    }
}
