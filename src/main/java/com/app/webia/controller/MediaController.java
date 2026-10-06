package com.app.webia.controller;

import com.app.webia.security.CustomUserDetails;
import com.app.webia.service.SupabaseStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
public class MediaController {
    private final SupabaseStorageService storageService;

    @Value("${SUPABASE_BUCKET_MEDIA:media}")
    private String bucketMedia;

    public MediaController(SupabaseStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Archivo vacío"));
            }
            String path = "media/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
            String url = storageService.subirArchivo(file, bucketMedia, path);
            Map<String, Object> body = new HashMap<>();
            body.put("url", url);
            body.put("nombre", file.getOriginalFilename());
            body.put("tipo", file.getContentType());
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
