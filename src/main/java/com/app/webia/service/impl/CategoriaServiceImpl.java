package com.app.webia.service.impl;

import com.app.webia.entity.Categoria;
import com.app.webia.repository.CategoriaRepository;
import com.app.webia.service.CategoriaService;
import com.app.webia.util.SlugUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoriaServiceImpl implements CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<Categoria> listarActivas() {
        return categoriaRepository.findByActivoTrue();
    }

    @Override
    public Categoria obtenerPorId(UUID id) {
        return categoriaRepository.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public Categoria guardar(Categoria categoria) {
        if (categoria.getSlug() == null || categoria.getSlug().isBlank()) {
            categoria.setSlug(SlugUtil.slugify(categoria.getNombre()));
        }
        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public void eliminar(UUID id) {
        categoriaRepository.deleteById(id);
    }
}
