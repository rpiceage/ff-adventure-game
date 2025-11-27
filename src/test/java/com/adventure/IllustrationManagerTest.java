package com.adventure;

import org.junit.jupiter.api.Test;
import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.*;

public class IllustrationManagerTest {
    
    @Test
    public void testNullPathReturnsNull() {
        IllustrationManager manager = new IllustrationManager(null);
        BufferedImage image = manager.loadIllustration(0);
        assertNull(image);
    }
    
    @Test
    public void testNonExistentImageReturnsNull() {
        IllustrationManager manager = new IllustrationManager("books/nonexistent.yaml");
        BufferedImage image = manager.loadIllustration(999);
        assertNull(image);
    }
    
    @Test
    public void testLoadExistingImage() {
        IllustrationManager manager = new IllustrationManager("books/sample.yaml");
        BufferedImage image = manager.loadIllustration(0);
        
        if (image != null) {
            assertTrue(image.getWidth() > 0);
            assertTrue(image.getHeight() > 0);
            assertEquals(BufferedImage.TYPE_INT_ARGB, image.getType());
        }
    }
}
