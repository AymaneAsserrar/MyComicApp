package com.project.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class APIKeyManager {
    private static final int MAX_CALLS_PER_KEY = 100; // Nombre maximum d'appels par clé
    private final List<String> apiKeys;
    private final AtomicInteger currentKeyIndex;
    private final AtomicInteger currentKeyCalls;

    public APIKeyManager() {
        this.apiKeys = new ArrayList<>();
        apiKeys.add("e22bd0a8fe36c642d47999f6e61f8e252a717ec7");
        apiKeys.add("fb510a6ba1c22a8cf5dc10783ad1b61e0d2c323c");
        apiKeys.add("2dcd105b6422271e9abc8d5cfd046559925495ed");
        this.currentKeyIndex = new AtomicInteger(0);
        this.currentKeyCalls = new AtomicInteger(0);
    }

    public synchronized String getCurrentKey() {
        int calls = currentKeyCalls.incrementAndGet();
        if (calls >= MAX_CALLS_PER_KEY) {
            rotateKey();
        }
        return apiKeys.get(currentKeyIndex.get());
    }

    private void rotateKey() {
        currentKeyCalls.set(0);
        int nextIndex = (currentKeyIndex.get() + 1) % apiKeys.size();
        currentKeyIndex.set(nextIndex);
    }
}