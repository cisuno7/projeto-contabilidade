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

            String ncm = normalizar(obterValor(linha, "CODIGONCM", "NCM"));
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
                            String ncmFormatado = formatarNcmParaPlanilha(melhorMatch.getCodigo());
                            campoNcm.setValor(ncmFormatado);

                            ncmCorrigidos.add(
                                    nome + ": " + antes + " → " + ncmFormatado
                            );

                            Map<String, Object> acao = new LinkedHashMap<>();
                            acao.put("campo", "CODIGONCM");
                            acao.put("antes", antes);
                            acao.put("depois", ncmFormatado);
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

                                Campo campoNcm = obterCampo(camposLinha, "CODIGONCM", "NCM");

                                if (campoNcm != null) {

                                    String antes = campoNcm.getValor();
                                    campoNcm.setValor(formatarNcmParaPlanilha(sugestaoIA));

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
                    // Buscar CESTs pelo NCM vinculado no banco (não pelo código do CEST)
                    List<CEST> cestsOficiais =
                            cestRepository.findAllByNcm(ncmOpt.get());

                    if (!cestsOficiais.isEmpty()) {

                        Optional<CEST> cestMatch =
                                encontrarMelhorCest(cestsOficiais, nome, grupo);

                        if (cestMatch.isPresent()) {

                            CEST cestEntity = cestMatch.get();
                            String cestOficial = cestEntity.getCodigo(); // sempre 7 dígitos do banco (CEST)
                            Campo campoCest = obterCampo(camposLinha, "CEST", "CODIGOCEST");

                            if (campoCest != null) {

                                String antes = campoCest.getValor();
                                boolean cestVazio =
                                        (antes == null || antes.isBlank());

                                if (cestVazio ||
                                        !normalizar(antes)
                                                .equals(normalizar(cestOficial))) {

                                    campoCest.setValor(formatarCestParaPlanilha(cestOficial));

                                    String cestFormatado = formatarCestParaPlanilha(cestOficial);
                                    cestCorrigidos.add(
                                            nome + ": " + antes + " → " + cestFormatado
                                    );

                                    Map<String, Object> acao =
                                            new LinkedHashMap<>();

                                    acao.put("campo", "CEST");
                                    acao.put("antes", antes);
                                    acao.put("depois", cestFormatado);
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

    /** Obtém valor da linha por uma das chaves (ex.: CODIGONCM ou NCM). */
    private static String obterValor(Map<String, String> linha, String... chaves) {
        for (String chave : chaves) {
            String v = linha.get(chave);
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    /** Obtém campo da linha por um dos nomes canônicos (ex.: CODIGONCM ou NCM). */
    private static Campo obterCampo(Map<String, Campo> camposLinha, String... nomes) {
        for (String nome : nomes) {
            Campo c = camposLinha.get(nome);
            if (c != null) return c;
        }
        return null;
    }

    /** NCM no banco é 8 dígitos; formata para planilha (ex.: 2005.20.00). */
    private static String formatarNcmParaPlanilha(String codigoNcm) {
        if (codigoNcm == null || codigoNcm.isBlank()) return codigoNcm;
        String digits = codigoNcm.replaceAll("\\D", "");
        if (digits.length() < 8) return codigoNcm;
        return digits.substring(0, 4) + "." + digits.substring(4, 6) + "." + digits.substring(6, 8);
    }

    /** CEST no banco é 7 dígitos; formata para planilha (ex.: 17.009.00). Nunca usar NCM aqui. */
    private static String formatarCestParaPlanilha(String codigoCest) {
        if (codigoCest == null || codigoCest.isBlank()) return codigoCest;
        String digits = codigoCest.replaceAll("\\D", "");
        if (digits.length() != 7) return codigoCest;
        return digits.substring(0, 2) + "." + digits.substring(2, 5) + "." + digits.substring(5, 7);
    }
}