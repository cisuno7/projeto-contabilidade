package com.empresa.contabil.domain.service;

import java.util.List;
import java.util.Map;

public interface AIService {
    String processarPreenchimento(String prompt, Map<String, Object> contexto);
    Map<String, Object> interpretarDadosPlanilha(String dadosPlanilha, String instrucoes);
    boolean isDisponivel();
    String sugerirNcm(String nomeProduto, String grupo);

    /**
     * Enriquece itens pendentes do relatório de correlação com texto da IA (conferência humana).
     * Cada mapa deve conter pelo menos: linha, nome, grupo, candidatos_produto.
     */
    void enrichirAnaliseCorrelacaoIA(List<Map<String, Object>> pendentes);
}
