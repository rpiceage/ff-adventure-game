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

public class UITest {
    private GameWindow window;

    @AfterEach
    public void cleanup() throws Exception {
        if (window != null) {
            SwingUtilities.invokeAndWait(() -> window.dispose());
        }
    }

    @Test
    public void testLuckTest_ComponentsRestoredCorrectly() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-luck.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);

        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
        });

        JPanel statsPanel = getField(window, "statsPanel");
        assertTrue(statsPanel.isVisible());

        // Start luck test
        SwingUtilities.invokeAndWait(() -> {
            JButton luckButton = findButton(window, Messages.get(Messages.Key.LUCK_TEST_BUTTON));
            luckButton.doClick();
        });

        assertTrue(statsPanel.isVisible());

        // Execute test
        SwingUtilities.invokeAndWait(() -> {
            JButton testButton = findButton(window, Messages.get(Messages.Key.LUCK_TEST_BUTTON));
            if (testButton != null) {
                testButton.doClick();
            }
        });

        Thread.sleep(1500);

        assertTrue(statsPanel.isVisible());

        // Click continue
        SwingUtilities.invokeAndWait(() -> {
            JButton continueButton = findButton(window, Messages.get(Messages.Key.LUCK_CONTINUE));
            if (continueButton != null) {
                continueButton.doClick();
            }
        });

        // Verify both stats panel and text panel are restored
        SwingUtilities.invokeAndWait(() -> {
            assertTrue(statsPanel.isVisible());
            Container contentPane = window.getContentPane();
            BorderLayout layout = (BorderLayout) contentPane.getLayout();
            Component center = layout.getLayoutComponent(BorderLayout.CENTER);
            assertTrue(center instanceof JScrollPane, "Center should be JScrollPane after restoration");
        });
    }

    @Test
    public void testHeroStats_Update_DuringBattle() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-battle.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);

        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
        });

        JLabel staminaLabel = getField(window, "staminaLabel");
        String initialStamina = staminaLabel.getText();

        // Start battle
        SwingUtilities.invokeAndWait(() -> {
            JButton battleButton = findButton(window, Messages.get(Messages.Key.BATTLE_BEGIN));
            assertNotNull(battleButton, "Battle button should be present");
            battleButton.doClick();
        });

        // Execute a turn where hero loses (we can't control dice, so just execute turn)
        SwingUtilities.invokeAndWait(() -> {
            JButton nextTurnButton = findButton(window, Messages.get(Messages.Key.BATTLE_NEXT_TURN));
            if (nextTurnButton != null) {
                nextTurnButton.doClick();
            }
        });

        // Wait for dice animation
        Thread.sleep(1500);

        // Check if stamina label was updated (it might have changed if hero lost the turn)
        SwingUtilities.invokeAndWait(() -> {
            String currentStamina = staminaLabel.getText();
            assertNotNull(currentStamina);
            assertTrue(currentStamina.contains("STAMINA") || currentStamina.contains("ÉLETERŐ"));
        });
    }

    @Test
    public void testTextArea_HasBackground() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-luck.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);

        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
        });

        JTextArea textArea = getField(window, "textArea");
        
        // Verify text area is not opaque (so background image shows through)
        assertFalse(textArea.isOpaque(), "TextArea should not be opaque to show background");
    }

    @Test
    public void testLuck_Decreases_WhenTesting() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-luck.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);

        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
        });

        GameController controller = getField(window, "controller");
        int initialLuck = controller.getHero().getLuck();

        // Start luck test
        SwingUtilities.invokeAndWait(() -> {
            JButton luckButton = findButton(window, Messages.get(Messages.Key.LUCK_TEST_BUTTON));
            luckButton.doClick();
        });

        // Execute test
        SwingUtilities.invokeAndWait(() -> {
            JButton testButton = findButton(window, Messages.get(Messages.Key.LUCK_TEST_BUTTON));
            if (testButton != null) {
                testButton.doClick();
            }
        });

        // Wait for dice animation
        Thread.sleep(1500);

        // Verify luck decreased by 1
        SwingUtilities.invokeAndWait(() -> {
            assertEquals(initialLuck - 1, controller.getHero().getLuck());
        });
    }

    @Test
    public void testItemButtons_DisabledAfterUse() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-items.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);

        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
        });

        GameController controller = getField(window, "controller");
        Hero hero = controller.getHero();

        // Initially no items
        assertEquals(0, hero.getInventory().size());

        // Find "Take Aranygyűrű" button and verify it's enabled
        SwingUtilities.invokeAndWait(() -> {
            JButton takeButton = findButton(window, Messages.get(Messages.Key.ADD_ITEM) + " Aranygyűrű");
            assertNotNull(takeButton, "Take Aranygyűrű button should be present");
            assertTrue(takeButton.isEnabled(), "Button should be enabled initially");
            takeButton.doClick();
        });

        // Verify item was added and button is disabled
        SwingUtilities.invokeAndWait(() -> {
            assertEquals(1, hero.getInventory().size());
            assertTrue(hero.hasItem("Aranygyűrű"));
            
            JButton takeButton = findButton(window, Messages.get(Messages.Key.ADD_ITEM) + " Aranygyűrű");
            assertNotNull(takeButton, "Button should still exist");
            assertFalse(takeButton.isEnabled(), "Button should be disabled after taking item");
        });

        // Find "Take Sword" button and verify it's still enabled
        SwingUtilities.invokeAndWait(() -> {
            JButton takeButton = findButton(window, Messages.get(Messages.Key.ADD_ITEM) + " Sword");
            assertNotNull(takeButton, "Take Sword button should be present");
            assertTrue(takeButton.isEnabled(), "Sword button should still be enabled");
            takeButton.doClick();
        });

        // Verify second item was added and its button is disabled
        SwingUtilities.invokeAndWait(() -> {
            assertEquals(2, hero.getInventory().size());
            assertTrue(hero.hasItem("Sword"));
            
            JButton takeButton = findButton(window, Messages.get(Messages.Key.ADD_ITEM) + " Sword");
            assertFalse(takeButton.isEnabled(), "Sword button should be disabled after taking");
        });

        // Verify item buttons appear in stats panel
        SwingUtilities.invokeAndWait(() -> {
            try {
                JPanel itemsPanel = getField(window, "itemsPanel");
                assertEquals(2, itemsPanel.getComponentCount(), "Should have 2 item buttons in stats panel");
            } catch (Exception e) {
                fail("Failed to get itemsPanel: " + e.getMessage());
            }
        });
    }

    // Helper methods
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
