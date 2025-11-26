package com.adventure;

import javax.swing.*;

public class AnimationHelper {
    private static final float[] LABEL_SIZES = {24f, 25f, 26f, 27f, 26f, 25f, 24f};
    private static final float[] BUTTON_SIZES = {18f, 19f, 20f, 21f, 20f, 19f, 18f};
    
    public static void animateLabel(JLabel label) {
        animate(label, LABEL_SIZES, 24f);
    }
    
    public static void animateButton(JButton button) {
        animate(button, BUTTON_SIZES, 18f);
    }
    
    private static void animate(JComponent component, float[] sizes, float finalSize) {
        Timer timer = new Timer(UIConstants.ANIMATION_STEP_MS, null);
        final int[] step = {0};
        timer.addActionListener(e -> {
            if (step[0] < sizes.length) {
                component.setFont(component.getFont().deriveFont(sizes[step[0]]));
                step[0]++;
            } else {
                component.setFont(component.getFont().deriveFont(finalSize));
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }
    
    private AnimationHelper() {
        // Prevent instantiation
    }
}
