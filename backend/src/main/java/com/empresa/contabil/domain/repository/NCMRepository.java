package com.empresa.contabil.domain.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


import com.empresa.contabil.domain.model.NCM;


public interface NCMRepository extends JpaRepository<NCM, UUID> {
    Optional<NCM> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

}
