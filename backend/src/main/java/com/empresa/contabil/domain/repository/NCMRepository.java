package com.empresa.contabil.domain.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;


import com.empresa.contabil.domain.model.NCM;


public interface NCMRepository extends JpaRepository<NCM, UUID> {
    Optional<NCM> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
    @Query("""
        SELECT n FROM NCM n
        WHERE LOWER(n.descricao) LIKE LOWER(CONCAT('%', :descricao, '%'))
        """)
    List<NCM> buscarPorDescricaoAproximada(@Param("descricao") String descricao);
}
