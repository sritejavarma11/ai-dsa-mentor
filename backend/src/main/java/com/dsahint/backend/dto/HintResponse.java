package com.dsahint.backend.dto;

public class HintResponse {

    private String hint;

    public HintResponse() {
    }

    public HintResponse(String hint) {
        this.hint = hint;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
