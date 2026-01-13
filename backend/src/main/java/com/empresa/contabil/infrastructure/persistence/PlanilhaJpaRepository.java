package com.empresa.contabil.infrastructure.persistence;

import com.empresa.contabil.domain.model.Planilha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlanilhaJpaRepository extends JpaRepository<PlanilhaEntity, UUID> {
    List<PlanilhaEntity> findByClienteId(UUID clienteId);
    List<PlanilhaEntity> findByStatus(Planilha.StatusPlanilha status);
}

