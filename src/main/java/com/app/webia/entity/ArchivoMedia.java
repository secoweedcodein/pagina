package com.app.webia.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "archivos_media")
public class ArchivoMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nombre_original", nullable = false)
    private String nombreOriginal;

    @Column(name = "nombre_storage", nullable = false)
    private String nombreStorage;

    @Column(name = "ruta_storage", nullable = false, length = 500)
    private String rutaStorage;

    @Column(nullable = false, length = 50)
    private String bucket;

    @Column(name = "tipo_mime", nullable = false, length = 120)
    private String tipoMime;

    @Column(name = "tamaño_bytes", nullable = false)
    private Long tamañoBytes;

    @Column(name = "entidad_tipo", length = 50)
    private String entidadTipo;

    @Column(name = "entidad_id")
    private UUID entidadId;

    @Column(nullable = false)
    private boolean publico = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNombreOriginal() { return nombreOriginal; }
    public void setNombreOriginal(String nombreOriginal) { this.nombreOriginal = nombreOriginal; }
    public String getNombreStorage() { return nombreStorage; }
    public void setNombreStorage(String nombreStorage) { this.nombreStorage = nombreStorage; }
    public String getRutaStorage() { return rutaStorage; }
    public void setRutaStorage(String rutaStorage) { this.rutaStorage = rutaStorage; }
    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }
    public String getTipoMime() { return tipoMime; }
    public void setTipoMime(String tipoMime) { this.tipoMime = tipoMime; }
    public Long getTamañoBytes() { return tamañoBytes; }
    public void setTamañoBytes(Long tamañoBytes) { this.tamañoBytes = tamañoBytes; }
    public String getEntidadTipo() { return entidadTipo; }
    public void setEntidadTipo(String entidadTipo) { this.entidadTipo = entidadTipo; }
    public UUID getEntidadId() { return entidadId; }
    public void setEntidadId(UUID entidadId) { this.entidadId = entidadId; }
    public boolean isPublico() { return publico; }
    public void setPublico(boolean publico) { this.publico = publico; }
    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
