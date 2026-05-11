package com.dsahint.backend.dto;

public class DiscoverResponse {

    private String problemStatement;
    private String boilerplateCode;

    public DiscoverResponse() {
    }

    public DiscoverResponse(String problemStatement, String boilerplateCode) {
        this.problemStatement = problemStatement;
        this.boilerplateCode = boilerplateCode;
    }

    public String getProblemStatement() {
        return problemStatement;
    }

    public void setProblemStatement(String problemStatement) {
        this.problemStatement = problemStatement;
    }

    public String getBoilerplateCode() {
        return boilerplateCode;
    }

    public void setBoilerplateCode(String boilerplateCode) {
        this.boilerplateCode = boilerplateCode;
    }
}

