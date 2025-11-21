package com.adventure;

import java.util.List;
import java.util.Map;

public class Adventure {
    public String title;
    public List<Chapter> chapters;

    public static class Chapter {
        public int index;
        public List<Map<String, Object>> actions;
    }
}
