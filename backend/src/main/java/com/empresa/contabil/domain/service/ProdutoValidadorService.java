package com.empresa.contabil.domain.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.empresa.contabil.domain.model.CEST;
import com.empresa.contabil.domain.model.NCM;
import com.empresa.contabil.domain.model.Produto;
import com.empresa.contabil.domain.model.StatusValidacaoProduto;
import com.empresa.contabil.domain.repository.ProdutoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProdutoValidadorService {

    private final ProdutoRepository produtoRepository;
    private final NCMValidatorService ncmValidatorService;
    private final CESTValidatorService cestValidatorService;

    @Transactional
    public void validarProduto(UUID produtoId) {

        Produto produto = produtoRepository.findById(produtoId)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // 1️⃣ Validar NCM
        Optional<NCM> ncmOpt = ncmValidatorService
                .validar(produto.getCodigoNcmInformado());

        if (ncmOpt.isEmpty()) {
            produto.definirStatus(StatusValidacaoProduto.INVALIDO);
            produto.definirDataProcessamento(LocalDateTime.now());
            return;
        }

        produto.definirNcm(ncmOpt.get());

        // 2️⃣ Validar CEST
        Optional<CEST> cestOpt = cestValidatorService
                .validar(produto.getCodigoCestInformado(), ncmOpt.get());

        if (cestOpt.isEmpty()) {
            produto.definirStatus(StatusValidacaoProduto.INVALIDO);
            produto.definirDataProcessamento(LocalDateTime.now());
            return;
        }

        produto.definirCest(cestOpt.get());

        // 3️⃣ Produto válido
        produto.definirStatus(StatusValidacaoProduto.VALIDADO);
        produto.definirDataProcessamento(LocalDateTime.now());
    }
}
