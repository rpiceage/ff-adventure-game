package com.adventure;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdventureLog {
    private List<String> entries;
    private DateTimeFormatter timeFormatter;
    
    public AdventureLog() {
        this.entries = new ArrayList<>();
        this.timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    }
    
    public void log(String message) {
        String timestamp = LocalDateTime.now().format(timeFormatter);
        entries.add("[" + timestamp + "] " + message);
    }
    
    public List<String> getEntries() {
        return new ArrayList<>(entries);
    }
    
    public String getFullLog() {
        return String.join("\n", entries);
    }
    
    public void clear() {
        entries.clear();
    }
    
    public void restoreEntries(List<String> savedEntries) {
        entries.clear();
        if (savedEntries != null) {
            entries.addAll(savedEntries);
        }
    }
}
