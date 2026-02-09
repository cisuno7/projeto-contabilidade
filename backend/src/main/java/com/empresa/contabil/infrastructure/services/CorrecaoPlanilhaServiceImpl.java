package com.empresa.contabil.infrastructure.services;

import com.empresa.contabil.domain.model.Campo;
import com.empresa.contabil.domain.model.Planilha;
import com.empresa.contabil.domain.service.AIService;
import com.empresa.contabil.domain.service.CorrecaoPlanilhaService;
import com.empresa.contabil.domain.service.InterpretadorPlanilhaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementação inicial do serviço de correção automática de NCM/CEST.
 *
 * Estratégia da fase 1:
 * - Validar formato de NCM (regex simples).
 * - Preencher/ajustar CEST com base em um mapa de referência NCM -> CEST
 *   derivado da planilha de exemplo (padaria) e da tabela de CEST SP.
 * - Registrar em aiMetadata um resumo das alterações realizadas.
 *
 * Observação: a integração com IA (OpenAI) ainda é opcional aqui; o foco é
 * estruturar o pipeline de correção. Quando a IA estiver configurada, este
 * serviço pode usar o AIService para tratar casos não cobertos pelas regras
 * fixas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CorrecaoPlanilhaServiceImpl implements CorrecaoPlanilhaService {
    
    private static final Pattern NCM_REGEX = Pattern.compile("\\d{4}\\.\\d{2}\\.\\d{2}");
    
    // Mapa inicial de referência NCM -> CEST baseado na planilha de exemplo (padaria).
    // Focado em SP + Simples Nacional.
    private static final Map<String, String> NCM_PARA_CEST_PADRAO;
    
    static {
        Map<String, String> map = new HashMap<>();
        map.put("0401.10.10", "17.016.00"); // Leite UHT
        map.put("1806.90.00", "17.006.00"); // Achocolatado em pó
        map.put("0402.99.90", "17.020.00"); // Leite condensado
        map.put("0903.00.10", "17.098.00"); // Chá (erva-mate)
        map.put("2106.90.30", "17.097.00"); // Chá industrializado
        map.put("2106.90.10", "17.033.00"); // Gelatina alimentar
        map.put("2102.30.10", "17.033.00"); // Fermento químico
        map.put("0801.19.00", "17.016.00"); // Coco seco ralado
        map.put("0806.20.00", "17.016.00"); // Uva passa
        map.put("2209.00.10", "17.036.00"); // Vinagre
        map.put("1507.10.00", "17.015.00"); // Óleo de soja
        map.put("2501.00.90", "17.004.00"); // Sal alimentar
        map.put("0901.21.00", "17.099.00"); // Café torrado e moído
        map.put("1701.99.00", "17.001.00"); // Açúcar refinado
        map.put("1701.11.00", "17.001.00"); // Açúcar cristal
        map.put("1101.00.10", "17.005.00"); // Farinha de trigo
        map.put("1102.20.00", "17.005.00"); // Fubá
        map.put("1108.12.00", "17.005.00"); // Amido de milho
        map.put("1006.30.21", "17.002.00"); // Arroz branco
        map.put("1006.30.11", "17.002.00"); // Arroz parboilizado
        map.put("1902.11.00", "17.008.00"); // Massas secas
        map.put("1902.30.00", "17.008.00"); // Massa instantânea
        map.put("0713.33.19", "17.003.00"); // Feijões
        map.put("0713.40.00", "17.003.00"); // Lentilha
        NCM_PARA_CEST_PADRAO = Collections.unmodifiableMap(map);
    }
    
    private final InterpretadorPlanilhaService interpretadorPlanilhaService;
    private final AIService aiService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Planilha corrigirNcmECest(Planilha planilha) {
        if (planilha == null) {
            return null;
        }
        
        log.info("Iniciando correção de NCM/CEST para planilha {}", planilha.getId());
        
        Map<String, Object> dados = interpretadorPlanilhaService.extrairDadosEstruturados(planilha);
        @SuppressWarnings("unchecked")
        List<Map<String, String>> linhas = (List<Map<String, String>>) dados.getOrDefault("linhas", List.of());
        
        if (linhas.isEmpty()) {
            log.warn("Nenhuma linha estruturada encontrada para planilha {}", planilha.getId());
            return planilha;
        }
        
        // Índice rápido: (linha, nomeCampo) -> Campo
        Map<Integer, Map<String, Campo>> camposPorLinha = planilha.getCampos().stream()
                .collect(Collectors.groupingBy(
                        c -> c.getLinha() != null ? c.getLinha() : -1,
                        Collectors.toMap(
                                c -> c.getNome() != null ? c.getNome().trim().toUpperCase() : "",
                                c -> c,
                                (c1, c2) -> c1,
                                LinkedHashMap::new
                        )));
        
        List<Map<String, Object>> metadadosLinhas = new ArrayList<>();
        
        for (Map<String, String> linha : linhas) {
            // Procurar a linha "real" pelo número de linha do primeiro campo
            Integer numeroLinha = encontrarNumeroLinha(camposPorLinha, linha);
            if (numeroLinha == null) {
                continue;
            }
            
            Map<String, Campo> camposLinha = camposPorLinha.get(numeroLinha);
            if (camposLinha == null) {
                continue;
            }
            
            String ncm = normalizar(linha.get("CODIGONCM"));
            String cest = normalizar(linha.get("CEST"));
            String nome = normalizar(linha.get("NOME"));
            String grupo = normalizar(linha.get("GRUPO"));
            
            List<Map<String, Object>> acoes = new ArrayList<>();
            
            // Validação básica de NCM (formato)
            if (ncm != null && !ncm.isBlank() && !NCM_REGEX.matcher(ncm).matches()) {
                Map<String, Object> acao = new LinkedHashMap<>();
                acao.put("campo", "CODIGONCM");
                acao.put("antes", ncm);
                acao.put("depois", ncm);
                acao.put("tipo", "VALIDACAO");
                acao.put("mensagem", "NCM com formato inesperado (esperado ####.##.##). Nenhuma correção automática aplicada.");
                acoes.add(acao);
            }
            
            // Correção/ preenchimento de CEST com base no NCM
            String cestSugerido = null;
            if (ncm != null && !ncm.isBlank()) {
                cestSugerido = NCM_PARA_CEST_PADRAO.get(ncm);
            }
            
            boolean cestVazio = (cest == null || cest.isBlank() || "–".equals(cest));
            if (cestSugerido != null && (cestVazio || !cestSugerido.equals(cest))) {
                Campo campoCest = camposLinha.getOrDefault("CEST", null);
                if (campoCest != null) {
                    String antes = campoCest.getValor();
                    campoCest.setValor(cestSugerido);
                    
                    Map<String, Object> acao = new LinkedHashMap<>();
                    acao.put("campo", "CEST");
                    acao.put("antes", antes);
                    acao.put("depois", cestSugerido);
                    acao.put("tipo", cestVazio ? "PREENCHIMENTO" : "CORRECAO");
                    acao.put("criterio", "MAPEAMENTO_FIXO_NCM_CEST_SP");
                    acao.put("ncm", ncm);
                    acao.put("nome", nome);
                    acao.put("grupo", grupo);
                    acoes.add(acao);
                }
            }
            
            if (!acoes.isEmpty()) {
                Map<String, Object> metaLinha = new LinkedHashMap<>();
                metaLinha.put("linha", numeroLinha);
                metaLinha.put("nome", nome);
                metaLinha.put("grupo", grupo);
                metaLinha.put("acoes", acoes);
                metadadosLinhas.add(metaLinha);
            }
        }
        
        if (!metadadosLinhas.isEmpty()) {
            try {
                String json = objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(metadadosLinhas);
                planilha.setAiMetadata(json);
            } catch (JsonProcessingException e) {
                log.error("Erro ao serializar metadados de IA para JSON", e);
            }
        }
        
        planilha.setDataAtualizacao(java.time.LocalDateTime.now());
        
        log.info("Correção de NCM/CEST concluída para planilha {}. Linhas com ações: {}", 
                planilha.getId(), metadadosLinhas.size());
        
        return planilha;
    }
    
    private Integer encontrarNumeroLinha(Map<Integer, Map<String, Campo>> camposPorLinha,
                                         Map<String, String> linha) {
        // Estratégia simples: encontrar uma linha cujo campo NOME ou CODIGO case com os valores da linha.
        String nome = normalizar(linha.get("NOME"));
        String codigo = normalizar(linha.get("CODIGO"));
        
        for (Map.Entry<Integer, Map<String, Campo>> entry : camposPorLinha.entrySet()) {
            Integer numLinha = entry.getKey();
            Map<String, Campo> campos = entry.getValue();
            
            Campo campoNome = campos.get("NOME");
            Campo campoCodigo = campos.get("CODIGO");
            
            if (campoNome != null && nome != null && nome.equalsIgnoreCase(normalizar(campoNome.getValor()))) {
                return numLinha;
            }
            if (campoCodigo != null && codigo != null && codigo.equalsIgnoreCase(normalizar(campoCodigo.getValor()))) {
                return numLinha;
            }
        }
        return null;
    }
    
    private String normalizar(String valor) {
        return valor != null ? valor.trim() : null;
    }
}

