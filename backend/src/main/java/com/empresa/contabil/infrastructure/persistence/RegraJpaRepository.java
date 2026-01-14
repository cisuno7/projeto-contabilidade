package com.empresa.contabil.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RegraJpaRepository extends JpaRepository<RegraEntity, UUID> {
    List<RegraEntity> findByClientId(UUID clientId);
    List<RegraEntity> findByClientIdAndIsActiveTrue(UUID clientId);
}
