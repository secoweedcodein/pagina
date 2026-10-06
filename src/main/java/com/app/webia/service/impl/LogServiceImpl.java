package com.app.webia.service.impl;

import com.app.webia.entity.LogActividad;
import com.app.webia.entity.User;
import com.app.webia.repository.LogActividadRepository;
import com.app.webia.service.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LogServiceImpl implements LogService {
    private final LogActividadRepository logActividadRepository;
    private final ObjectMapper objectMapper;

    public LogServiceImpl(LogActividadRepository logActividadRepository, ObjectMapper objectMapper) {
        this.logActividadRepository = logActividadRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void registrar(String accion, String modulo, User usuario, String ip, String userAgent, Map<String, Object> metadata) {
        LogActividad log = new LogActividad();
        log.setAccion(accion);
        log.setModulo(modulo);
        log.setUsuario(usuario);
        log.setIp(ip);
        log.setUserAgent(userAgent);
        try {
            log.setMetadata(metadata == null ? "{}" : objectMapper.writeValueAsString(metadata));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("No se pudieron serializar los metadatos del registro", exception);
        }
        logActividadRepository.save(log);
    }

    @Override
    public List<LogActividad> ultimos100() {
        return logActividadRepository.findTop100ByOrderByCreatedAtDesc();
    }
}
