package com.app.webia.service;

import com.app.webia.dto.request.IAChatRequest;
import com.app.webia.dto.request.IAGenerarRequest;
import com.app.webia.dto.response.IAResponse;
import com.app.webia.entity.InteraccionIA;
import com.app.webia.entity.User;
import com.app.webia.enums.EstadoIA;
import com.app.webia.enums.TipoInteraccion;
import com.app.webia.repository.InteraccionIARepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class IAService {
    private final WebClient webClient;
    private final InteraccionIARepository interaccionIARepository;
    private final String apiKey;
    private final String baseUrl;
    private final String chatModel;
    private final String textModel;
    private final String imageModel;
    private final String mediaModel;
    private final String siteUrl;
    private final String appName;
    private final Duration timeout;

    public IAService(WebClient.Builder webClientBuilder,
                     InteraccionIARepository interaccionIARepository,
                     @Value("${OPENROUTER_API_KEY:}") String apiKey,
                     @Value("${OPENROUTER_BASE_URL:https://openrouter.ai/api/v1}") String baseUrl,
                     @Value("${OPENROUTER_CHAT_MODEL:anthropic/claude-3.5-haiku}") String chatModel,
                     @Value("${OPENROUTER_TEXT_MODEL:anthropic/claude-3.5-haiku}") String textModel,
                     @Value("${OPENROUTER_IMAGE_MODEL:google/gemini-2.5-flash-image-preview}") String imageModel,
                     @Value("${OPENROUTER_MEDIA_MODEL:anthropic/claude-3.5-haiku}") String mediaModel,
                     @Value("${OPENROUTER_SITE_URL:http://localhost:8080}") String siteUrl,
                     @Value("${OPENROUTER_APP_NAME:WebIA-Supabase}") String appName,
                     @Value("${IA_TIMEOUT_MS:60000}") long timeoutMs) {
        this.webClient = webClientBuilder.build();
        this.interaccionIARepository = interaccionIARepository;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.chatModel = chatModel;
        this.textModel = textModel;
        this.imageModel = imageModel;
        this.mediaModel = mediaModel;
        this.siteUrl = siteUrl;
        this.appName = appName;
        this.timeout = Duration.ofMillis(timeoutMs);
    }

    public boolean isHabilitado() {
        return apiKey != null && !apiKey.isBlank() && !apiKey.startsWith("TU_");
    }

    public IAResponse chat(IAChatRequest request, User usuario) {
        if (!isHabilitado()) {
            return new IAResponse("La IA no está habilitada. Configura OPENROUTER_API_KEY.", null);
        }
        List<Map<String, Object>> messages = new ArrayList<>();
        if (request.getContexto() != null && !request.getContexto().isBlank()) {
            messages.add(Map.of("role", "system", "content", request.getContexto()));
        }
        messages.add(Map.of("role", "user", "content", request.getMensaje()));
        IAResponse response = completar(chatModel, messages);
        registrarInteraccion(usuario, TipoInteraccion.CHAT_ASISTENTE,
                request.getMensaje(), response);
        return response;
    }

    public IAResponse generar(IAGenerarRequest request, User usuario) {
        if (!isHabilitado()) {
            return new IAResponse("La IA no está habilitada. Configura OPENROUTER_API_KEY.", null);
        }
        boolean imagen = "imagen".equalsIgnoreCase(request.getTipo());
        String model = imagen ? imageModel : textModel;
        Map<String, Object> message = Map.of("role", "user", "content", request.getPrompt());
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", model);
        payload.put("messages", List.of(message));
        if (imagen) {
            payload.put("modalities", List.of("text", "image"));
        }
        IAResponse response = completar(payload, model);
        registrarInteraccion(usuario,
                imagen ? TipoInteraccion.IA_GENERE_IMAGEN : TipoInteraccion.IA_GENERE_TEXTO,
                request.getPrompt(), response);
        return response;
    }

    public IAResponse clasificarMedia(MultipartFile file, User usuario) {
        if (!isHabilitado()) {
            return new IAResponse("La IA no está habilitada. Configura OPENROUTER_API_KEY.", null);
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar un archivo para clasificar");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen compatible");
        }
        try {
            String imageData = "data:" + contentType + ";base64,"
                    + java.util.Base64.getEncoder().encodeToString(file.getBytes());
            Map<String, Object> image = Map.of("type", "image_url",
                    "image_url", Map.of("url", imageData));
            Map<String, Object> text = Map.of("type", "text",
                    "text", "Describe y clasifica esta imagen de forma concisa.");
            Map<String, Object> message = Map.of("role", "user", "content", List.of(text, image));
            IAResponse response = completar(mediaModel, List.of(message));
            registrarInteraccion(usuario, TipoInteraccion.IA_CLASIFICACION_MEDIA,
                    "Clasificación de archivo " + file.getOriginalFilename(), response);
            return response;
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo leer el archivo para clasificarlo", exception);
        }
    }

    private IAResponse completar(String model, List<Map<String, Object>> messages) {
        return completar(Map.of("model", model, "messages", messages), model);
    }

    private IAResponse completar(Map<String, Object> payload, String model) {
        JsonNode response = webClient.post()
                .uri(baseUrl.replaceAll("/+$", "") + "/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .header("HTTP-Referer", siteUrl)
                .header("X-Title", appName)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block(timeout);

        if (response == null) {
            throw new IllegalStateException("OpenRouter devolvió una respuesta vacía");
        }
        JsonNode message = response.path("choices").path(0).path("message");
        String answer = message.path("content").asText("");
        if (answer.isBlank() && message.path("content").isArray()) {
            StringBuilder content = new StringBuilder();
            for (JsonNode part : message.path("content")) {
                if (part.hasNonNull("text")) {
                    content.append(part.path("text").asText());
                } else if (part.path("image_url").hasNonNull("url")) {
                    content.append(part.path("image_url").path("url").asText());
                }
            }
            answer = content.toString();
        }
        if (answer.isBlank() && message.path("images").isArray() && !message.path("images").isEmpty()) {
            answer = message.path("images").get(0).path("image_url").path("url").asText("");
        }
        if (answer.isBlank()) {
            throw new IllegalStateException("OpenRouter no devolvió contenido utilizable");
        }

        IAResponse result = new IAResponse(answer, response.path("model").asText(model));
        result.setTokensEntrada(response.path("usage").path("prompt_tokens").isInt()
                ? response.path("usage").path("prompt_tokens").asInt() : null);
        result.setTokensSalida(response.path("usage").path("completion_tokens").isInt()
                ? response.path("usage").path("completion_tokens").asInt() : null);
        return result;
    }

    private void registrarInteraccion(User usuario, TipoInteraccion tipo, String prompt, IAResponse response) {
        InteraccionIA interaccion = new InteraccionIA();
        interaccion.setUsuario(usuario);
        interaccion.setTipoInteraccion(tipo);
        interaccion.setPrompt(prompt);
        interaccion.setRespuesta(response.getRespuesta().length() > 4000
                ? response.getRespuesta().substring(0, 4000)
                : response.getRespuesta());
        interaccion.setModeloIa(response.getModelo());
        interaccion.setTokensEntrada(response.getTokensEntrada());
        interaccion.setTokensSalida(response.getTokensSalida());
        interaccion.setEstado(EstadoIA.COMPLETADO);
        interaccionIARepository.save(interaccion);
    }
}
