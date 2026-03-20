package com.empresa.contabil.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.empresa.contabil.domain.model.Produto;
import com.empresa.contabil.domain.model.StatusValidacaoProduto;


public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

    List<Produto> findByStatusValidacao(StatusValidacaoProduto status);

    List<Produto> findByNcmIsNull();

    List<Produto> findByCestIsNull();

    /**
     * Busca produtos por nome contendo o termo (case insensitive).
     * Usado para classificar planilhas: primeiro tenta achar NCM/CEST pelo cadastro de produtos.
     */
    @Query("SELECT p FROM Produto p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Produto> buscarPorNomeContendo(@Param("termo") String termo);
}
