package com.dsahint.backend.controller;

import com.dsahint.backend.dto.HintRequest;
import com.dsahint.backend.dto.HintResponse;
import com.dsahint.backend.service.GeminiClient;
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
public class HintController {

    private final GeminiClient geminiClient;

    public HintController(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    @PostMapping("/get-hint")
    public ResponseEntity<HintResponse> getHint(@Valid @RequestBody HintRequest request) {
        try {
            String prompt = buildPrompt(request);
            String responseBody = geminiClient.generateContent(prompt);
            String hintText = geminiClient.extractCandidateText(responseBody);
            if (hintText.isBlank()) {
                return ResponseEntity.ok(new HintResponse(
                        "I could not generate a hint right now. Try refining your code or problem statement."));
            }
            return ResponseEntity.ok(new HintResponse(hintText));
        } catch (ResponseStatusException e) {
            return ResponseEntity.ok(new HintResponse(buildFallbackHint(request, e.getReason())));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.ok(new HintResponse(buildFallbackHint(request, "Gemini request interrupted")));
        } catch (IOException e) {
            return ResponseEntity.ok(new HintResponse(buildFallbackHint(request, "Failed to generate hint from Gemini")));
        }
    }

    private String buildPrompt(HintRequest request) {
        return "You are a DSA mentor. Give a concise hint (not full solution) to help solve the problem. "
                + "Focus on approach and edge cases.\n\n"
                + "Problem Statement:\n" + request.getProblemStatement() + "\n\n"
                + "User Code:\n" + request.getUserCode();
    }

    private String buildFallbackHint(HintRequest request, String reason) {
        String conciseReason = summarizeReason(reason);
        String problem = request.getProblemStatement() == null ? "" : request.getProblemStatement().toLowerCase();
        String code = request.getUserCode() == null ? "" : request.getUserCode().toLowerCase();

        if (problem.contains("two numbers") && problem.contains("target")) {
            return "AI service is temporarily unavailable (" + conciseReason + ").\n\n"
                    + "Hint: Try a single-pass HashMap approach. As you iterate, compute `complement = target - nums[i]`. "
                    + "If complement already exists in the map, you found the pair. Otherwise, store the current number "
                    + "with its index and continue.";
        }

        int forLoops = code.split("for", -1).length - 1;
        if (forLoops >= 2) {
            return "AI service is temporarily unavailable (" + conciseReason + ").\n\n"
                    + "Hint: Your current solution may be using nested loops. See if you can trade extra space for speed "
                    + "with a HashMap/Set so the lookup for what you need becomes O(1).";
        }

        return "AI service is temporarily unavailable (" + conciseReason + ").\n\n"
                + "Hint: Break the problem into: input/output shape, brute-force idea, and one data structure that can "
                + "avoid repeated scans.";
    }

    private String summarizeReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return "provider request failed";
        }

        String lowerReason = reason.toLowerCase();
        if (lowerReason.contains("quota") || lowerReason.contains("resource_exhausted")) {
            return "Gemini API quota exceeded";
        }
        if (lowerReason.contains("invalid_argument")) {
            return "invalid request to Gemini API";
        }
        if (lowerReason.contains("not_found")) {
            return "Gemini model not found";
        }

        int maxLen = 80;
        String singleLine = reason.replaceAll("\\s+", " ").trim();
        if (singleLine.length() > maxLen) {
            return singleLine.substring(0, maxLen) + "...";
        }
        return singleLine;
    }
}
