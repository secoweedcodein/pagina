package com.app.webia.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public class ProductoRequest {
    @NotBlank
    @Size(max = 200)
    private String nombre;

    @Size(max = 5000)
    private String descripcion;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal precio;

    @NotNull
    @Min(0)
    private Integer stock;

    private boolean destacado;

    @NotNull
    private UUID categoriaId;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public boolean isDestacado() { return destacado; }
    public void setDestacado(boolean destacado) { this.destacado = destacado; }
    public UUID getCategoriaId() { return categoriaId; }
    public void setCategoriaId(UUID categoriaId) { this.categoriaId = categoriaId; }
}
