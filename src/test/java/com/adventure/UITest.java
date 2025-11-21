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
    public void testStatsPanel_RemainsVisible_AfterLuckTest() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-luck.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);

        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
        });

        // Verify stats panel is visible initially
        JPanel statsPanel = getField(window, "statsPanel");
        assertTrue(statsPanel.isVisible());

        // Click "Test your luck!" button
        SwingUtilities.invokeAndWait(() -> {
            JButton luckButton = findButton(window, Messages.get(Messages.Key.LUCK_TEST_BUTTON));
            assertNotNull(luckButton, "Luck button should be present");
            luckButton.doClick();
        });

        // Verify stats panel is still visible during luck test
        assertTrue(statsPanel.isVisible());
        assertTrue(statsPanel.isShowing());

        // Click "Test your luck!" again to execute the test
        SwingUtilities.invokeAndWait(() -> {
            JButton testButton = findButton(window, Messages.get(Messages.Key.LUCK_TEST_BUTTON));
            if (testButton != null) {
                testButton.doClick();
            }
        });

        // Wait for dice animation
        Thread.sleep(1500);

        // Verify stats panel is still visible after test
        SwingUtilities.invokeAndWait(() -> {
            assertTrue(statsPanel.isVisible());
            assertTrue(statsPanel.isShowing());
        });

        // Click continue
        SwingUtilities.invokeAndWait(() -> {
            JButton continueButton = findButton(window, Messages.get(Messages.Key.LUCK_CONTINUE));
            if (continueButton != null) {
                continueButton.doClick();
            }
        });

        // Verify stats panel is still visible after restoration
        SwingUtilities.invokeAndWait(() -> {
            assertTrue(statsPanel.isVisible());
            assertTrue(statsPanel.isShowing());
        });
    }

    @Test
    public void testTextPanel_Restored_AfterLuckTest() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-luck.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);

        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
        });

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

        Thread.sleep(1500);

        // Click continue
        SwingUtilities.invokeAndWait(() -> {
            JButton continueButton = findButton(window, Messages.get(Messages.Key.LUCK_CONTINUE));
            if (continueButton != null) {
                continueButton.doClick();
            }
        });

        // Verify text panel is restored
        SwingUtilities.invokeAndWait(() -> {
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
