package com.empresa.contabil.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.contabil.domain.model.CEST;
import com.empresa.contabil.domain.model.NCM;

public interface CESTRepository extends JpaRepository<CEST, UUID> {
    Optional<CEST> findByCodigo(String codigo);

    List<CEST> findAllByNcm(NCM ncm);

    Optional<CEST> findByCodigoAndNcm(String codigo, NCM ncm);

    boolean existsByCodigoAndNcm(String codigo, NCM ncm);
}
