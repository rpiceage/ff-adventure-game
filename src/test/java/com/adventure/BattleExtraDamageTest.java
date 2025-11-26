package com.adventure;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class BattleExtraDamageTest {
    private GameWindow window;

    @AfterEach
    public void cleanup() throws Exception {
        if (window != null) {
            SwingUtilities.invokeAndWait(() -> window.dispose());
        }
    }

    @Test
    public void testExtraDamage_ButtonChanges() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-extra-damage.yaml");
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

        // Execute first turn
        SwingUtilities.invokeAndWait(() -> {
            JButton nextTurnButton = findButton(window, Messages.get(Messages.Key.BATTLE_NEXT_TURN));
            assertNotNull(nextTurnButton, "Next turn button should be present");
            nextTurnButton.doClick();
        });

        // Wait for dice animation
        Thread.sleep(1500);

        // Verify extra damage button appears
        SwingUtilities.invokeAndWait(() -> {
            JButton extraDamageButton = findButton(window, Messages.get(Messages.Key.BATTLE_EXTRA_DAMAGE_BUTTON));
            assertNotNull(extraDamageButton, "Extra damage button should appear after turn");
            
            // Click extra damage button
            extraDamageButton.doClick();
        });

        // Wait for extra damage dice animation
        Thread.sleep(1500);

        // Verify button changes back to Next Turn
        SwingUtilities.invokeAndWait(() -> {
            JButton nextTurnButton = findButton(window, Messages.get(Messages.Key.BATTLE_NEXT_TURN));
            assertNotNull(nextTurnButton, "Next turn button should reappear after extra damage");
            
            JButton extraDamageButton = findButton(window, Messages.get(Messages.Key.BATTLE_EXTRA_DAMAGE_BUTTON));
            assertNull(extraDamageButton, "Extra damage button should be gone");
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

    @SuppressWarnings("unchecked")
    private <T> T getField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(obj);
    }
}
