package com.empresa.contabil.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.empresa.contabil.domain.model.CEST;
import com.empresa.contabil.domain.model.NCM;

public interface CESTRepository extends JpaRepository<CEST, UUID> {
    Optional<CEST> findByCodigo(String codigo);

    List<CEST> findAllByNcm(NCM ncm);

    @Query("""
            SELECT c FROM CEST c
            WHERE c.ncm = :ncm AND c.valido = true
            AND (c.uf IS NULL OR UPPER(TRIM(c.uf)) = UPPER(TRIM(:uf)))
            """)
    List<CEST> findAllByNcmAndUf(@Param("ncm") NCM ncm, @Param("uf") String uf);

    Optional<CEST> findByCodigoAndNcm(String codigo, NCM ncm);

    @Query("""
            SELECT c FROM CEST c
            WHERE c.codigo = :codigo AND c.ncm = :ncm
            AND (c.uf IS NULL OR UPPER(TRIM(c.uf)) = UPPER(TRIM(:uf)))
            """)
    Optional<CEST> findByCodigoAndNcmAndUf(
            @Param("codigo") String codigo,
            @Param("ncm") NCM ncm,
            @Param("uf") String uf
    );

    boolean existsByCodigoAndNcm(String codigo, NCM ncm);



    @Query("""
            SELECT c FROM CEST c
            WHERE c.valido = true
            AND :ncm LIKE CONCAT(c.codigo, '%')
            """)
    List<CEST> buscarPorNcmCompativel(String ncm);
}
