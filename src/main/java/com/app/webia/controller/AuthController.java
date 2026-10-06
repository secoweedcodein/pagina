package com.app.webia.controller;

import com.app.webia.dto.request.AuthRegisterRequest;
import com.app.webia.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registro", new AuthRegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registrar(@Valid @ModelAttribute("registro") AuthRegisterRequest request,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirm", "Las contraseñas no coinciden");
            return "auth/register";
        }
        try {
            authService.registrar(request);
            redirectAttributes.addFlashAttribute("success", "Registro exitoso. Inicia sesión.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            result.reject("error.general", e.getMessage());
            return "auth/register";
        }
    }
}
