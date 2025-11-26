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

public class SellItemUITest {
    private GameWindow window;

    @AfterEach
    public void cleanup() throws Exception {
        if (window != null) {
            SwingUtilities.invokeAndWait(() -> window.dispose());
        }
    }

    @Test
    public void testSellItem_ButtonsDisabledAfterMaxCount() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-sell-item.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);

        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
        });

        // Take all 5 items
        SwingUtilities.invokeAndWait(() -> {
            JButton swordButton = findButton(window, "Take Sword");
            assertNotNull(swordButton);
            swordButton.doClick();
            
            JButton shieldButton = findButton(window, "Take Shield");
            assertNotNull(shieldButton);
            shieldButton.doClick();
            
            JButton potionButton = findButton(window, "Take Potion");
            assertNotNull(potionButton);
            potionButton.doClick();
            
            JButton ringButton = findButton(window, "Take Ring");
            assertNotNull(ringButton);
            ringButton.doClick();
            
            JButton amuletButton = findButton(window, "Take Amulet");
            assertNotNull(amuletButton);
            amuletButton.doClick();
        });

        // Verify all items in inventory
        GameController controller = getField(window, "controller");
        assertEquals(5, controller.getHero().getInventory().size());
        assertEquals(0, controller.getHero().getGold());

        // Sell first item (Sword)
        SwingUtilities.invokeAndWait(() -> {
            JButton swordItem = findItemButton(window, "Sword");
            assertNotNull(swordItem);
            assertTrue(swordItem.isEnabled());
            swordItem.doClick();
        });

        assertEquals(4, controller.getHero().getInventory().size());
        assertEquals(50, controller.getHero().getGold());

        // Sell second item (Shield)
        SwingUtilities.invokeAndWait(() -> {
            JButton shieldItem = findItemButton(window, "Shield");
            assertNotNull(shieldItem);
            assertTrue(shieldItem.isEnabled());
            shieldItem.doClick();
        });

        assertEquals(3, controller.getHero().getInventory().size());
        assertEquals(100, controller.getHero().getGold());

        // Sell third item (Potion)
        SwingUtilities.invokeAndWait(() -> {
            JButton potionItem = findItemButton(window, "Potion");
            assertNotNull(potionItem);
            assertTrue(potionItem.isEnabled());
            potionItem.doClick();
        });

        assertEquals(2, controller.getHero().getInventory().size());
        assertEquals(150, controller.getHero().getGold());

        // Verify remaining items are disabled (max 3 items sold)
        SwingUtilities.invokeAndWait(() -> {
            JButton ringItem = findItemButton(window, "Ring");
            assertNotNull(ringItem);
            assertFalse(ringItem.isEnabled(), "Ring button should be disabled after selling 3 items");
            
            JButton amuletItem = findItemButton(window, "Amulet");
            assertNotNull(amuletItem);
            assertFalse(amuletItem.isEnabled(), "Amulet button should be disabled after selling 3 items");
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

    private JButton findItemButton(Container container, String itemName) {
        return findButton(container, itemName);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(obj);
    }
}
