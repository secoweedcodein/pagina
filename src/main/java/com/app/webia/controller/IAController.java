package com.app.webia.controller;

import com.app.webia.entity.User;
import com.app.webia.security.CustomUserDetails;
import com.app.webia.service.IAService;
import com.app.webia.dto.request.IAChatRequest;
import com.app.webia.dto.request.IAGenerarRequest;
import com.app.webia.dto.response.IAResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ia")
public class IAController {
    private final IAService iaService;

    public IAController(IAService iaService) {
        this.iaService = iaService;
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@Valid @RequestBody IAChatRequest request,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        User usuario = userDetails != null ? userDetails.getUser() : null;
        IAResponse res = iaService.chat(request, usuario);
        Map<String, Object> body = new HashMap<>();
        body.put("respuesta", res.getRespuesta());
        body.put("modelo", res.getModelo());
        body.put("habilitado", iaService.isHabilitado());
        return ResponseEntity.ok(body);
    }

    @PostMapping("/generar")
    public ResponseEntity<?> generar(@Valid @RequestBody IAGenerarRequest request,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        User usuario = userDetails != null ? userDetails.getUser() : null;
        IAResponse res = iaService.generar(request, usuario);
        Map<String, Object> body = new HashMap<>();
        body.put("respuesta", res.getRespuesta());
        body.put("modelo", res.getModelo());
        body.put("tipo", request.getTipo());
        body.put("habilitado", iaService.isHabilitado());
        return ResponseEntity.ok(body);
    }

    @PostMapping("/clasificar")
    public ResponseEntity<?> clasificar(@RequestParam("file") MultipartFile file,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        User usuario = userDetails != null ? userDetails.getUser() : null;
        IAResponse res = iaService.clasificarMedia(file, usuario);
        Map<String, Object> body = new HashMap<>();
        body.put("respuesta", res.getRespuesta());
        body.put("habilitado", iaService.isHabilitado());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/estado")
    public ResponseEntity<?> estado() {
        Map<String, Object> body = new HashMap<>();
        body.put("habilitado", iaService.isHabilitado());
        return ResponseEntity.ok(body);
    }
}
