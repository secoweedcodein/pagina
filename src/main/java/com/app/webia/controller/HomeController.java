package com.app.webia.controller;

import com.app.webia.service.CategoriaService;
import com.app.webia.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public HomeController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("destacados", productoService.destacados());
        model.addAttribute("ultimos", productoService.ultimos());
        model.addAttribute("categorias", categoriaService.listarActivas());
        return "index";
    }
}
