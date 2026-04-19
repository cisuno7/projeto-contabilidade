package com.empresa.contabil.infrastructure.filestorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Armazena arquivos no Supabase Storage (bucket).
 * Ativo quando file.storage.type=supabase.
 */
@ConditionalOnProperty(name = "file.storage.type", havingValue = "supabase")
@org.springframework.stereotype.Service
@Slf4j
public class FileStorageServiceSupabaseImpl implements FileStorageService {

    private static final Pattern NAO_SEGURO_STORAGE = Pattern.compile("[^a-zA-Z0-9._-]");
    private static final Pattern MULTIPLOS_UNDERSCORE = Pattern.compile("_{2,}");

    private final WebClient webClient;
    private final String bucket;

    public FileStorageServiceSupabaseImpl(
            @Value("${file.storage.supabase.url}") String baseUrl,
            @Value("${file.storage.supabase.service-role-key}") String serviceRoleKey,
            @Value("${file.storage.supabase.bucket:planilhas}") String bucket) {
        this.bucket = bucket;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl.endsWith("/") ? baseUrl + "storage/v1" : baseUrl + "/storage/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + serviceRoleKey)
                .defaultHeader("apikey", serviceRoleKey)
                .build();
        log.info("FileStorage Supabase ativo: bucket={}", bucket);
    }

    @Override
    public String salvar(MultipartFile arquivo, String nomeArquivo) {
        try {
            if (arquivo.isEmpty()) {
                throw new RuntimeException("Arquivo vazio não pode ser salvo");
            }
            String nomeFinal = nomeArquivo != null ? nomeArquivo : arquivo.getOriginalFilename();
            if (nomeFinal == null || nomeFinal.isEmpty()) {
                nomeFinal = UUID.randomUUID().toString();
            }
            String key = UUID.randomUUID() + "_" + sanitizarNomeParaStorage(nomeFinal);
            byte[] bytes = arquivo.getBytes();
            upload(key, bytes, arquivo.getContentType());
            log.info("Arquivo enviado ao Supabase Storage: {}", key);
            return key;
        } catch (IOException e) {
            log.error("Erro ao salvar arquivo", e);
            throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage(), e);
        }
    }

    @Override
    public String salvarBytes(byte[] bytes, String nomeArquivo) {
        if (bytes == null || bytes.length == 0) {
            throw new RuntimeException("Conteúdo vazio não pode ser salvo");
        }
        String nomeFinal = nomeArquivo != null && !nomeArquivo.isEmpty()
                ? nomeArquivo
                : UUID.randomUUID().toString() + ".xlsx";
        if (!nomeFinal.toLowerCase().endsWith(".xlsx") && !nomeFinal.toLowerCase().endsWith(".xls")) {
            nomeFinal = nomeFinal + ".xlsx";
        }
        String key = UUID.randomUUID() + "_" + sanitizarNomeParaStorage(nomeFinal);
        upload(key, bytes, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        log.info("Arquivo (bytes) enviado ao Supabase Storage: {}", key);
        return key;
    }

    @Override
    public InputStream ler(String caminhoArquivo) {
        byte[] bytes = webClient.get()
                .uri("/object/{bucket}/{path}", bucket, caminhoArquivo)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
        if (bytes == null) {
            throw new RuntimeException("Arquivo não encontrado: " + caminhoArquivo);
        }
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void deletar(String caminhoArquivo) {
        try {
            webClient.delete()
                    .uri("/object/{bucket}/{path}", bucket, caminhoArquivo)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.info("Arquivo removido do Supabase Storage: {}", caminhoArquivo);
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Arquivo já não existe no bucket: {}", caminhoArquivo);
        }
    }

    @Override
    public Path obterCaminhoCompleto(String caminhoArquivo) {
        return Path.of("supabase", bucket, caminhoArquivo);
    }

    @Override
    public boolean existe(String caminhoArquivo) {
        try {
            webClient.get()
                    .uri("/object/{bucket}/{path}", bucket, caminhoArquivo)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        }
    }

    private void upload(String key, byte[] body, String contentType) {
        if (body == null) {
            throw new RuntimeException("Conteúdo do arquivo não pode ser nulo");
        }
        String type = contentType != null && !contentType.isBlank()
                ? contentType
                : "application/octet-stream";
        webClient.post()
                .uri("/object/{bucket}/{path}", bucket, key)
                .contentType(MediaType.parseMediaType(type))
                .header("x-upsert", "true")
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    /**
     * O Storage do Supabase (S3-like) rejeita vários caracteres no path do objeto (400).
     * Ex.: circunflexo, colchetes, chaves, pipe, asterisco, interrogação, aspas, dois-pontos, barra invertida.
     */
    static String sanitizarNomeParaStorage(String nomeOriginal) {
        if (nomeOriginal == null || nomeOriginal.isBlank()) {
            return "arquivo.bin";
        }
        String n = Normalizer.normalize(nomeOriginal.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        int lastDot = n.lastIndexOf('.');
        String base = lastDot > 0 ? n.substring(0, lastDot) : n;
        String ext = lastDot > 0 ? n.substring(lastDot) : "";
        String safe = NAO_SEGURO_STORAGE.matcher(base).replaceAll("_");
        safe = MULTIPLOS_UNDERSCORE.matcher(safe).replaceAll("_");
        safe = safe.replaceAll("^_+|_+$", "");
        if (safe.isEmpty()) {
            safe = "arquivo";
        }
        if (safe.length() > 150) {
            safe = safe.substring(0, 150);
        }
        String extNorm = ext.isEmpty() ? "" : ext.toLowerCase();
        if (extNorm.length() > 10) {
            extNorm = extNorm.substring(0, 10);
        }
        return safe + extNorm;
    }
}
