package com.dsahint.backend.controller;

import com.dsahint.backend.dto.DiscoverRequest;
import com.dsahint.backend.dto.DiscoverResponse;
import com.dsahint.backend.service.BoilerplateService;
import com.dsahint.backend.service.GeminiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class DiscoverController {

    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;
    private final BoilerplateService boilerplateService;

    public DiscoverController(GeminiClient geminiClient, ObjectMapper objectMapper, BoilerplateService boilerplateService) {
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
        this.boilerplateService = boilerplateService;
    }

    @PostMapping("/discover")
    public ResponseEntity<DiscoverResponse> discover(@Valid @RequestBody DiscoverRequest request) {
        String language = boilerplateService.normalizeLanguage(request.getLanguage());
        String query = request.getQuery();

        try {
            String prompt = buildDiscoverPrompt(query, language);
            String responseBody = geminiClient.generateContent(prompt);
            String text = geminiClient.extractCandidateText(responseBody);
            DiscoverResponse parsed = parseDiscoverJson(text);
            if (parsed != null && parsed.getProblemStatement() != null && !parsed.getProblemStatement().isBlank()) {
                if (parsed.getBoilerplateCode() == null || parsed.getBoilerplateCode().isBlank()) {
                    parsed.setBoilerplateCode(boilerplateService.boilerplateFor(language, parsed.getProblemStatement()));
                }
                return ResponseEntity.ok(parsed);
            }
        } catch (ResponseStatusException | InterruptedException | IOException ignored) {
            // fall back
        }

        String problemStatement = boilerplateService.fallbackProblemFor(query);
        String boilerplate = boilerplateService.boilerplateFor(language, problemStatement + "\n" + query);
        return ResponseEntity.ok(new DiscoverResponse(problemStatement, boilerplate));
    }

    private String buildDiscoverPrompt(String query, String language) {
        return "You generate coding interview problems and starter code.\n"
                + "User will provide a short query like 'leetcode 512' or 'problem on dfs'.\n"
                + "Return ONLY a single JSON object, no markdown, no extra text, in this exact shape:\n"
                + "{\"problemStatement\":\"...\",\"boilerplateCode\":\"...\"}\n\n"
                + "Rules:\n"
                + "- problemStatement must be clear and self-contained with constraints + at least 1 example.\n"
                + "- boilerplateCode must be minimal starter code in the requested language: " + language + ".\n"
                + "- Do NOT include a full solution.\n\n"
                + "Query:\n" + query;
    }

    private DiscoverResponse parseDiscoverJson(String text) {
        if (text == null) return null;
        String trimmed = text.trim();
        DiscoverResponse direct = tryParse(trimmed);
        if (direct != null) return direct;
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return tryParse(trimmed.substring(start, end + 1));
        }
        return null;
    }

    private DiscoverResponse tryParse(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            if (!node.has("problemStatement")) return null;
            String ps = node.get("problemStatement").asText("");
            String bp = node.has("boilerplateCode") ? node.get("boilerplateCode").asText("") : "";
            return new DiscoverResponse(ps, bp);
        } catch (Exception ignored) {
            return null;
        }
    }
}

