package com.app.webia.entity;

import com.app.webia.enums.EstadoIA;
import com.app.webia.enums.TipoInteraccion;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "interacciones_ia")
public class InteraccionIA {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_interaccion", nullable = false)
    private TipoInteraccion tipoInteraccion = TipoInteraccion.CHAT_ASISTENTE;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String prompt;

    @Column(columnDefinition = "TEXT")
    private String respuesta;

    @Column(name = "modelo_ia", length = 120)
    private String modeloIa;

    @Column(name = "tokens_entrada")
    private Integer tokensEntrada;

    @Column(name = "tokens_salida")
    private Integer tokensSalida;

    @Column(name = "costo_estimado", precision = 10, scale = 6)
    private BigDecimal costoEstimado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoIA estado = EstadoIA.COMPLETADO;

    @Column(columnDefinition = "JSONB")
    private String metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }
    public TipoInteraccion getTipoInteraccion() { return tipoInteraccion; }
    public void setTipoInteraccion(TipoInteraccion tipoInteraccion) { this.tipoInteraccion = tipoInteraccion; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }
    public String getModeloIa() { return modeloIa; }
    public void setModeloIa(String modeloIa) { this.modeloIa = modeloIa; }
    public Integer getTokensEntrada() { return tokensEntrada; }
    public void setTokensEntrada(Integer tokensEntrada) { this.tokensEntrada = tokensEntrada; }
    public Integer getTokensSalida() { return tokensSalida; }
    public void setTokensSalida(Integer tokensSalida) { this.tokensSalida = tokensSalida; }
    public BigDecimal getCostoEstimado() { return costoEstimado; }
    public void setCostoEstimado(BigDecimal costoEstimado) { this.costoEstimado = costoEstimado; }
    public EstadoIA getEstado() { return estado; }
    public void setEstado(EstadoIA estado) { this.estado = estado; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
