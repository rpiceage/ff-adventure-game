package com.adventure;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Random;

public class IllustrationManager {
    private final String gameYamlPath;
    private final Random random = new Random();
    
    public IllustrationManager(String gameYamlPath) {
        this.gameYamlPath = gameYamlPath;
    }
    
    public BufferedImage loadIllustration(int chapterIndex) {
        if (gameYamlPath == null) {
            return null;
        }
        
        try {
            String fileName = gameYamlPath.substring(gameYamlPath.lastIndexOf('/') + 1);
            String folderName = fileName.replace(".yaml", "");
            
            String imagePath = "books/" + folderName + "/" + chapterIndex + ".jpg";
            InputStream imageStream = getClass().getClassLoader().getResourceAsStream(imagePath);
            
            if (imageStream == null) {
                int randomIndex = random.nextInt(5) + 1;
                imagePath = "books/" + folderName + "/rnd_0" + randomIndex + ".jpg";
                imageStream = getClass().getClassLoader().getResourceAsStream(imagePath);
            }
            
            if (imageStream != null) {
                BufferedImage originalImage = ImageIO.read(imageStream);
                imageStream.close();
                
                int targetWidth = UIConstants.ILLUSTRATION_WIDTH;
                int targetHeight = (int) (originalImage.getHeight() * ((double) targetWidth / originalImage.getWidth()));
                
                BufferedImage transparentImage = new BufferedImage(
                    targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = transparentImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
                g2d.dispose();
                
                processTransparency(transparentImage, targetWidth, targetHeight);
                
                return transparentImage;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private void processTransparency(BufferedImage image, int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                int brightness = (r + g + b) / 3;
                int alpha = 255 - brightness;
                
                if (brightness < 128) {
                    alpha = (int) (alpha * 1.6);
                    if (alpha > 255) alpha = 255;
                } else {
                    alpha = (int) (alpha * 0.3);
                }
                
                int newRgb = (alpha << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, newRgb);
            }
        }
    }
}
