package com.app.webia.service.impl;

import com.app.webia.dto.request.ProductoRequest;
import com.app.webia.entity.Categoria;
import com.app.webia.entity.Producto;
import com.app.webia.entity.User;
import com.app.webia.enums.EstadoProducto;
import com.app.webia.repository.CategoriaRepository;
import com.app.webia.repository.ProductoRepository;
import com.app.webia.repository.UserRepository;
import com.app.webia.service.ProductoService;
import com.app.webia.util.SlugUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductoServiceImpl implements ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UserRepository userRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository, CategoriaRepository categoriaRepository, UserRepository userRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Producto> destacados() {
        return productoRepository.findTop6ByDeletedAtIsNullAndDestacadoTrueAndEstadoOrderByCreatedAtDesc(EstadoProducto.ACTIVO);
    }

    @Override
    public List<Producto> ultimos() {
        return productoRepository.findTop8ByDeletedAtIsNullAndEstadoOrderByCreatedAtDesc(EstadoProducto.ACTIVO);
    }

    @Override
    public Page<Producto> listarActivos(Pageable pageable, String q, UUID categoriaId, BigDecimal precioMin, BigDecimal precioMax) {
        return productoRepository.buscarActivos(q, categoriaId, precioMin, precioMax, pageable);
    }

    @Override
    public Producto obtenerPorId(UUID id) {
        return productoRepository.findWithCategoryById(id).orElseThrow();
    }

    @Override
    @Transactional
    public Producto crear(ProductoRequest request, UUID vendedorId, String imagenPrincipalUrl) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId()).orElseThrow();
        User vendedor = userRepository.findById(vendedorId).orElseThrow();

        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setSlug(SlugUtil.slugify(request.getNombre()));
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setDestacado(request.isDestacado());
        producto.setEstado(EstadoProducto.ACTIVO);
        producto.setCategoria(categoria);
        producto.setVendedor(vendedor);
        producto.setImagenPrincipalUrl(imagenPrincipalUrl);
        producto.setGaleria("[]");

        return productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void eliminarLogico(UUID id) {
        Producto producto = productoRepository.findById(id).orElseThrow();
        producto.setDeletedAt(OffsetDateTime.now());
        producto.setEstado(EstadoProducto.INACTIVO);
        productoRepository.save(producto);
    }
}
