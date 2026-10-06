package com.app.webia.service;

import com.app.webia.dto.request.AuthRegisterRequest;
import com.app.webia.entity.User;

public interface AuthService {
    User registrar(AuthRegisterRequest request);
}
