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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorrecaoPlanilhaServiceImpl implements CorrecaoPlanilhaService {

    private final NCMRepository ncmRepository;
    private final CESTRepository cestRepository;
    private final InterpretadorPlanilhaService interpretadorPlanilhaService;
    private final AIService aiService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Planilha corrigirNcmECest(Planilha planilha) {

        if (planilha == null) return null;

        log.info("Iniciando correção de NCM/CEST para planilha {}", planilha.getId());

        List<String> ncmCorrigidos = new ArrayList<>();
        List<String> cestCorrigidos = new ArrayList<>();

        Map<String, Object> dados = interpretadorPlanilhaService.extrairDadosEstruturados(planilha);

        @SuppressWarnings("unchecked")
        List<Map<String, String>> linhas =
                (List<Map<String, String>>) dados.getOrDefault("linhas", List.of());

        if (linhas.isEmpty()) return planilha;

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
            String nome = normalizar(linha.get("NOME"));
            String grupo = normalizar(linha.get("GRUPO"));

            List<Map<String, Object>> acoes = new ArrayList<>();

            if (ncm != null && !ncm.isBlank()) {

                Optional<NCM> ncmOpt = ncmRepository.findByCodigo(ncm);

                if (ncmOpt.isEmpty()) {

                    List<NCM> possiveis =
                            ncmRepository.buscarPorDescricaoAproximada(nome);

                    if (!possiveis.isEmpty()) {

                        NCM melhorMatch = possiveis.get(0);
                        Campo campoNcm = camposLinha.get("CODIGONCM");

                        if (campoNcm != null) {

                            String antes = campoNcm.getValor();
                            campoNcm.setValor(melhorMatch.getCodigo());

                            ncmCorrigidos.add(
                                    nome + ": " + antes + " → " + melhorMatch.getCodigo()
                            );

                            Map<String, Object> acao = new LinkedHashMap<>();
                            acao.put("campo", "CODIGONCM");
                            acao.put("antes", antes);
                            acao.put("depois", melhorMatch.getCodigo());
                            acao.put("tipo", "CORRECAO_POR_DESCRICAO_BASE");
                            acao.put("criterio", "BUSCA_DESCRICAO_NCM");

                            acoes.add(acao);

                            ncm = melhorMatch.getCodigo();
                            ncmOpt = Optional.of(melhorMatch);
                        }

                    } else {

                        String sugestaoIA = aiService.sugerirNcm(nome, grupo);

                        if (sugestaoIA != null) {

                            Optional<NCM> ncmIA =
                                    ncmRepository.findByCodigo(sugestaoIA);

                            if (ncmIA.isPresent()) {

                                Campo campoNcm = camposLinha.get("CODIGONCM");

                                if (campoNcm != null) {

                                    String antes = campoNcm.getValor();
                                    campoNcm.setValor(sugestaoIA);

                                    ncmCorrigidos.add(
                                            nome + ": " + antes + " → " + sugestaoIA
                                    );

                                    Map<String, Object> acao = new LinkedHashMap<>();
                                    acao.put("campo", "CODIGONCM");
                                    acao.put("antes", antes);
                                    acao.put("depois", sugestaoIA);
                                    acao.put("tipo", "CORRECAO_VIA_IA_VALIDADA");

                                    acoes.add(acao);

                                    ncm = sugestaoIA;
                                    ncmOpt = ncmIA;
                                }
                            }
                        }
                    }
                }

                if (ncmOpt.isPresent()) {

                    // 🔥 ALTERAÇÃO IMPORTANTE AQUI
                    List<CEST> cestsOficiais =
                            cestRepository.buscarPorNcmCompativel(ncm);

                    if (!cestsOficiais.isEmpty()) {

                        Optional<CEST> cestMatch =
                                encontrarMelhorCest(cestsOficiais, nome, grupo);

                        if (cestMatch.isPresent()) {

                            String cestOficial = cestMatch.get().getCodigo();
                            Campo campoCest = camposLinha.get("CEST");

                            if (campoCest != null) {

                                String antes = campoCest.getValor();
                                boolean cestVazio =
                                        (antes == null || antes.isBlank());

                                if (cestVazio ||
                                        !normalizar(antes)
                                                .equals(normalizar(cestOficial))) {

                                    campoCest.setValor(cestOficial);

                                    cestCorrigidos.add(
                                            nome + ": " + antes + " → " + cestOficial
                                    );

                                    Map<String, Object> acao =
                                            new LinkedHashMap<>();

                                    acao.put("campo", "CEST");
                                    acao.put("antes", antes);
                                    acao.put("depois", cestOficial);
                                    acao.put("tipo",
                                            cestVazio
                                                    ? "PREENCHIMENTO_OFICIAL"
                                                    : "CORRECAO_OFICIAL_BASE");

                                    acoes.add(acao);
                                }
                            }
                        }
                    }
                }
            }

            if (!acoes.isEmpty()) {

                Map<String, Object> metaLinha =
                        new LinkedHashMap<>();

                metaLinha.put("linha", numeroLinha);
                metaLinha.put("nome", nome);
                metaLinha.put("grupo", grupo);
                metaLinha.put("acoes", acoes);

                metadadosLinhas.add(metaLinha);
            }
        }

        if (!metadadosLinhas.isEmpty()) {
            try {

                String json =
                        objectMapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(metadadosLinhas);

                log.debug("Detalhamento técnico da correção:\n{}", json);

                StringBuilder resumo = new StringBuilder();
                resumo.append("CEST e NCM ajustados\n\n");

                if (!cestCorrigidos.isEmpty()) {
                    resumo.append("CEST corrigidos:\n\n");
                    cestCorrigidos.forEach(c ->
                            resumo.append(c).append("\n"));
                    resumo.append("\n");
                }

                if (!ncmCorrigidos.isEmpty()) {
                    resumo.append("NCM corrigidos:\n\n");
                    ncmCorrigidos.forEach(n ->
                            resumo.append(n).append("\n"));
                    resumo.append("\n");
                }

                planilha.setAiMetadata(resumo.toString());

            } catch (JsonProcessingException e) {
                log.error("Erro ao gerar metadata", e);
            }
        }

        planilha.setDataAtualizacao(java.time.LocalDateTime.now());

        log.info("Correção concluída para planilha {}", planilha.getId());

        return planilha;
    }

    private Integer encontrarNumeroLinha(
            Map<Integer, Map<String, Campo>> camposPorLinha,
            Map<String, String> linha) {

        String nome = normalizar(linha.get("NOME"));

        for (Map.Entry<Integer, Map<String, Campo>> entry :
                camposPorLinha.entrySet()) {

            Campo campoNome = entry.getValue().get("NOME");

            if (campoNome != null &&
                    nome != null &&
                    nome.equalsIgnoreCase(
                            normalizar(campoNome.getValor())
                    )) {

                return entry.getKey();
            }
        }

        return null;
    }

    private String normalizar(String valor) {
        if (valor == null) return null;

        return valor.replace(".", "")
                .replace("-", "")
                .trim();
    }

    private Optional<CEST> encontrarMelhorCest(
            List<CEST> cests,
            String nomeProduto,
            String grupo
    ) {

        if (cests.size() == 1)
            return Optional.of(cests.get(0));

        String nomeLower =
                nomeProduto != null
                        ? nomeProduto.toLowerCase()
                        : "";

        for (CEST cest : cests) {

            String descricao =
                    cest.getDescricao() != null
                            ? cest.getDescricao().toLowerCase()
                            : "";

            if (!descricao.isBlank() &&
                    nomeLower.contains(descricao)) {

                return Optional.of(cest);
            }
        }

        return Optional.empty();
    }
}