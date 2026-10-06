package com.app.webia.controller;

import com.app.webia.service.CategoriaService;
import com.app.webia.service.ProductoService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.UUID;

@Controller
public class CatalogoController {
    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public CatalogoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/catalogo")
    public String catalogo(Model model,
                           @PageableDefault(size = 12) Pageable pageable,
                           @RequestParam(required = false) String q,
                           @RequestParam(required = false) UUID categoriaId,
                           @RequestParam(required = false) BigDecimal precioMin,
                           @RequestParam(required = false) BigDecimal precioMax) {
        model.addAttribute("productos", productoService.listarActivos(pageable, q, categoriaId, precioMin, precioMax));
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("q", q);
        model.addAttribute("categoriaId", categoriaId);
        model.addAttribute("precioMin", precioMin);
        model.addAttribute("precioMax", precioMax);
        return "catalogo/list";
    }

    @GetMapping("/producto/{id}")
    public String detalle(@PathVariable UUID id, Model model) {
        model.addAttribute("producto", productoService.obtenerPorId(id));
        return "catalogo/detalle";
    }
}
