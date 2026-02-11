package com.empresa.contabil.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.contabil.domain.model.CorrecaoAIMetadata;
import com.empresa.contabil.domain.model.Produto;

public interface CorrecaoAIRepository extends JpaRepository<CorrecaoAIMetadata, UUID> {

    List<CorrecaoAIMetadata> findByProdutoOrderByDataCorrecaoDesc(Produto produto);
}

