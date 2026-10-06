package com.app.webia.controller;

import com.app.webia.dto.request.ProductoRequest;
import com.app.webia.security.CustomUserDetails;
import com.app.webia.service.CategoriaService;
import com.app.webia.service.ProductoService;
import com.app.webia.service.SupabaseStorageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/admin/productos")
public class ProductoController {
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final SupabaseStorageService storageService;

    @Value("${SUPABASE_BUCKET_PRODUCTOS:productos}")
    private String bucketProductos;

    public ProductoController(ProductoService productoService, CategoriaService categoriaService, SupabaseStorageService storageService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.storageService = storageService;
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new ProductoRequest());
        model.addAttribute("categorias", categoriaService.listarActivas());
        return "admin/producto-form";
    }

    @PostMapping("/nuevo")
    public String crear(@Valid @ModelAttribute("producto") ProductoRequest request,
                        BindingResult result,
                        @RequestParam(value = "imagen", required = false) MultipartFile imagen,
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarActivas());
            return "admin/producto-form";
        }
        try {
            String imagenUrl = null;
            if (imagen != null && !imagen.isEmpty()) {
                String path = "productos/" + UUID.randomUUID() + "-" + imagen.getOriginalFilename();
                imagenUrl = storageService.subirArchivo(imagen, bucketProductos, path);
            }
            productoService.crear(request, userDetails.getUser().getId(), imagenUrl);
            redirectAttributes.addFlashAttribute("success", "Producto creado correctamente");
            return "redirect:/admin/productos";
        } catch (Exception e) {
            result.reject("error", e.getMessage());
            model.addAttribute("categorias", categoriaService.listarActivas());
            return "admin/producto-form";
        }
    }

    @GetMapping
    public String listarAdmin(Model model) {
        model.addAttribute("productos", productoService.listarActivos(org.springframework.data.domain.Pageable.unpaged(), null, null, null, null));
        return "admin/productos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminarLogico(id);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/productos";
    }
}
