package com.app.webia.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class IAGenerarRequest {
    @NotBlank
    private String tipo; // texto | imagen

    @NotBlank
    @Size(max = 4000)
    private String prompt;

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
}
