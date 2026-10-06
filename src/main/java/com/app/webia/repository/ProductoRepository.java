package com.app.webia.repository;

import com.app.webia.entity.Producto;
import com.app.webia.enums.EstadoProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductoRepository extends JpaRepository<Producto, UUID> {
    List<Producto> findTop6ByDeletedAtIsNullAndDestacadoTrueAndEstadoOrderByCreatedAtDesc(EstadoProducto estado);
    List<Producto> findTop8ByDeletedAtIsNullAndEstadoOrderByCreatedAtDesc(EstadoProducto estado);

    @Query("select p from Producto p join fetch p.categoria where p.id = :id")
    Optional<Producto> findWithCategoryById(@Param("id") UUID id);

    @Query(value = """
            select p from Producto p join fetch p.categoria
            where p.deletedAt is null
              and p.estado = com.app.webia.enums.EstadoProducto.ACTIVO
              and (:q is null or :q = ''
                   or lower(p.nombre) like lower(concat('%', :q, '%'))
                   or lower(p.descripcion) like lower(concat('%', :q, '%')))
              and (:categoriaId is null or p.categoria.id = :categoriaId)
              and (:precioMin is null or p.precio >= :precioMin)
              and (:precioMax is null or p.precio <= :precioMax)
            """,
            countQuery = """
                    select count(p) from Producto p
                    where p.deletedAt is null
                      and p.estado = com.app.webia.enums.EstadoProducto.ACTIVO
                      and (:q is null or :q = ''
                           or lower(p.nombre) like lower(concat('%', :q, '%'))
                           or lower(p.descripcion) like lower(concat('%', :q, '%')))
                      and (:categoriaId is null or p.categoria.id = :categoriaId)
                      and (:precioMin is null or p.precio >= :precioMin)
                      and (:precioMax is null or p.precio <= :precioMax)
                    """)
    Page<Producto> buscarActivos(@Param("q") String q,
                                 @Param("categoriaId") UUID categoriaId,
                                 @Param("precioMin") BigDecimal precioMin,
                                 @Param("precioMax") BigDecimal precioMax,
                                 Pageable pageable);
}
