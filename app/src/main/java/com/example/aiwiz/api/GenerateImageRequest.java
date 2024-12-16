package com.example.aiwiz.api;

public class GenerateImageRequest {
    private String inputs;

    public GenerateImageRequest(String inputs) {
        this.inputs = inputs;
    }

    // Getter 및 Setter
    public String getInputs() {
        return inputs;
    }

    public void setInputs(String inputs) {
        this.inputs = inputs;
    }
}
