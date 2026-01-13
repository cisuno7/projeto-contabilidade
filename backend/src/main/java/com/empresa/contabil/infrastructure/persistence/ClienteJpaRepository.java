package com.empresa.contabil.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, UUID> {
    Optional<ClienteEntity> findByCnpj(String cnpj);
    boolean existsByCnpj(String cnpj);
}
