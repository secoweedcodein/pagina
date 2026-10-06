package com.app.webia.repository;

import com.app.webia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByUsernameIgnoreCase(String username);
}
