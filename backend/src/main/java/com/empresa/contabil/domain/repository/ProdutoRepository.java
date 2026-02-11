package com.empresa.contabil.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;


import com.empresa.contabil.domain.model.Produto;
import com.empresa.contabil.domain.model.StatusValidacaoProduto;


public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

    List<Produto> findByStatusValidacao(StatusValidacaoProduto status);

    List<Produto> findByNcmIsNull();

    List<Produto> findByCestIsNull();
}
