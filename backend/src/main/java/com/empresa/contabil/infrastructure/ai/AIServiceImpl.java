package com.empresa.contabil.infrastructure.ai;

import com.empresa.contabil.domain.service.AIService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIServiceImpl implements AIService {

    private final AIConfig aiConfig;
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String processarPreenchimento(String prompt, Map<String, Object> contexto) {
        try {

            log.debug("Chamada à API de IA (chat completions)");

            if (!isDisponivel()) {
                return null;
            }

            String model = aiConfig.getModel() != null ? aiConfig.getModel() : "gpt-4o-mini";
            ArrayNode messages = objectMapper.createArrayNode();
            messages.add(objectMapper.createObjectNode()
                    .put("role", "user")
                    .put("content", prompt));

            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", model);
            body.set("messages", messages);
            body.put("temperature", 0.2);

            int timeoutMs = aiConfig.getTimeout() != null ? aiConfig.getTimeout() : 30000;

            String raw = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofMillis(timeoutMs));

            if (raw == null || raw.isBlank()) {
                return null;
            }

            JsonNode root = objectMapper.readTree(raw);
            String content = root.path("choices").path(0).path("message").path("content").asText(null);
            return content != null && !content.isBlank() ? content.trim() : null;

        } catch (Exception e) {
            log.warn("Erro ao processar com IA: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Map<String, Object> interpretarDadosPlanilha(String dadosPlanilha, String instrucoes) {
        try {
            log.info("Interpretando dados da planilha com IA");
            return new HashMap<>();
        } catch (Exception e) {
            log.error("Erro ao interpretar dados da planilha", e);
            return new HashMap<>();
        }
    }

    @Override
    public boolean isDisponivel() {
        return aiConfig.getApiKey() != null && !aiConfig.getApiKey().isEmpty();
    }

    @Override
    public String sugerirNcm(String nomeProduto, String grupo) {

        if (!isDisponivel()) {
            return null;
        }

        try {

            String prompt = """
                    Você é um especialista fiscal brasileiro.

                    Retorne somente o código NCM correto (8 dígitos numéricos).
                    Não explique.
                    Não escreva texto adicional.

                    Produto: %s
                    Grupo: %s
                    """.formatted(
                    nomeProduto != null ? nomeProduto : "",
                    grupo != null ? grupo : ""
            );

            String resposta = processarPreenchimento(prompt, Map.of());

            if (resposta == null || resposta.isBlank()) {
                return null;
            }

            String somenteNumeros = resposta.replaceAll("\\D", "");

            if (somenteNumeros.length() == 8) {
                return somenteNumeros;
            }

            return null;

        } catch (Exception e) {
            log.error("Erro ao sugerir NCM via IA", e);
            return null;
        }
    }

    @Override
    public void enrichirAnaliseCorrelacaoIA(List<Map<String, Object>> pendentes) {
        if (!isDisponivel() || pendentes == null || pendentes.isEmpty()) {
            return;
        }
        try {
            int cap = Math.min(pendentes.size(), 30);
            List<Map<String, Object>> sub = pendentes.subList(0, cap);
            String dados = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sub);
            String prompt = """
                    Você é um analista fiscal brasileiro. O cliente NÃO pode alterar os nomes dos produtos na planilha.

                    Para cada item do JSON, sugira em 1 ou 2 frases o que cadastrar na tabela "produto" do sistema (pode manter o mesmo nome do cliente) para permitir classificação NCM/CEST nas próximas importações.
                    Indique prioridade de enriquecimento da base: baixa, media ou alta.

                    Retorne APENAS um JSON válido neste formato (sem markdown, sem texto extra):
                    {"itens":[{"linha":<numero>,"insight":"<texto>","prioridade":"baixa|media|alta"}]}

                    Dados:
                    %s
                    """.formatted(dados);

            String resp = processarPreenchimento(prompt, Map.of());
            if (resp == null || resp.isBlank()) {
                return;
            }
            String cleaned = resp.trim();
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replaceFirst("^```(?:json)?\\s*", "");
                int end = cleaned.lastIndexOf("```");
                if (end > 0) {
                    cleaned = cleaned.substring(0, end).trim();
                }
            }
            JsonNode root = objectMapper.readTree(cleaned);
            for (JsonNode item : root.path("itens")) {
                int linha = item.path("linha").asInt(-1);
                String insight = item.path("insight").asText("");
                String prioridade = item.path("prioridade").asText("");
                if (linha < 0) {
                    continue;
                }
                for (Map<String, Object> p : pendentes) {
                    Object nl = p.get("linha");
                    if (nl instanceof Number && ((Number) nl).intValue() == linha) {
                        p.put("ia_insight", insight);
                        p.put("ia_prioridade", prioridade);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Não foi possível enriquecer relatório com IA: {}", e.getMessage());
        }
    }
}