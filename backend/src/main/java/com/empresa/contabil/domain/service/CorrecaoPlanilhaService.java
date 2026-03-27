package com.empresa.contabil.domain.service;

import com.empresa.contabil.domain.model.Planilha;

import java.util.List;
import java.util.Map;

/**
 * Serviço de alto nível responsável por aplicar correções automáticas
 * de NCM e CEST em uma planilha de produtos.
 *
 * Nesta fase inicial, o foco é:
 * - Estado de São Paulo
 * - Regime Simples Nacional
 * - Colunas CODIGONCM e CEST
 */
public interface CorrecaoPlanilhaService {
    
    /**
     * Aplica correções de NCM/CEST na planilha informada, atualizando
     * os campos em memória e preenchendo metadados de IA (aiMetadata)
     * com um resumo das ações realizadas.
     *
     * @param planilha planilha já lida e estruturada em campos
     * @return instância de Planilha com correções aplicadas e metadados atualizados
     */
    Planilha corrigirNcmECest(Planilha planilha);

    /**
     * Variante que considera uma planilha base/referência para ampliar correlação.
     */
    Planilha corrigirNcmECest(Planilha planilha, List<Map<String, String>> linhasReferencia, String nomeArquivoReferencia);
}

