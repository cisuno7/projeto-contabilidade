package com.empresa.contabil.domain.service;

import com.empresa.contabil.domain.model.Planilha;

/**
 * Serviço de alto nível responsável por aplicar correções automáticas
 * de NCM e CEST em uma planilha de produtos.
 *
 * Com {@code ufConferencia} (ex.: ES), a correção prioriza vínculos no banco
 * (produto + NCM/CEST da mesma UF). Sem UF, mantém heurísticas gerais (legado).
 */
public interface CorrecaoPlanilhaService {

    Planilha corrigirNcmECest(Planilha planilha);

    /**
     * @param ufConferencia sigla da UF (ex.: ES, SP) para filtrar NCM/CEST/produto no banco; null = sem filtro regional
     */
    Planilha corrigirNcmECest(Planilha planilha, String ufConferencia);
}
