package com.empresa.contabil.infrastructure.services;

import com.empresa.contabil.domain.model.CEST;
import com.empresa.contabil.domain.model.Campo;
import com.empresa.contabil.domain.model.NCM;
import com.empresa.contabil.domain.model.Planilha;
import com.empresa.contabil.domain.repository.CESTRepository;
import com.empresa.contabil.domain.repository.NCMRepository;
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


@Service
@RequiredArgsConstructor
@Slf4j
public class CorrecaoPlanilhaServiceImpl implements CorrecaoPlanilhaService {

    private static final Pattern NCM_REGEX = Pattern.compile("\\d{4}\\.\\d{2}\\.\\d{2}");

    private final NCMRepository ncmRepository;
    private final CESTRepository cestRepository;
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
        List<Map<String, String>> linhas =
                (List<Map<String, String>>) dados.getOrDefault("linhas", List.of());

        if (linhas.isEmpty()) {
            log.warn("Nenhuma linha estruturada encontrada para planilha {}", planilha.getId());
            return planilha;
        }

        Map<Integer, Map<String, Campo>> camposPorLinha =
                planilha.getCampos().stream()
                        .collect(Collectors.groupingBy(
                                c -> c.getLinha() != null ? c.getLinha() : -1,
                                Collectors.toMap(
                                        c -> c.getNome() != null ? c.getNome().trim().toUpperCase() : "",
                                        c -> c,
                                        (c1, c2) -> c1,
                                        LinkedHashMap::new
                                )
                        ));

        List<Map<String, Object>> metadadosLinhas = new ArrayList<>();

        for (Map<String, String> linha : linhas) {

            Integer numeroLinha = encontrarNumeroLinha(camposPorLinha, linha);
            if (numeroLinha == null) continue;

            Map<String, Campo> camposLinha = camposPorLinha.get(numeroLinha);
            if (camposLinha == null) continue;

            String ncm = normalizar(linha.get("CODIGONCM"));
            String cest = normalizar(linha.get("CEST"));
            String nome = normalizar(linha.get("NOME"));
            String grupo = normalizar(linha.get("GRUPO"));

            List<Map<String, Object>> acoes = new ArrayList<>();

            // =============================
            // Validação formato NCM
            // =============================
            if (ncm != null && !ncm.isBlank() && !NCM_REGEX.matcher(ncm).matches()) {

                Map<String, Object> acao = new LinkedHashMap<>();
                acao.put("campo", "CODIGONCM");
                acao.put("antes", ncm);
                acao.put("depois", ncm);
                acao.put("tipo", "VALIDACAO");
                acao.put("mensagem",
                        "NCM com formato inesperado (esperado ####.##.##). Nenhuma correção automática aplicada.");

                acoes.add(acao);
            }

            // =============================
            // Correção baseada em banco
            // =============================
            if (ncm != null && !ncm.isBlank()) {

                Optional<NCM> ncmOpt = ncmRepository.findByCodigo(ncm);

                if (ncmOpt.isEmpty()) {

                    Map<String, Object> acao = new LinkedHashMap<>();
                    acao.put("campo", "CODIGONCM");
                    acao.put("tipo", "ERRO");
                    acao.put("mensagem", "NCM não encontrado na base oficial.");

                    acoes.add(acao);

                } else {

                    NCM ncmEntity = ncmOpt.get();

                    List<CEST> cestsOficiais =
                            cestRepository.findAllByNcm(ncmEntity);

                    if (cestsOficiais.isEmpty()) {

                        Map<String, Object> acao = new LinkedHashMap<>();
                        acao.put("campo", "CEST");
                        acao.put("tipo", "ERRO");
                        acao.put("mensagem", "Nenhum CEST vinculado a este NCM na base oficial.");

                        acoes.add(acao);

                    } else {

                        Optional<CEST> cestMatch =
                                encontrarMelhorCest(cestsOficiais, nome, grupo);

                        if (cestMatch.isEmpty()) {

                            Map<String, Object> acao = new LinkedHashMap<>();
                            acao.put("campo", "CEST");
                            acao.put("tipo", "CONFLITO");
                            acao.put("mensagem",
                                    "Múltiplos CEST possíveis. Não foi possível determinar automaticamente.");

                            acoes.add(acao);

                        } else {

                            String cestOficial = cestMatch.get().getCodigo();

                            boolean cestVazio =
                                    (cest == null || cest.isBlank() || "–".equals(cest));

                            if (cestVazio || !cestOficial.equals(cest)) {

                                Campo campoCest = camposLinha.get("CEST");

                                if (campoCest != null) {

                                    String antes = campoCest.getValor();
                                    campoCest.setValor(cestOficial);

                                    Map<String, Object> acao = new LinkedHashMap<>();
                                    acao.put("campo", "CEST");
                                    acao.put("antes", antes);
                                    acao.put("depois", cestOficial);
                                    acao.put("tipo",
                                            cestVazio ? "PREENCHIMENTO_OFICIAL" : "CORRECAO_OFICIAL_BASE");
                                    acao.put("criterio", "BASE_OFICIAL_NCM_CEST");
                                    acao.put("ncm", ncm);
                                    acao.put("nome", nome);
                                    acao.put("grupo", grupo);

                                    acoes.add(acao);
                                }
                            }
                        }
                    }
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
                String json = objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(metadadosLinhas);

                planilha.setAiMetadata(json);

            } catch (JsonProcessingException e) {
                log.error("Erro ao serializar metadados de IA para JSON", e);
            }
        }

        planilha.setDataAtualizacao(java.time.LocalDateTime.now());

        log.info("Correção concluída para planilha {}. Linhas com ações: {}",
                planilha.getId(), metadadosLinhas.size());

        return planilha;
    }

    private Integer encontrarNumeroLinha(
            Map<Integer, Map<String, Campo>> camposPorLinha,
            Map<String, String> linha) {

        String nome = normalizar(linha.get("NOME"));
        String codigo = normalizar(linha.get("CODIGO"));

        for (Map.Entry<Integer, Map<String, Campo>> entry : camposPorLinha.entrySet()) {

            Integer numLinha = entry.getKey();
            Map<String, Campo> campos = entry.getValue();

            Campo campoNome = campos.get("NOME");
            Campo campoCodigo = campos.get("CODIGO");

            if (campoNome != null && nome != null &&
                    nome.equalsIgnoreCase(normalizar(campoNome.getValor()))) {
                return numLinha;
            }

            if (campoCodigo != null && codigo != null &&
                    codigo.equalsIgnoreCase(normalizar(campoCodigo.getValor()))) {
                return numLinha;
            }
        }

        return null;
    }

    private String normalizar(String valor) {
        if (valor == null) return null;
    
        return valor
                .replace(".", "")
                .replace("-", "")
                .trim();
    }
    
    private Optional<CEST> encontrarMelhorCest(
            List<CEST> cests,
            String nomeProduto,
            String grupo
    ) {

        if (cests.size() == 1) {
            return Optional.of(cests.get(0));
        }

        String nomeLower = nomeProduto != null ? nomeProduto.toLowerCase() : "";
        String grupoLower = grupo != null ? grupo.toLowerCase() : "";

        for (CEST cest : cests) {

            String descricao = cest.getDescricao() != null
                    ? cest.getDescricao().toLowerCase()
                    : "";

            if (!descricao.isBlank()) {

                if (nomeLower.contains(descricao) ||
                        grupoLower.contains(descricao)) {

                    return Optional.of(cest);
                }
            }
        }

        return Optional.empty();
    }
}
