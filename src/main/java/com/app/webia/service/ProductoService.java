package com.app.webia.service;

import com.app.webia.dto.request.ProductoRequest;
import com.app.webia.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductoService {
    List<Producto> destacados();
    List<Producto> ultimos();
    Page<Producto> listarActivos(Pageable pageable, String q, UUID categoriaId, BigDecimal precioMin, BigDecimal precioMax);
    Producto obtenerPorId(UUID id);
    Producto crear(ProductoRequest request, UUID vendedorId, String imagenPrincipalUrl);
    void eliminarLogico(UUID id);
}
