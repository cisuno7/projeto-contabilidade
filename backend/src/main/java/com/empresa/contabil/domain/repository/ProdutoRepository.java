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
     * Busca produtos por nome - match nos dois sentidos (case insensitive):
     * - produto.nome contém o termo (ex: produto "Leite UHT" encontra "Leite UHT Integral")
     * - termo contém produto.nome (ex: planilha "Leite UHT Integral 1L" encontra produto "Leite UHT")
     */
    @Query("SELECT p FROM Produto p WHERE " +
            "LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) " +
            "OR LOWER(:termo) LIKE LOWER(CONCAT('%', p.nome, '%'))")
    List<Produto> buscarPorNomeContendo(@Param("termo") String termo);
}
