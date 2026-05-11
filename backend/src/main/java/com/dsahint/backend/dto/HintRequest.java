package com.dsahint.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class HintRequest {

    @NotBlank(message = "problemStatement is required")
    private String problemStatement;

    @NotBlank(message = "userCode is required")
    private String userCode;

    public String getProblemStatement() {
        return problemStatement;
    }

    public void setProblemStatement(String problemStatement) {
        this.problemStatement = problemStatement;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
