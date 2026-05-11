package com.dsahint.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class DiscoverRequest {

    @NotBlank(message = "query is required")
    private String query;

    @NotBlank(message = "language is required")
    private String language;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}

