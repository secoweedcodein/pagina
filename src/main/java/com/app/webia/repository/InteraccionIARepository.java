package com.app.webia.repository;

import com.app.webia.entity.InteraccionIA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface InteraccionIARepository extends JpaRepository<InteraccionIA, UUID> {
    @Query("select i from InteraccionIA i left join fetch i.usuario order by i.createdAt desc")
    List<InteraccionIA> findRecentWithUser(Pageable pageable);
}
