package com.dsahint.backend.controller;

import com.dsahint.backend.dto.HintRequest;
import com.dsahint.backend.dto.SubmitResponse;
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
public class SubmitController {

    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    public SubmitController(GeminiClient geminiClient, ObjectMapper objectMapper) {
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/submit")
    public ResponseEntity<SubmitResponse> submit(@Valid @RequestBody HintRequest request) {
        try {
            String prompt = buildCheckPrompt(request);
            String responseBody = geminiClient.generateContent(prompt);
            String text = geminiClient.extractCandidateText(responseBody);
            if (text.isBlank()) {
                return ResponseEntity.ok(heuristicCheck(request));
            }
            SubmitResponse parsed = parseSubmitJson(text);
            if (parsed != null) {
                return ResponseEntity.ok(parsed);
            }
            return ResponseEntity.ok(heuristicCheck(request));
        } catch (ResponseStatusException e) {
            return ResponseEntity.ok(buildUnavailableSubmit(e.getReason(), request));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.ok(buildUnavailableSubmit("Gemini request interrupted", request));
        } catch (IOException e) {
            return ResponseEntity.ok(buildUnavailableSubmit("Failed to reach Gemini", request));
        }
    }

    private String buildCheckPrompt(HintRequest request) {
        return "You are a strict code reviewer for coding interview problems. Given the problem statement and the user's code, "
                + "decide if the solution is algorithmically correct for all valid inputs (including edge cases). "
                + "Respond with ONLY a single JSON object, no markdown fences, no extra text, in this exact shape: "
                + "{\"correct\":true or false,\"message\":\"one short sentence\"}\n\n"
                + "Problem:\n" + request.getProblemStatement() + "\n\n"
                + "Code:\n" + request.getUserCode();
    }

    private SubmitResponse parseSubmitJson(String text) {
        String trimmed = text.trim();
        SubmitResponse direct = tryParseObject(trimmed);
        if (direct != null) {
            return direct;
        }
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return tryParseObject(trimmed.substring(start, end + 1));
        }
        return null;
    }

    private SubmitResponse tryParseObject(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            if (node.has("correct") && node.has("message")) {
                return new SubmitResponse(node.get("correct").asBoolean(), node.get("message").asText());
            }
        } catch (Exception ignored) {
            // try next strategy
        }
        return null;
    }

    private SubmitResponse buildUnavailableSubmit(String reason, HintRequest request) {
        SubmitResponse h = heuristicCheck(request);
        return new SubmitResponse(h.isCorrect(),
                "Automatic check unavailable (" + summarizeReason(reason) + "). " + h.getMessage());
    }

    private String summarizeReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return "provider error";
        }
        String lower = reason.toLowerCase();
        if (lower.contains("quota") || lower.contains("resource_exhausted")) {
            return "Gemini API quota exceeded";
        }
        return reason.replaceAll("\\s+", " ").trim();
    }

    /**
     * Lightweight fallback for Two Sum when Gemini is unavailable or response is not JSON.
     */
    private SubmitResponse heuristicCheck(HintRequest request) {
        String problem = request.getProblemStatement() == null ? "" : request.getProblemStatement().toLowerCase();
        String rawCode = request.getUserCode() == null ? "" : request.getUserCode();
        String code = rawCode.toLowerCase().replaceAll("\\s+", " ");

        boolean twoSum = problem.contains("two") && problem.contains("target")
                || problem.contains("add up to");

        if (!twoSum) {
            return new SubmitResponse(false, "Could not verify automatically for this problem. Use AI grading when Gemini is available.");
        }

        boolean hasComplement = code.contains("complement") && code.contains("containskey");
        if (!hasComplement) {
            return new SubmitResponse(false, "Could not verify this submission without running tests.");
        }

        int forCount = rawCode.split("for", -1).length - 1;

        boolean checksDistinctIndex = code.contains("map.get(complement)!=i")
                || code.contains("map.get(complement) != i")
                || code.contains("!= i")
                || code.contains("i !=");

        // Classic buggy pattern: two passes over nums + containsKey without ensuring different indices.
        if (forCount >= 2 && !checksDistinctIndex) {
            return new SubmitResponse(false,
                    "Likely incorrect: same element/index can satisfy complement lookup after you overwrite duplicates in the map. Ensure indices differ.");
        }

        // Typical correct one-pass: single loop, complement lookup against previously stored indices.
        if (forCount == 1) {
            return new SubmitResponse(true, "Looks like a standard one-pass hash-map Two Sum approach.");
        }

        if (forCount >= 2 && checksDistinctIndex) {
            return new SubmitResponse(true, "Two-pass approach with an explicit different-index check — likely correct.");
        }

        return new SubmitResponse(false, "Could not verify this submission without running tests.");
    }
}
