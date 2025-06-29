package com.PFM.CD.service;

/**
 * @Author: 马xs
 * @CreateTime: 2025-06-29
 * @Description:
 * @Version: 17.0
 */


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.json.JSONArray;

public class OpenRouterService {
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private final String apiKey;

    public OpenRouterService(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAIResponse(String userMessage) throws Exception {
        // Create connection
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setRequestProperty("HTTP-Referer", "http://localhost:8088");
        connection.setRequestProperty("X-Title", "Java Swing AI Assistant");
        connection.setDoOutput(true);

        // Prepare JSON payload
        JSONObject payload = new JSONObject();
        payload.put("model", "deepseek/deepseek-r1-distill-llama-70b:free");

        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", userMessage);
        messages.put(message);

        payload.put("messages", messages);

        // Send request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Read response
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        // Parse JSON response
        JSONObject jsonResponse = new JSONObject(response.toString());
        String aiResponseText = jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        return aiResponseText;
    }
}