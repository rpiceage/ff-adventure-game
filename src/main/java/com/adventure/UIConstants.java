package com.adventure;

import java.awt.*;

public class UIConstants {
    // Window dimensions
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 800;
    
    // Panel dimensions
    public static final int STATS_PANEL_WIDTH = 300;
    public static final int BUTTON_PANEL_HEIGHT = 80;
    public static final int ILLUSTRATION_WIDTH = 300;
    public static final int TEXT_RIGHT_MARGIN = 320; // ILLUSTRATION_WIDTH + 20
    
    // Font sizes
    public static final int FONT_SIZE_TITLE = 24;
    public static final int FONT_SIZE_LARGE = 20;
    public static final int FONT_SIZE_MEDIUM = 18;
    public static final int FONT_SIZE_SMALL = 16;
    
    // Fonts
    public static final Font FONT_TITLE = new Font("Arial", Font.BOLD, FONT_SIZE_TITLE);
    public static final Font FONT_LARGE = new Font("Arial", Font.BOLD, FONT_SIZE_LARGE);
    public static final Font FONT_MEDIUM = new Font("Arial", Font.BOLD, FONT_SIZE_MEDIUM);
    public static final Font FONT_SMALL = new Font("Arial", Font.BOLD, FONT_SIZE_SMALL);
    
    // Timing
    public static final int NOTIFICATION_DURATION_MS = 3000;
    public static final int ANIMATION_STEP_MS = 50;
    
    // Colors
    public static final Color SEMI_TRANSPARENT_BLACK = new Color(0, 0, 0, 100);
    public static final Color NOTIFICATION_BG = new Color(255, 255, 200);
    public static final Color BUTTON_GREEN = new Color(0, 100, 0);
    
    // Margins
    public static final Insets TEXT_MARGINS = new Insets(10, 10, 10, TEXT_RIGHT_MARGIN);
    
    private UIConstants() {
        // Prevent instantiation
    }
}
