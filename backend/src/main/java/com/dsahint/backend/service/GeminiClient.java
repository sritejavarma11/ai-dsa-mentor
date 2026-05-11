package com.dsahint.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class GeminiClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.model}")
    private String geminiModel;

    public GeminiClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    public String generateContent(String prompt) throws IOException, InterruptedException {
        String baseUrl = geminiApiUrl.endsWith("/")
                ? geminiApiUrl.substring(0, geminiApiUrl.length() - 1)
                : geminiApiUrl;
        String endpoint = String.format("%s/models/%s:generateContent?key=%s", baseUrl, geminiModel, geminiApiKey);

        ObjectNode root = objectMapper.createObjectNode();
        ObjectNode part = root.putArray("contents")
                .addObject()
                .putArray("parts")
                .addObject();
        part.put("text", prompt);
        String requestBody = root.toString();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Gemini API error: " + response.statusCode() + " - " + response.body());
        }

        return response.body();
    }

    public String extractCandidateText(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode textNode = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");

        if (textNode.isMissingNode() || textNode.asText().isBlank()) {
            return "";
        }

        return textNode.asText();
    }
}
