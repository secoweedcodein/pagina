package com.app.webia.repository;

import com.app.webia.entity.LogActividad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LogActividadRepository extends JpaRepository<LogActividad, UUID> {
    List<LogActividad> findTop100ByOrderByCreatedAtDesc();
}
