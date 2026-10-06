package com.app.webia.service.impl;

import com.app.webia.dto.request.AuthRegisterRequest;
import com.app.webia.entity.User;
import com.app.webia.enums.Rol;
import com.app.webia.repository.UserRepository;
import com.app.webia.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User registrar(AuthRegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El username ya está registrado");
        }

        User user = new User();
        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRol(Rol.ROLE_USER);
        user.setActivo(true);
        user.setEmailVerificado(false);

        return userRepository.save(user);
    }
}
