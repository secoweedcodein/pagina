package com.app.webia.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class SupabaseStorageService {
    private final WebClient webClient;
    private final String supabaseUrl;
    private final String serviceRoleKey;

    public SupabaseStorageService(WebClient.Builder webClientBuilder,
                                  @Value("${SUPABASE_URL:}") String supabaseUrl,
                                  @Value("${SUPABASE_SERVICE_ROLE_KEY:}") String serviceRoleKey) {
        this.webClient = webClientBuilder.build();
        this.supabaseUrl = supabaseUrl;
        this.serviceRoleKey = serviceRoleKey;
    }

    public String subirArchivo(MultipartFile file, String bucket, String path) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No se puede subir un archivo vacío");
        }
        if (supabaseUrl.isBlank() || serviceRoleKey.isBlank()) {
            throw new IllegalStateException("La configuración de Supabase Storage está incompleta");
        }
        if (bucket == null || bucket.isBlank() || path == null || path.isBlank()) {
            throw new IllegalArgumentException("El bucket y la ruta del archivo son obligatorios");
        }

        String encodedPath = encodePath(path);
        String contentType = file.getContentType() == null
                ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                : file.getContentType();
        webClient.post()
                .uri(supabaseUrl + "/storage/v1/object/" + encodePath(bucket) + "/" + encodedPath)
                .header("apikey", serviceRoleKey)
                .header("Authorization", "Bearer " + serviceRoleKey)
                .header("x-upsert", "true")
                .contentType(MediaType.parseMediaType(contentType))
                .bodyValue(file.getBytes())
                .retrieve()
                .toBodilessEntity()
                .block();

        return supabaseUrl + "/storage/v1/object/public/" + encodePath(bucket) + "/" + encodedPath;
    }

    private String encodePath(String path) {
        return java.util.Arrays.stream(path.split("/", -1))
                .map(segment -> URLEncoder.encode(segment, StandardCharsets.UTF_8).replace("+", "%20"))
                .collect(java.util.stream.Collectors.joining("/"));
    }
}
