package com.empresa.contabil.infrastructure.filestorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {
    
    private final Path rootLocation;
    
    public FileStorageServiceImpl(@Value("${file.storage.path:./uploads}") String storagePath) {
        this.rootLocation = Paths.get(storagePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
            log.info("Diretório de armazenamento criado/verificado: {}", this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o diretório de armazenamento", e);
        }
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
            
            // Garantir nome único
            String nomeComId = UUID.randomUUID() + "_" + nomeFinal;
            Path destino = this.rootLocation.resolve(nomeComId);
            
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("Arquivo salvo: {}", destino);
            return nomeComId;
            
        } catch (IOException e) {
            log.error("Erro ao salvar arquivo", e);
            throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage(), e);
        }
    }
    
    @Override
    public InputStream ler(String caminhoArquivo) {
        try {
            Path arquivo = this.rootLocation.resolve(caminhoArquivo).normalize();
            
            if (!arquivo.startsWith(this.rootLocation.normalize())) {
                throw new RuntimeException("Acesso negado: caminho fora do diretório base");
            }
            
            if (!Files.exists(arquivo)) {
                throw new RuntimeException("Arquivo não encontrado: " + caminhoArquivo);
            }
            
            return Files.newInputStream(arquivo);
            
        } catch (IOException e) {
            log.error("Erro ao ler arquivo: {}", caminhoArquivo, e);
            throw new RuntimeException("Erro ao ler arquivo: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deletar(String caminhoArquivo) {
        try {
            Path arquivo = this.rootLocation.resolve(caminhoArquivo).normalize();
            
            if (!arquivo.startsWith(this.rootLocation.normalize())) {
                throw new RuntimeException("Acesso negado: caminho fora do diretório base");
            }
            
            if (Files.exists(arquivo)) {
                Files.delete(arquivo);
                log.info("Arquivo deletado: {}", arquivo);
            }
            
        } catch (IOException e) {
            log.error("Erro ao deletar arquivo: {}", caminhoArquivo, e);
            throw new RuntimeException("Erro ao deletar arquivo: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Path obterCaminhoCompleto(String caminhoArquivo) {
        return this.rootLocation.resolve(caminhoArquivo).normalize();
    }
    
    @Override
    public boolean existe(String caminhoArquivo) {
        Path arquivo = this.rootLocation.resolve(caminhoArquivo).normalize();
        return Files.exists(arquivo);
    }
}
