package com.project.api;

import java.util.LinkedHashMap;
import java.util.Map;


public class APICache {
    private static final int CACHE_SIZE = 10;
    private static final Map<String, String> cache = new LinkedHashMap<String, String>(CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > CACHE_SIZE;
        }
    };

    public static String get(String key) {
        return cache.get(key);
    }

    public static void put(String key, String value) {
        cache.put(key, value);
    }
    
}
