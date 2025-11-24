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

    @Test
    public void testProvisionsButton_DisplaysCorrectly() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-provisions.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);

        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
            window.setVisible(true);
        });

        Thread.sleep(200);

        SwingUtilities.invokeAndWait(() -> {
            try {
                JButton provisionsButton = getField(window, "provisionsButton");
                assertNotNull(provisionsButton, "Provisions button should exist");
                assertEquals(Messages.get(Messages.Key.PROVISIONS) + ": 3", provisionsButton.getText());
                assertTrue(provisionsButton.isEnabled(), "Button should be enabled when provisions > 0");
            } catch (Exception e) {
                fail("Failed to get provisionsButton: " + e.getMessage());
            }
        });
    }

    @Test
    public void testProvisionsButton_ConsumesProvision() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-provisions.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);

        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
            window.setVisible(true);
        });

        Thread.sleep(100);

        SwingUtilities.invokeAndWait(() -> {
            try {
                GameController controller = getField(window, "controller");
                Hero hero = controller.getHero();
                
                // Lose stamina first
                hero.modifyStaminaSilent(-5);
                assertEquals(19, hero.getStamina());
                
                // Click provisions button
                JButton provisionsButton = getField(window, "provisionsButton");
                provisionsButton.doClick();
                
                // Check stamina restored and provisions decreased
                assertEquals(23, hero.getStamina());
                assertEquals(2, hero.getProvisions());
                assertEquals(Messages.get(Messages.Key.PROVISIONS) + ": 2", provisionsButton.getText());
            } catch (Exception e) {
                fail("Failed to consume provision: " + e.getMessage());
            }
        });
    }

    @Test
    public void testProvisionsButton_DisabledWhenEmpty() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-provisions.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);

        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
            window.setVisible(true);
        });

        Thread.sleep(100);

        SwingUtilities.invokeAndWait(() -> {
            try {
                GameController controller = getField(window, "controller");
                Hero hero = controller.getHero();
                JButton provisionsButton = getField(window, "provisionsButton");
                
                // Consume all provisions
                hero.modifyStaminaSilent(-12);
                hero.consumeProvision();
                hero.consumeProvision();
                hero.consumeProvision();
                
                assertEquals(0, hero.getProvisions());
                
                // Update UI
                window.updateHeroStats();
                
                // Button should be disabled
                assertFalse(provisionsButton.isEnabled(), "Button should be disabled when provisions = 0");
                assertEquals(Messages.get(Messages.Key.PROVISIONS) + ": 0", provisionsButton.getText());
            } catch (Exception e) {
                fail("Failed to test empty provisions: " + e.getMessage());
            }
        });
    }

    @Test
    public void testProvisionsModification_UpdatesButton() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-modify-provisions.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);

        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
            window.setVisible(true);
        });

        Thread.sleep(200);

        SwingUtilities.invokeAndWait(() -> {
            try {
                JButton provisionsButton = getField(window, "provisionsButton");
                assertEquals(Messages.get(Messages.Key.PROVISIONS) + ": 2", provisionsButton.getText());
                
                // Click continue to go to chapter with modification
                JPanel buttonPanel = getField(window, "buttonPanel");
                JButton continueButton = findButton(buttonPanel, "Continue");
                continueButton.doClick();
            } catch (Exception e) {
                fail("Failed to click button: " + e.getMessage());
            }
        });

        Thread.sleep(200);

        SwingUtilities.invokeAndWait(() -> {
            try {
                JButton provisionsButton = getField(window, "provisionsButton");
                assertEquals(Messages.get(Messages.Key.PROVISIONS) + ": 5", provisionsButton.getText());
            } catch (Exception e) {
                fail("Failed to verify provisions: " + e.getMessage());
            }
        });
    }

    @Test
    public void testDeathAction_ShowsSkull() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-death.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);

        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
            window.setVisible(true);
        });

        Thread.sleep(200);

        SwingUtilities.invokeAndWait(() -> {
            try {
                // Click the wrong door button
                JPanel buttonPanel = getField(window, "buttonPanel");
                JButton wrongDoorButton = findButton(buttonPanel, "Right door");
                assertNotNull(wrongDoorButton, "Right door button should exist");
                wrongDoorButton.doClick();
            } catch (Exception e) {
                fail("Failed to click button: " + e.getMessage());
            }
        });

        Thread.sleep(200);

        SwingUtilities.invokeAndWait(() -> {
            try {
                GameController controller = getField(window, "controller");
                
                // Hero should be dead
                assertTrue(controller.isGameOver());
                assertEquals(0, controller.getHero().getStamina());
                
                // Should show custom death text, not default game over message
                JTextArea textArea = getField(window, "textArea");
                String displayedText = textArea.getText();
                assertTrue(displayedText.contains("You choose wrongly"), 
                    "Should show custom death text");
                assertTrue(displayedText.contains("Your adventure ends here"), 
                    "Should show custom death text");
                
                // Stats panel should be removed (replaced by skull)
                JPanel statsPanel = getField(window, "statsPanel");
                assertFalse(statsPanel.isShowing(), "Stats panel should not be showing after death");
            } catch (Exception e) {
                fail("Failed to test death action: " + e.getMessage());
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
    
    @Test
    public void testButtonPanelScrollsWithManyButtons() throws Exception {
        // Create a YAML with many long button choices
        String yaml = "title: Scroll Test\n" +
                      "chapters:\n" +
                      "  - index: 0\n" +
                      "    actions:\n" +
                      "      - display: Choose one\n" +
                      "      - goto:\n" +
                      "          - chapter: 1\n" +
                      "            text: Very Long Button Text Number One\n" +
                      "          - chapter: 1\n" +
                      "            text: Very Long Button Text Number Two\n" +
                      "          - chapter: 1\n" +
                      "            text: Very Long Button Text Number Three\n" +
                      "          - chapter: 1\n" +
                      "            text: Very Long Button Text Number Four\n" +
                      "          - chapter: 1\n" +
                      "            text: Very Long Button Text Number Five\n" +
                      "          - chapter: 1\n" +
                      "            text: Very Long Button Text Number Six\n" +
                      "          - chapter: 1\n" +
                      "            text: Very Long Button Text Number Seven\n" +
                      "          - chapter: 1\n" +
                      "            text: Very Long Button Text Number Eight\n" +
                      "          - chapter: 1\n" +
                      "            text: Very Long Button Text Number Nine\n" +
                      "          - chapter: 1\n" +
                      "            text: Very Long Button Text Number Ten\n" +
                      "          - chapter: 1\n" +
                      "            text: Very Long Button Text Number Eleven\n" +
                      "          - chapter: 1\n" +
                      "            text: Very Long Button Text Number Twelve\n" +
                      "          - chapter: 1\n" +
                      "            text: Very Long Button Text Number Thirteen\n" +
                      "          - chapter: 1\n" +
                      "            text: Very Long Button Text Number Fourteen\n" +
                      "          - chapter: 1\n" +
                      "            text: Very Long Button Text Number Fifteen\n" +
                      "          - chapter: 1\n" +
                      "            text: Very Long Button Text Number Sixteen\n" +
                      "  - index: 1\n" +
                      "    actions:\n" +
                      "      - display: Done\n";
        
        Yaml yamlParser = new Yaml(new LoaderOptions());
        Adventure adventure = yamlParser.loadAs(yaml, Adventure.class);
        
        SwingUtilities.invokeAndWait(() -> {
            window = new GameWindow(adventure);
        });
        
        Thread.sleep(100); // Let UI settle
        
        JScrollPane buttonScrollPane = getField(window, "buttonScrollPane");
        JPanel buttonPanel = getField(window, "buttonPanel");
        
        assertNotNull(buttonScrollPane);
        assertNotNull(buttonPanel);
        
        // Check if button panel height exceeds scroll pane viewport height
        int panelHeight = buttonPanel.getPreferredSize().height;
        int viewportHeight = buttonScrollPane.getViewport().getHeight();
        
        System.out.println("Button panel preferred height: " + panelHeight);
        System.out.println("Viewport height: " + viewportHeight);
        System.out.println("Scroll pane height: " + buttonScrollPane.getHeight());
        
        // If panel is taller than viewport, scrollbar should be visible
        if (panelHeight > viewportHeight) {
            JScrollBar verticalScrollBar = buttonScrollPane.getVerticalScrollBar();
            assertTrue(verticalScrollBar.isVisible(), "Vertical scrollbar should be visible when content exceeds viewport");
        }
    }
    
    @Test
    public void testBattleEscapeYamlLoads() throws Exception {
        // Simple test to verify battle escape YAML loads correctly
        InputStream input = getClass().getClassLoader().getResourceAsStream("sample-with-battle-escape.yaml");
        Yaml yaml = new Yaml(new LoaderOptions());
        Adventure adventure = yaml.loadAs(input, Adventure.class);
        
        assertNotNull(adventure);
        assertEquals("Battle Escape Test", adventure.title);
        
        // Verify battle has escape configuration
        var battleAction = adventure.chapters.get(0).actions.stream()
            .filter(a -> a.containsKey("battle"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(battleAction);
        var battleData = (java.util.Map<String, Object>) battleAction.get("battle");
        assertTrue(battleData.containsKey("escape"));
        
        var escapeData = (java.util.Map<String, Object>) battleData.get("escape");
        assertEquals(2, escapeData.get("turn"));
        assertEquals(2, escapeData.get("chapter"));
    }
}
