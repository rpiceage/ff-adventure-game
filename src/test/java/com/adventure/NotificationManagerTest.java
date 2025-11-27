package com.adventure;

import org.junit.jupiter.api.Test;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

public class NotificationManagerTest {
    
    @Test
    public void testShowCreatesWindow() {
        JFrame parent = new JFrame();
        parent.setSize(800, 600);
        parent.setLocation(100, 100);
        
        NotificationManager manager = new NotificationManager(parent);
        manager.show("Test message");
        
        manager.hide();
        parent.dispose();
    }
    
    @Test
    public void testHideDisposesWindow() {
        JFrame parent = new JFrame();
        NotificationManager manager = new NotificationManager(parent);
        
        manager.show("Test");
        manager.hide();
        manager.hide(); // Should not throw
        
        parent.dispose();
    }
    
    @Test
    public void testMultipleShowsDisposesPrevious() {
        JFrame parent = new JFrame();
        NotificationManager manager = new NotificationManager(parent);
        
        manager.show("First");
        manager.show("Second");
        manager.hide();
        
        parent.dispose();
    }
}
