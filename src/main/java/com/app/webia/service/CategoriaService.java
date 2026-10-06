package com.app.webia.service;

import com.app.webia.entity.Categoria;
import java.util.List;
import java.util.UUID;

public interface CategoriaService {
    List<Categoria> listarActivas();
    Categoria obtenerPorId(UUID id);
    Categoria guardar(Categoria categoria);
    void eliminar(UUID id);
}
