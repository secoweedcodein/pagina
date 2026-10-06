package com.app.webia.dto.response;

public class IAResponse {
    private String respuesta;
    private String modelo;
    private Integer tokensEntrada;
    private Integer tokensSalida;

    public IAResponse() {}
    public IAResponse(String respuesta, String modelo) {
        this.respuesta = respuesta;
        this.modelo = modelo;
    }

    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public Integer getTokensEntrada() { return tokensEntrada; }
    public void setTokensEntrada(Integer tokensEntrada) { this.tokensEntrada = tokensEntrada; }
    public Integer getTokensSalida() { return tokensSalida; }
    public void setTokensSalida(Integer tokensSalida) { this.tokensSalida = tokensSalida; }
}
