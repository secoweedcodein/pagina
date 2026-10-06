package com.app.webia.service;

import com.app.webia.entity.LogActividad;
import com.app.webia.entity.User;

import java.util.List;
import java.util.Map;

public interface LogService {
    void registrar(String accion, String modulo, User usuario, String ip, String userAgent, Map<String, Object> metadata);
    List<LogActividad> ultimos100();
}
