package com.empresa.contabil.infrastructure.services;

import com.empresa.contabil.domain.model.CEST;
import com.empresa.contabil.domain.model.Campo;
import com.empresa.contabil.domain.model.NCM;
import com.empresa.contabil.domain.model.Planilha;
import com.empresa.contabil.domain.model.Produto;
import com.empresa.contabil.domain.repository.CESTRepository;
import com.empresa.contabil.domain.repository.NCMRepository;
import com.empresa.contabil.domain.repository.ProdutoRepository;
import com.empresa.contabil.domain.service.AIService;
import com.empresa.contabil.domain.service.CorrecaoPlanilhaService;
import com.empresa.contabil.domain.service.InterpretadorPlanilhaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorrecaoPlanilhaServiceImpl implements CorrecaoPlanilhaService {

    private static final Map<String, Set<String>> DICIONARIO_SEMANTICO = Map.ofEntries(
            Map.entry("massas_alimenticias", Set.of(
                    "massa", "massas", "macarrao", "espaguete", "parafuso", "penne", "lamen", "noodles", "nissin", "talharim"
            )),
            Map.entry("achocolatados_po", Set.of(
                    "achocolatado", "achocolatados", "nescau", "toddy", "cacau", "chocolate", "cappuccino"
            )),
            Map.entry("batata_processada", Set.of(
                    "batata", "palha", "chips", "ruffles", "lays", "frita", "cebola", "salsa", "churrasco"
            )),
            Map.entry("goma_mascar", Set.of(
                    "chiclete", "goma", "mascar", "trident"
            )),
            Map.entry("bebidas_refrigerantes", Set.of(
                    "refrigerante", "schweppes", "coca", "cola", "fanta", "sprite", "guarana", "tonica", "zero"
            )),
            Map.entry("bebidas_sucos", Set.of(
                    "suco", "nectar", "kapo", "tang", "valle", "maracuja", "limao", "laranja", "abacaxi", "tangerina", "uva", "frut"
            )),
            Map.entry("agua_mineral", Set.of(
                    "agua", "mineral", "crystal", "retornavel"
            )),
            Map.entry("panificacao_geral", Set.of(
                    "pao", "broa", "torrada", "wafer", "bolinho", "bolo", "donuts", "pizza", "polvilho", "bauducco", "marilan"
            )),
            Map.entry("doces_balas_confeitos", Set.of(
                    "pirulito", "marshmallow", "bala", "bombom", "sonho", "doce"
            )),
            Map.entry("oleos_e_condimentos", Set.of(
                    "oleo", "soja", "azeitona", "azeitonas", "ketchup", "maionese", "vinagre", "fugini", "quero"
            )),
            Map.entry("leite_e_laticinios", Set.of(
                    "leite", "uht", "condensado", "iogurte", "yakult", "creme", "requeijao", "queijo", "manteiga", "mussarela", "prato", "parmesao"
            )),
            Map.entry("cha_e_mate", Set.of(
                    "cha", "leao", "mate", "camomila", "hortela", "cidreira", "erva", "gelado"
            )),
            Map.entry("cafe", Set.of(
                    "cafe", "canecao", "moraes", "soluvel", "nescafe", "latte"
            )),
            Map.entry("salgadinhos_fabitos_cheetos", Set.of(
                    "fabitos", "cebolitos", "cheetos", "doritos", "lays", "ruffles"
            )),
            Map.entry("biscoitos_e_bolachas", Set.of(
                    "biscoito", "cracker", "maisena", "trakinas", "passatempo", "recheado", "cookie", "torrada"
            )),
            Map.entry("fermentos_e_ingredientes", Set.of(
                    "fermento", "royal", "gelatina", "amido", "fuba", "farinha", "acucar", "sal"
            )),
            Map.entry("temperos_prontos", Set.of(
                    "maggi", "caldo", "sazon", "segredo", "tempero"
            )),
            Map.entry("coco_derivados", Set.of(
                    "coco", "flocado", "ralado", "tapioca"
            )),
            Map.entry("filtro_papel", Set.of(
                    "filtro", "melita", "papel"
            )),
            Map.entry("doces_tradicionais", Set.of(
                    "pacoca", "moleque", "cocada", "cueca"
            )),
            Map.entry("lanches_frios", Set.of(
                    "salgado", "esfiha", "enroladinho", "lanche", "mini"
            ))
    );

    private final NCMRepository ncmRepository;
    private final CESTRepository cestRepository;
    private final ProdutoRepository produtoRepository;
    private final InterpretadorPlanilhaService interpretadorPlanilhaService;
    private final AIService aiService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Planilha corrigirNcmECest(Planilha planilha) {
        return corrigirNcmECest(planilha, null, null);
    }

    @Override
    public Planilha corrigirNcmECest(
            Planilha planilha,
            List<Map<String, String>> linhasReferencia,
            String nomeArquivoReferencia
    ) {

        if (planilha == null) return null;

        log.info("Iniciando correção de NCM/CEST para planilha {}", planilha.getId());

        List<String> ncmCorrigidos = new ArrayList<>();
        List<String> cestCorrigidos = new ArrayList<>();
        int correcoesViaReferencia = 0;

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
        int totalLinhas = 0;

        for (Map<String, String> linha : linhas) {

            Integer numeroLinha = encontrarNumeroLinha(camposPorLinha, linha);
            if (numeroLinha == null) continue;
            totalLinhas++;

            Map<String, Campo> camposLinha = camposPorLinha.get(numeroLinha);
            if (camposLinha == null) continue;

            String ncm = normalizar(obterValor(linha, "CODIGONCM", "NCM"));
            String nome = normalizar(linha.get("NOME"));
            String grupo = normalizar(linha.get("GRUPO"));

            List<Map<String, Object>> acoes = new ArrayList<>();
            Campo campoNcm = obterCampo(camposLinha, "CODIGONCM", "NCM");
            Optional<NCM> ncmOpt = Optional.empty();

            if (ncm != null && !ncm.isBlank()) {
                ncmOpt = ncmRepository.findByCodigo(ncm);
            }

            boolean resolucaoCompletaViaReferencia = false;
            if (nome != null && !nome.isBlank()
                    && linhasReferencia != null && !linhasReferencia.isEmpty()) {

                Optional<ReferenciaMatch> refMatchOpt =
                        buscarEmPlanilhaReferencia(nome, grupo, linhasReferencia);

                if (refMatchOpt.isPresent()) {
                    ReferenciaMatch ref = refMatchOpt.get();
                    Optional<NCM> ncmRef = ncmRepository.findByCodigo(ref.ncm8());
                    Optional<CEST> cestRef = ref.cest7() != null
                            ? cestRepository.findByCodigo(ref.cest7())
                            : Optional.empty();

                    if (ncmRef.isPresent()) {
                        if (campoNcm != null) {
                            String antes = campoNcm.getValor();
                            boolean vazio = antes == null || antes.isBlank();
                            String ncmFormatado = formatarNcmParaPlanilha(ncmRef.get().getCodigo());
                            campoNcm.setValor(ncmFormatado);
                            ncmCorrigidos.add(nome + ": " + antes + " → " + ncmFormatado);

                            Map<String, Object> acao = new LinkedHashMap<>();
                            acao.put("campo", "CODIGONCM");
                            acao.put("antes", antes);
                            acao.put("depois", ncmFormatado);
                            acao.put("tipo", vazio ? "PREENCHIMENTO_PLANILHA_REFERENCIA" : "CORRECAO_PLANILHA_REFERENCIA");
                            acao.put("criterio", "PLANILHA_REFERENCIA_IA");
                            acao.put("referencia_nome", ref.nomeReferencia());
                            acoes.add(acao);
                            correcoesViaReferencia++;
                        }
                        ncmOpt = ncmRef;
                        ncm = ncmRef.get().getCodigo();
                    }

                    Campo campoCest = obterCampo(camposLinha, "CEST", "CODIGOCEST");
                    if (campoCest != null && cestRef.isPresent()) {
                        String antes = campoCest.getValor();
                        boolean vazio = antes == null || antes.isBlank();
                        if (vazio || !normalizar(antes).equals(normalizar(cestRef.get().getCodigo()))) {
                            String cestFmt = formatarCestParaPlanilha(cestRef.get().getCodigo());
                            campoCest.setValor(cestFmt);
                            cestCorrigidos.add(nome + ": " + antes + " → " + cestFmt);

                            Map<String, Object> acao = new LinkedHashMap<>();
                            acao.put("campo", "CEST");
                            acao.put("antes", antes);
                            acao.put("depois", cestFmt);
                            acao.put("tipo", vazio ? "PREENCHIMENTO_PLANILHA_REFERENCIA" : "CORRECAO_PLANILHA_REFERENCIA");
                            acao.put("criterio", "PLANILHA_REFERENCIA_IA");
                            acao.put("referencia_nome", ref.nomeReferencia());
                            acoes.add(acao);
                            correcoesViaReferencia++;
                        }
                    }

                    boolean ncmResolvido = campoNcm == null
                            || (campoNcm.getValor() != null && !campoNcm.getValor().isBlank());
                    boolean cestResolvido = campoCest == null
                            || (campoCest.getValor() != null && !campoCest.getValor().isBlank());
                    resolucaoCompletaViaReferencia = ncmResolvido && cestResolvido;
                }
            }

            if (!resolucaoCompletaViaReferencia && ncmOpt.isEmpty() && nome != null && !nome.isBlank()) {

                Optional<Produto> produtoMatch = buscarProdutoComClassificacao(nome, grupo);
                if (produtoMatch.isPresent()) {
                    Produto p = produtoMatch.get();
                    String criterioProduto = inferirCriterioProduto(nome, p.getNome());
                    String ncmProd = normalizar(p.getCodigoNcmInformado());
                    String cestProd = normalizar(p.getCodigoCestInformado());
                    Optional<NCM> ncmFromProd = ncmProd != null && !ncmProd.isBlank()
                            ? ncmRepository.findByCodigo(ncmProd) : Optional.empty();
                    Optional<CEST> cestFromProd = cestProd != null && cestProd.length() == 7
                            ? cestRepository.findByCodigo(cestProd) : Optional.empty();

                    if (ncmFromProd.isPresent() && cestFromProd.isPresent()) {

                        String ncmFormatado = formatarNcmParaPlanilha(ncmFromProd.get().getCodigo());
                        String cestFormatado = formatarCestParaPlanilha(cestFromProd.get().getCodigo());

                        if (campoNcm != null) {
                            String antesNcm = campoNcm.getValor();
                            boolean ncmVazio = antesNcm == null || antesNcm.isBlank();
                            campoNcm.setValor(ncmFormatado);
                            ncmCorrigidos.add(nome + ": " + antesNcm + " → " + ncmFormatado);
                            Map<String, Object> acao = new LinkedHashMap<>();
                            acao.put("campo", "CODIGONCM");
                            acao.put("antes", antesNcm);
                            acao.put("depois", ncmFormatado);
                            acao.put("tipo", ncmVazio ? "PREENCHIMENTO_TABELA_PRODUTO" : "CORRECAO_TABELA_PRODUTO");
                            acao.put("criterio", criterioProduto);
                            acoes.add(acao);
                        }
                        ncm = ncmFromProd.get().getCodigo();
                        ncmOpt = ncmFromProd;

                        Campo campoCest = obterCampo(camposLinha, "CEST", "CODIGOCEST");
                        if (campoCest != null) {
                            String antesCest = campoCest.getValor();
                            boolean cestVazio = antesCest == null || antesCest.isBlank();
                            if (cestVazio || !normalizar(antesCest).equals(normalizar(cestFromProd.get().getCodigo()))) {
                                campoCest.setValor(cestFormatado);
                                cestCorrigidos.add(nome + ": " + antesCest + " → " + cestFormatado);
                                Map<String, Object> acao = new LinkedHashMap<>();
                                acao.put("campo", "CEST");
                                acao.put("antes", antesCest);
                                acao.put("depois", cestFormatado);
                                acao.put("tipo", cestVazio ? "PREENCHIMENTO_TABELA_PRODUTO" : "CORRECAO_TABELA_PRODUTO");
                                acao.put("criterio", criterioProduto);
                                acoes.add(acao);
                            }
                        }
                    }
                }
            }

            if (!resolucaoCompletaViaReferencia && ncmOpt.isEmpty()) {

                List<NCM> possiveis =
                        nome != null && !nome.isBlank()
                                ? ncmRepository.buscarPorDescricaoAproximada(nome)
                                : List.of();

                if (!possiveis.isEmpty()) {

                    NCM melhorMatch = possiveis.get(0);

                    if (campoNcm != null) {

                        String antes = campoNcm.getValor();
                        String ncmFormatado = formatarNcmParaPlanilha(melhorMatch.getCodigo());
                        boolean ncmVazio = antes == null || antes.isBlank();

                        campoNcm.setValor(ncmFormatado);

                        ncmCorrigidos.add(
                                nome + ": " + antes + " → " + ncmFormatado
                        );

                        Map<String, Object> acao = new LinkedHashMap<>();
                        acao.put("campo", "CODIGONCM");
                        acao.put("antes", antes);
                        acao.put("depois", ncmFormatado);
                        acao.put("tipo", ncmVazio ? "PREENCHIMENTO_POR_DESCRICAO_BASE" : "CORRECAO_POR_DESCRICAO_BASE");
                        acao.put("criterio", "BUSCA_DESCRICAO_NCM");

                        acoes.add(acao);
                    }

                    ncm = melhorMatch.getCodigo();
                    ncmOpt = Optional.of(melhorMatch);

                } else if (nome != null && !nome.isBlank()) {

                    String sugestaoIA = aiService.sugerirNcm(nome, grupo);

                    if (sugestaoIA != null && !sugestaoIA.isBlank()) {

                        Optional<NCM> ncmIA =
                                ncmRepository.findByCodigo(normalizar(sugestaoIA));

                        if (ncmIA.isPresent()) {

                            if (campoNcm != null) {

                                String antes = campoNcm.getValor();
                                String ncmFormatado = formatarNcmParaPlanilha(ncmIA.get().getCodigo());
                                boolean ncmVazio = antes == null || antes.isBlank();

                                campoNcm.setValor(ncmFormatado);

                                ncmCorrigidos.add(
                                        nome + ": " + antes + " → " + ncmFormatado
                                );

                                Map<String, Object> acao = new LinkedHashMap<>();
                                acao.put("campo", "CODIGONCM");
                                acao.put("antes", antes);
                                acao.put("depois", ncmFormatado);
                                acao.put("tipo", ncmVazio ? "PREENCHIMENTO_VIA_IA_VALIDADA" : "CORRECAO_VIA_IA_VALIDADA");

                                acoes.add(acao);
                            }

                            ncm = ncmIA.get().getCodigo();
                            ncmOpt = ncmIA;
                        }
                    }
                }
            }

            if (ncmOpt.isPresent() && !resolucaoCompletaViaReferencia) {
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

        List<Map<String, Object>> pendentes =
                montarRelatorioPendentes(linhas, camposPorLinha);

        if (aiService.isDisponivel() && !pendentes.isEmpty()) {
            aiService.enrichirAnaliseCorrelacaoIA(pendentes);
        }

        try {

            StringBuilder resumo = new StringBuilder();
            if (!cestCorrigidos.isEmpty() || !ncmCorrigidos.isEmpty()) {
                resumo.append("CEST e NCM ajustados\n\n");
                if (!cestCorrigidos.isEmpty()) {
                    resumo.append("CEST corrigidos:\n\n");
                    cestCorrigidos.forEach(c -> resumo.append(c).append("\n"));
                    resumo.append("\n");
                }
                if (!ncmCorrigidos.isEmpty()) {
                    resumo.append("NCM corrigidos:\n\n");
                    ncmCorrigidos.forEach(n -> resumo.append(n).append("\n"));
                    resumo.append("\n");
                }
            } else {
                resumo.append("Nenhum campo NCM/CEST alterado automaticamente nesta execução.\n\n");
            }

            if (!pendentes.isEmpty()) {
                resumo.append("Relatório de correlação: ")
                        .append(pendentes.size())
                        .append(" linha(s) ainda sem NCM e/ou CEST — veja o bloco JSON \"relatorioCorrelacao\" para candidatos na base e sugestões.\n");
            }

            Map<String, Object> relatorioCorrelacao = new LinkedHashMap<>();
            relatorioCorrelacao.put("totalLinhas", totalLinhas);
            relatorioCorrelacao.put("linhasComAlteracao", metadadosLinhas.size());
            relatorioCorrelacao.put("linhasPendentes", pendentes.size());
            relatorioCorrelacao.put("correcoesViaPlanilhaReferencia", correcoesViaReferencia);
            relatorioCorrelacao.put("arquivoReferencia", nomeArquivoReferencia);
            relatorioCorrelacao.put(
                    "linhasReferenciaAnalisadas",
                    linhasReferencia != null ? linhasReferencia.size() : 0
            );
            relatorioCorrelacao.put(
                    "nota",
                    "Os nomes reproduzem o que o cliente informou. Use as correlações para decidir inclusões na tabela produto ou ajustes no cadastro fiscal, sem mudar a planilha."
            );
            relatorioCorrelacao.put("pendentes", pendentes);

            Map<String, Object> root = new LinkedHashMap<>();
            root.put("resumoTexto", resumo.toString());
            root.put("detalhesCorrecao", metadadosLinhas);
            root.put("relatorioCorrelacao", relatorioCorrelacao);

            planilha.setAiMetadata(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root)
            );

            log.debug(
                    "Metadados de correção e correlação persistidos ({} linhas, {} pendentes)",
                    totalLinhas,
                    pendentes.size()
            );

        } catch (JsonProcessingException e) {
            log.error("Erro ao gerar metadata", e);
        }

        planilha.setDataAtualizacao(java.time.LocalDateTime.now());

        log.info("Correção concluída para planilha {}", planilha.getId());

        return planilha;
    }

    /**
     * Linhas que continuam sem NCM e/ou CEST após o pipeline, com correlações na base para conferência.
     */
    private List<Map<String, Object>> montarRelatorioPendentes(
            List<Map<String, String>> linhas,
            Map<Integer, Map<String, Campo>> camposPorLinha) {

        List<Map<String, Object>> pendentes = new ArrayList<>();

        for (Map<String, String> linha : linhas) {
            Integer numeroLinha = encontrarNumeroLinha(camposPorLinha, linha);
            if (numeroLinha == null) {
                continue;
            }

            Map<String, Campo> camposLinha = camposPorLinha.get(numeroLinha);
            if (camposLinha == null) {
                continue;
            }

            String nome = normalizar(linha.get("NOME"));
            String grupo = normalizar(linha.get("GRUPO"));

            Campo campoNcm = obterCampo(camposLinha, "CODIGONCM", "NCM");
            Campo campoCest = obterCampo(camposLinha, "CEST", "CODIGOCEST");
            String ncmVal = campoNcm != null ? campoNcm.getValor() : null;
            String cestVal = campoCest != null ? campoCest.getValor() : null;

            boolean faltaNcm = ncmVal == null || ncmVal.isBlank();
            boolean faltaCest = cestVal == null || cestVal.isBlank();
            if (!faltaNcm && !faltaCest) {
                continue;
            }

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("linha", numeroLinha);
            item.put("nome", nome);
            item.put("grupo", grupo);
            item.put("ncm_atual", ncmVal);
            item.put("cest_atual", cestVal);
            item.put("falta_ncm", faltaNcm);
            item.put("falta_cest", faltaCest);

            item.put("candidatos_produto", montarCandidatosProduto(nome, grupo));

            if (faltaNcm && nome != null && !nome.isBlank()) {
                List<NCM> ncms = ncmRepository.buscarPorDescricaoAproximada(nome);
                List<Map<String, Object>> cn = new ArrayList<>();
                for (NCM n : ncms.stream().limit(5).toList()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("codigo", n.getCodigo());
                    m.put("descricao", n.getDescricao());
                    cn.add(m);
                }
                item.put("candidatos_ncm_por_descricao", cn);
            }

            if (faltaCest && !faltaNcm) {
                String codigoBusca = normalizar(ncmVal);
                Optional<NCM> ncmR = ncmRepository.findByCodigo(codigoBusca);
                if (ncmR.isEmpty() && codigoBusca != null) {
                    String d = codigoBusca.replaceAll("\\D", "");
                    if (d.length() >= 8) {
                        ncmR = ncmRepository.findByCodigo(d.substring(0, 8));
                    }
                }
                if (ncmR.isPresent()) {
                    List<CEST> lista = cestRepository.findAllByNcm(ncmR.get());
                    List<Map<String, Object>> op = new ArrayList<>();
                    for (CEST c : lista.stream().limit(10).toList()) {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("codigo", c.getCodigo());
                        m.put("descricao", c.getDescricao());
                        op.add(m);
                    }
                    item.put("cest_disponiveis_para_ncm", op);
                } else {
                    item.put(
                            "observacao",
                            "NCM informado não foi encontrado na base; valide o código ou cadastre o vínculo NCM/CEST."
                    );
                }
            }

            pendentes.add(item);
        }

        return pendentes;
    }

    private Optional<ReferenciaMatch> buscarEmPlanilhaReferencia(
            String nome,
            String grupo,
            List<Map<String, String>> linhasReferencia
    ) {
        if (nome == null || nome.isBlank() || linhasReferencia == null || linhasReferencia.isEmpty()) {
            return Optional.empty();
        }

        ReferenciaMatch melhor = null;
        int melhorScore = 0;

        for (Map<String, String> linhaRef : linhasReferencia) {
            String nomeRef = obterValorFlex(linhaRef, "produto", "nome", "descricao", "item");
            String ncmRefBruto = obterValorFlex(linhaRef, "ncm sugerido", "ncm", "codigoncm");
            String cestRefBruto = obterValorFlex(linhaRef, "cest sugerido", "cest", "codigocest");
            String grupoRef = obterValorFlex(linhaRef, "grupo sugerido", "grupo");

            String ncm8 = normalizarNcm8(ncmRefBruto);
            if (nomeRef == null || nomeRef.isBlank() || ncm8 == null) {
                continue;
            }

            int score = pontuarSimilaridadeProduto(nome, nomeRef);
            if (grupo != null && !grupo.isBlank() && grupoRef != null && grupo.equalsIgnoreCase(grupoRef)) {
                score += 2;
            }
            if (score < 2) {
                continue;
            }

            String cest7 = normalizarCest7(cestRefBruto);
            if (score > melhorScore) {
                melhorScore = score;
                melhor = new ReferenciaMatch(nomeRef, ncm8, cest7, score);
            }
        }

        return Optional.ofNullable(melhor);
    }

    private String obterValorFlex(Map<String, String> linha, String... chaves) {
        if (linha == null || linha.isEmpty()) return null;
        for (String chave : chaves) {
            String alvo = normalizarTexto(chave);
            for (Map.Entry<String, String> entry : linha.entrySet()) {
                if (normalizarTexto(entry.getKey()).equals(alvo)) {
                    String v = entry.getValue();
                    if (v != null && !v.isBlank()) return v.trim();
                }
            }
        }
        return null;
    }

    private String normalizarNcm8(String valor) {
        if (valor == null || valor.isBlank()) return null;
        String digits = valor.replaceAll("\\D", "");
        if (digits.length() >= 8) return digits.substring(0, 8);
        if (digits.length() == 6) return digits + "00";
        if (digits.length() == 4) return digits + "0000";
        return null;
    }

    private String normalizarCest7(String valor) {
        if (valor == null || valor.isBlank()) return null;
        if (valor.toLowerCase().contains("sem")) return null;
        String digits = valor.replaceAll("\\D", "");
        return digits.length() == 7 ? digits : null;
    }

    private record ReferenciaMatch(String nomeReferencia, String ncm8, String cest7, int score) {}

    private List<Map<String, Object>> montarCandidatosProduto(String nome, String grupo) {
        if (nome == null || nome.isBlank()) {
            return List.of();
        }
        List<Produto> candidatos = produtoRepository.buscarPorNomeContendo(nome);
        if (candidatos.isEmpty()) {
            String tok = primeiroTokenSignificativo(nome);
            if (tok != null) {
                candidatos = produtoRepository.buscarPorNomeContendo(tok);
            }
        }
        if (candidatos.isEmpty()) {
            return List.of();
        }

        return candidatos.stream()
                .map(p -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("nome", p.getNome());
                    m.put("grupo", p.getGrupo());
                    m.put("codigo_ncm", p.getCodigoNcmInformado());
                    m.put("codigo_cest", p.getCodigoCestInformado());
                    m.put("score_similaridade", pontuarSimilaridadeProduto(nome, p.getNome()));
                    return m;
                })
                .sorted((a, b) -> Integer.compare(
                        (Integer) b.get("score_similaridade"),
                        (Integer) a.get("score_similaridade")
                ))
                .limit(5)
                .collect(Collectors.toList());
    }

    private String primeiroTokenSignificativo(String nome) {
        Set<String> stop = Set.of("de", "da", "do", "dos", "das", "e", "com", "sem", "em", "para");
        for (String t : nome.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").split("\\s+")) {
            if (t.length() >= 3 && !stop.contains(t)) {
                return t;
            }
        }
        return null;
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

    /**
     * Busca na tabela produto por nome. Retorna o que tenha codigo_ncm e codigo_cest preenchidos.
     * Prioriza: 1) score de similaridade do nome, 2) grupo coincidente, 3) nome mais longo.
     */
    private Optional<Produto> buscarProdutoComClassificacao(String nome, String grupo) {
        if (nome == null || nome.isBlank()) return Optional.empty();
        List<Produto> candidatos = produtoRepository.buscarPorNomeContendo(nome);
        if (candidatos.isEmpty()) {
            // Fallback: varre catálogo para lidar com nomes comerciais (ex.: "Nescau 200g")
            candidatos = produtoRepository.findAll();
        }

        final String nomeEntrada = nome;
        final String grupoEntrada = grupo;

        return candidatos.stream()
                .filter(p -> p.getCodigoNcmInformado() != null && !p.getCodigoNcmInformado().isBlank()
                        && p.getCodigoCestInformado() != null && !p.getCodigoCestInformado().isBlank())
                .filter(p -> pontuarSimilaridadeProduto(nomeEntrada, p.getNome()) >= 2)
                .min((a, b) -> {
                    int scoreA = pontuarSimilaridadeProduto(nomeEntrada, a.getNome());
                    int scoreB = pontuarSimilaridadeProduto(nomeEntrada, b.getNome());
                    if (scoreA != scoreB) return Integer.compare(scoreB, scoreA);

                    if (grupoEntrada != null && !grupoEntrada.isBlank()) {
                        boolean aGrupo = grupoEntrada.equalsIgnoreCase(a.getGrupo());
                        boolean bGrupo = grupoEntrada.equalsIgnoreCase(b.getGrupo());
                        if (aGrupo != bGrupo) return aGrupo ? -1 : 1;
                    }
                    int lenA = a.getNome() != null ? a.getNome().length() : 0;
                    int lenB = b.getNome() != null ? b.getNome().length() : 0;
                    return Integer.compare(lenB, lenA);
                });
    }

    /**
     * Score simples para reduzir falsos negativos com nomes comerciais:
     * - +4: match exato normalizado
     * - +2: um contém o outro normalizado
     * - +1 por token relevante em comum (sem números/unidades)
     */
    private int pontuarSimilaridadeProduto(String nomePlanilha, String nomeProduto) {
        String a = normalizarTexto(nomePlanilha);
        String b = normalizarTexto(nomeProduto);
        if (a.isBlank() || b.isBlank()) return 0;

        int score = 0;
        if (a.equals(b)) score += 4;
        if (a.contains(b) || b.contains(a)) score += 2;

        Set<String> ta = tokensRelevantes(a);
        Set<String> tb = tokensRelevantes(b);
        ta.retainAll(tb);
        score += ta.size();

        Set<String> semA = tagsSemanticas(a);
        Set<String> semB = tagsSemanticas(b);
        semA.retainAll(semB);
        if (!semA.isEmpty()) {
            // Boost semântico para casos de "subentendido" (ex.: macarrão -> massas alimentícias)
            score += 3 + semA.size();
        }

        return score;
    }

    private String inferirCriterioProduto(String nomePlanilha, String nomeProduto) {
        Set<String> semA = tagsSemanticas(normalizarTexto(nomePlanilha));
        Set<String> semB = tagsSemanticas(normalizarTexto(nomeProduto));
        semA.retainAll(semB);
        return semA.isEmpty() ? "TABELA_PRODUTO" : "TABELA_PRODUTO_SEMANTICA";
    }

    private String normalizarTexto(String valor) {
        if (valor == null) return "";
        String semAcento = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private Set<String> tokensRelevantes(String texto) {
        if (texto == null || texto.isBlank()) return Set.of();
        Set<String> stop = Set.of("de", "da", "do", "dos", "das", "e", "com", "sem", "em", "para", "tipo", "g", "kg", "ml", "l");
        return Arrays.stream(texto.split(" "))
                .map(String::trim)
                .filter(t -> !t.isBlank())
                .filter(t -> !stop.contains(t))
                .filter(t -> !t.matches("\\d+"))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<String> tagsSemanticas(String textoNormalizado) {
        if (textoNormalizado == null || textoNormalizado.isBlank()) return Set.of();
        Set<String> tags = new LinkedHashSet<>();
        for (Map.Entry<String, Set<String>> entry : DICIONARIO_SEMANTICO.entrySet()) {
            for (String termo : entry.getValue()) {
                if (textoNormalizado.contains(termo)) {
                    tags.add(entry.getKey());
                    break;
                }
            }
        }
        return tags;
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

    /** NCM formata para planilha: 8 dígitos XX.XX.XX, 6 dígitos XX.XX.00, 4 dígitos XX.XX. */
    private static String formatarNcmParaPlanilha(String codigoNcm) {
        if (codigoNcm == null || codigoNcm.isBlank()) return codigoNcm;
        String digits = codigoNcm.replaceAll("\\D", "");
        if (digits.length() >= 8) {
            return digits.substring(0, 4) + "." + digits.substring(4, 6) + "." + digits.substring(6, 8);
        }
        if (digits.length() == 6) {
            return digits.substring(0, 4) + "." + digits.substring(4, 6) + ".00";
        }
        if (digits.length() == 4) {
            return digits.substring(0, 2) + "." + digits.substring(2, 4);
        }
        return codigoNcm;
    }

    /** CEST no banco é 7 dígitos; formata para planilha (ex.: 17.009.00). Nunca usar NCM aqui. */
    private static String formatarCestParaPlanilha(String codigoCest) {
        if (codigoCest == null || codigoCest.isBlank()) return codigoCest;
        String digits = codigoCest.replaceAll("\\D", "");
        if (digits.length() != 7) return codigoCest;
        return digits.substring(0, 2) + "." + digits.substring(2, 5) + "." + digits.substring(5, 7);
    }
}