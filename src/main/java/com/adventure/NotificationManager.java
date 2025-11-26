package com.adventure;

import javax.swing.*;
import java.awt.*;

public class NotificationManager {
    private JWindow notificationWindow;
    private final JFrame parent;
    
    public NotificationManager(JFrame parent) {
        this.parent = parent;
    }
    
    public void show(String message) {
        if (notificationWindow != null) {
            notificationWindow.dispose();
        }
        
        notificationWindow = new JWindow(parent);
        JLabel label = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
        label.setFont(UIConstants.FONT_MEDIUM);
        label.setForeground(Color.BLACK);
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        label.setBackground(UIConstants.NOTIFICATION_BG);
        label.setOpaque(true);
        notificationWindow.add(label);
        notificationWindow.pack();
        
        Point location = parent.getLocation();
        Dimension size = parent.getSize();
        Dimension notifSize = notificationWindow.getSize();
        notificationWindow.setLocation(
            location.x + 10,
            location.y + size.height - notifSize.height - 50
        );
        
        notificationWindow.setVisible(true);
        
        Timer timer = new Timer(UIConstants.NOTIFICATION_DURATION_MS, e -> {
            notificationWindow.dispose();
            notificationWindow = null;
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    public void hide() {
        if (notificationWindow != null) {
            notificationWindow.dispose();
            notificationWindow = null;
        }
    }
}
