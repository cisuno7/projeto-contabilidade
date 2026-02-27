package com.empresa.contabil.infrastructure.ai;

import com.empresa.contabil.domain.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIServiceImpl implements AIService {

    private final AIConfig aiConfig;
    private final WebClient webClient;

    @Override
    public String processarPreenchimento(String prompt, Map<String, Object> contexto) {
        try {

            log.info("Processando preenchimento com IA");

            if (!isDisponivel()) {
                return null;
            }

            // 🔥 AQUI você pode implementar chamada real depois
            // Por enquanto mock seguro:
            return null;

        } catch (Exception e) {
            log.error("Erro ao processar com IA", e);
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
}