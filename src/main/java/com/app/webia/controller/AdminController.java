package com.app.webia.controller;

import com.app.webia.entity.Categoria;
import com.app.webia.repository.InteraccionIARepository;
import com.app.webia.repository.ProductoRepository;
import com.app.webia.repository.UserRepository;
import com.app.webia.service.CategoriaService;
import com.app.webia.service.LogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserRepository userRepository;
    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;
    private final LogService logService;
    private final InteraccionIARepository interaccionIARepository;

    public AdminController(UserRepository userRepository,
                           ProductoRepository productoRepository,
                           CategoriaService categoriaService,
                           LogService logService,
                           InteraccionIARepository interaccionIARepository) {
        this.userRepository = userRepository;
        this.productoRepository = productoRepository;
        this.categoriaService = categoriaService;
        this.logService = logService;
        this.interaccionIARepository = interaccionIARepository;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalUsuarios", userRepository.count());
        model.addAttribute("totalProductos", productoRepository.count());
        var logs = logService.ultimos100().stream().limit(20).toList();
        model.addAttribute("totalLogs", interaccionIARepository.count());
        model.addAttribute("logs", logs);
        return "admin/dashboard";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", userRepository.findAll());
        return "admin/usuarios";
    }

    @GetMapping("/categorias")
    public String categorias() {
        return "redirect:/admin/categorias/lista";
    }

    @GetMapping("/categorias/lista")
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", categoriaService.listarActivas());
        return "admin/categorias";
    }

    @PostMapping("/categorias")
    public String crearCategoria(@RequestParam String nombre,
                                 @RequestParam(required = false) String descripcion,
                                 RedirectAttributes redirectAttributes) {
        try {
            Categoria categoria = new Categoria();
            categoria.setNombre(nombre.trim());
            categoria.setDescripcion(descripcion == null ? null : descripcion.trim());
            categoria.setActivo(true);
            categoriaService.guardar(categoria);
            redirectAttributes.addFlashAttribute("success", "Categoría creada correctamente");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", "No se pudo crear la categoría. Comprueba que el nombre no esté repetido.");
        }
        return "redirect:/admin/categorias/lista";
    }

    @PostMapping("/categorias/{id}/desactivar")
    public String desactivarCategoria(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            Categoria categoria = categoriaService.obtenerPorId(id);
            categoria.setActivo(false);
            categoriaService.guardar(categoria);
            redirectAttributes.addFlashAttribute("success", "Categoría desactivada");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", "No se pudo desactivar la categoría");
        }
        return "redirect:/admin/categorias/lista";
    }

    @GetMapping("/media")
    public String media() {
        return "admin/media";
    }

    @GetMapping("/logs-ia")
    public String logsIa() {
        return "redirect:/admin/logs-ia/lista";
    }

    @GetMapping("/logs-ia/lista")
    public String listarLogsIa(Model model) {
        model.addAttribute("interacciones", interaccionIARepository.findRecentWithUser(PageRequest.of(0, 100)));
        return "admin/logs-ia";
    }
}
