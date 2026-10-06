package com.app.webia.controller;

import com.app.webia.security.CustomUserDetails;
import com.app.webia.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PerfilController {
    private final com.app.webia.service.SupabaseStorageService storageService;
    private final com.app.webia.repository.UserRepository userRepository;

    @org.springframework.beans.factory.annotation.Value("${SUPABASE_BUCKET_USUARIOS:usuarios}")
    private String bucketUsuarios;

    public PerfilController(com.app.webia.service.SupabaseStorageService storageService,
                            com.app.webia.repository.UserRepository userRepository) {
        this.storageService = storageService;
        this.userRepository = userRepository;
    }

    @GetMapping("/perfil")
    public String perfil(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("usuario", userDetails.getUser());
        return "perfil/perfil";
    }

    @PostMapping("/perfil/avatar")
    public String cambiarAvatar(@RequestParam("avatar") MultipartFile avatar,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            if (avatar != null && !avatar.isEmpty()) {
                User user = userDetails.getUser();
                String path = "usuarios/" + user.getId() + "/" + java.util.UUID.randomUUID() + "-" + avatar.getOriginalFilename();
                String url = storageService.subirArchivo(avatar, bucketUsuarios, path);
                user.setAvatarUrl(url);
                userRepository.save(user);
                redirectAttributes.addFlashAttribute("success", "Avatar actualizado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/perfil";
    }
}
