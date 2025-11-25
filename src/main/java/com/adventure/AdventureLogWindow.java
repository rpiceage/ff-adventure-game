package com.adventure;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdventureLogWindow extends JFrame {
    private AdventureLog adventureLog;
    private JTextArea logTextArea;
    
    public AdventureLogWindow(AdventureLog adventureLog) {
        this.adventureLog = adventureLog;
        
        setTitle("Adventure Log");
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Create text area with parchment background
        try {
            InputStream bgStream = getClass().getClassLoader().getResourceAsStream("pergament.jpg");
            BufferedImage bgImage = ImageIO.read(bgStream);
            logTextArea = new JTextArea(adventureLog.getFullLog()) {
                @Override
                protected void paintComponent(Graphics g) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                    super.paintComponent(g);
                }
            };
            logTextArea.setOpaque(false);
        } catch (Exception e) {
            logTextArea = new JTextArea(adventureLog.getFullLog());
        }
        
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font("Arial", Font.BOLD, 24));
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);
        logTextArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create Export button
        JPanel buttonPanel = new JPanel();
        JButton exportButton = new JButton("Export");
        exportButton.addActionListener(e -> exportLog());
        buttonPanel.add(exportButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }
    
    private void exportLog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Adventure Log");
        
        // Default filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        fileChooser.setSelectedFile(new File("adventure_log_" + timestamp + ".txt"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(adventureLog.getFullLog());
                JOptionPane.showMessageDialog(this, 
                    "Adventure log exported successfully!", 
                    "Export Complete", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting log: " + ex.getMessage(), 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
