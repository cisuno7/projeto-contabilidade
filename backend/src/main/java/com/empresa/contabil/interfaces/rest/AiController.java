package com.empresa.contabil.interfaces.rest;

import com.empresa.contabil.domain.service.AIService;
import com.empresa.contabil.infrastructure.ai.AIConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {

    private final AIConfig aiConfig;
    private final AIService aiService;
    private final WebClient webClient;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        boolean configurada = aiConfig.getApiKey() != null && !aiConfig.getApiKey().isBlank();
        boolean disponivel = aiService.isDisponivel();

        if (!configurada) {
            return ResponseEntity.ok(Map.of(
                    "configurada", false,
                    "disponivel", false,
                    "valida", false,
                    "mensagem", "AI_API_KEY nao configurada. Defina a variavel de ambiente ou use o .env."
            ));
        }

        return ResponseEntity.ok(Map.of(
                "configurada", true,
                "disponivel", disponivel,
                "mensagem", disponivel ? "API key configurada e pronta para uso." : "API key presente mas servico indisponivel."
        ));
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testarApiKey() {
        if (aiConfig.getApiKey() == null || aiConfig.getApiKey().isBlank()) {
            return ResponseEntity.ok(Map.of(
                    "valida", false,
                    "erro", "AI_API_KEY nao configurada"
            ));
        }

        try {
            webClient.get()
                    .uri("/models")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return ResponseEntity.ok(Map.of(
                    "valida", true,
                    "mensagem", "API key funcionando corretamente."
            ));
        } catch (WebClientResponseException.Unauthorized e) {
            log.warn("API key invalida ou expirada: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                    "valida", false,
                    "erro", "API key invalida ou expirada (401)"
            ));
        } catch (WebClientResponseException e) {
            log.warn("Erro ao validar API key: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.ok(Map.of(
                    "valida", false,
                    "erro", "Erro HTTP " + e.getStatusCode() + ": " + e.getStatusText()
            ));
        } catch (Exception e) {
            log.error("Erro ao testar API key", e);
            return ResponseEntity.ok(Map.of(
                    "valida", false,
                    "erro", e.getMessage()
            ));
        }
    }
}
