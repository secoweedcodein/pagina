package com.app.webia.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class IAChatRequest {
    @NotBlank
    @Size(max = 4000)
    private String mensaje;

    private String contexto;

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getContexto() { return contexto; }
    public void setContexto(String contexto) { this.contexto = contexto; }
}
