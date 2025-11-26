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

public class AddItemMaxItemsUITest {
    private GameWindow window;

    @AfterEach
    public void cleanup() throws Exception {
        if (window != null) {
            SwingUtilities.invokeAndWait(() -> window.dispose());
        }
    }

    @Test
    public void testAddItem_MaxItemsDisablesRemainingButtons() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-max-items.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);

        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
        });

        GameController controller = getField(window, "controller");
        
        // Initially no items
        assertEquals(0, controller.getHero().getInventory().size());

        // Take first item (Sword)
        SwingUtilities.invokeAndWait(() -> {
            JButton swordButton = findButton(window, "Take Sword");
            assertNotNull(swordButton);
            assertTrue(swordButton.isEnabled());
            swordButton.doClick();
        });

        assertEquals(1, controller.getHero().getInventory().size());

        // Take second item (Shield)
        SwingUtilities.invokeAndWait(() -> {
            JButton shieldButton = findButton(window, "Take Shield");
            assertNotNull(shieldButton);
            assertTrue(shieldButton.isEnabled());
            shieldButton.doClick();
        });

        assertEquals(2, controller.getHero().getInventory().size());

        // Take third item (Potion) - this should disable remaining buttons
        SwingUtilities.invokeAndWait(() -> {
            JButton potionButton = findButton(window, "Take Potion");
            assertNotNull(potionButton);
            assertTrue(potionButton.isEnabled());
            potionButton.doClick();
        });

        assertEquals(3, controller.getHero().getInventory().size());

        // Verify remaining buttons are disabled
        SwingUtilities.invokeAndWait(() -> {
            JButton ringButton = findButton(window, "Take Ring");
            assertNotNull(ringButton);
            assertFalse(ringButton.isEnabled(), "Ring button should be disabled after taking 3 items");
            
            JButton amuletButton = findButton(window, "Take Amulet");
            assertNotNull(amuletButton);
            assertFalse(amuletButton.isEnabled(), "Amulet button should be disabled after taking 3 items");
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
