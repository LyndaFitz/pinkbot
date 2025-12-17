package com.pinkbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public String reply(String userMessage) {
        try {
            String requestBody = """
            {
              "model": "%s",
              "input": [
                {
                  "role": "system",
                  "content": "You are PinkBot: a friendly, upbeat assistant with a hot-pink vibe. Keep replies concise and helpful."
                },
                {
                  "role": "user",
                  "content": %s
                }
              ]
            }
            """.formatted(
                    model,
                    mapper.writeValueAsString(userMessage)
            );

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/responses"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            // If OpenAI returns an error status, show a helpful message
            if (res.statusCode() < 200 || res.statusCode() >= 300) {
                return "PinkBot error: OpenAI returned HTTP " + res.statusCode() + " → " + res.body();
            }

            JsonNode json = mapper.readTree(res.body());

            // 1) Best case: aggregated output text
            JsonNode outputText = json.get("output_text");
            if (outputText != null && !outputText.isNull() && !outputText.asText().isBlank()) {
                return outputText.asText();
            }

            // 2) Fallback: dig into output[0].content[0].text
            JsonNode output = json.path("output");
            if (output.isArray() && output.size() > 0) {
                JsonNode content = output.get(0).path("content");
                if (content.isArray() && content.size() > 0) {
                    JsonNode textNode = content.get(0).path("text");
                    if (!textNode.isMissingNode() && !textNode.isNull() && !textNode.asText().isBlank()) {
                        return textNode.asText();
                    }
                }
            }

            // 3) Last resort: show the raw response so we can adjust parsing if needed
            return "PinkBot is awake, but I couldn't read the AI reply yet 💗 (raw: " + res.body() + ")";

        } catch (Exception e) {
            return "PinkBot error: " + e.getMessage();
        }
    }
}
