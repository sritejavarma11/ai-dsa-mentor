package com.dsahint.backend.dto;

public class SubmitResponse {

    private boolean correct;
    private String message;

    public SubmitResponse() {
    }

    public SubmitResponse(boolean correct, String message) {
        this.correct = correct;
        this.message = message;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
