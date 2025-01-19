package com.project.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The APIKeyManager class manages a list of API keys and ensures that the number of calls
 * to each key does not exceed a specified maximum. It rotates the keys when the limit is reached.
 */
public class APIKeyManager {
    private static final int MAX_CALLS_PER_KEY = 100; // Nombre maximum d'appels par clé
    private final List<String> apiKeys;
    private final AtomicInteger currentKeyIndex;
    private final Map<String, AtomicInteger> endpointCalls;

    /**
     * Manages API keys for accessing external services.
     * 
     * <p>This class maintains a list of API keys and provides functionality to
     * manage and rotate through them. It also keeps track of the number of calls
     * made to each endpoint.</p>
     * 
     * <p>Usage example:</p>
     * <pre>
     * {@code
     * APIKeyManager apiKeyManager = new APIKeyManager();
     * String apiKey = apiKeyManager.getCurrentKey();
     * }
     * </pre>
     * 
     * <p>Note: Ensure that the API keys are kept secure and not exposed in the code.</p>
     * 
     * <p>Constructor:</p>
     * <ul>
     *   <li>Initializes the list of API keys.</li>
     *   <li>Sets the current key index to 0.</li>
     *   <li>Initializes the endpoint calls map.</li>
     * </ul>
     */
    public APIKeyManager() {
        this.apiKeys = new ArrayList<>();
        apiKeys.add("e22bd0a8fe36c642d47999f6e61f8e252a717ec7");
        apiKeys.add("fb510a6ba1c22a8cf5dc10783ad1b61e0d2c323c");
        apiKeys.add("2dcd105b6422271e9abc8d5cfd046559925495ed");
        this.currentKeyIndex = new AtomicInteger(0);
        this.endpointCalls = new HashMap<>();
    }

    public synchronized String getCurrentKey(String endpoint) {
        endpointCalls.putIfAbsent(endpoint, new AtomicInteger(0));
        int calls = endpointCalls.get(endpoint).incrementAndGet();
        if (calls >= MAX_CALLS_PER_KEY) {
            rotateKey(endpoint);
        }
        return apiKeys.get(currentKeyIndex.get());
    }

    private void rotateKey(String endpoint) {
        endpointCalls.get(endpoint).set(0);
        int nextIndex = (currentKeyIndex.get() + 1) % apiKeys.size();
        currentKeyIndex.set(nextIndex);
    }
    public synchronized void forceKeyRotation(String endpoint) {
        rotateKey(endpoint);
    }
}