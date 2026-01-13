package com.empresa.contabil.infrastructure.filestorage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Path;

public interface FileStorageService {
    String salvar(MultipartFile arquivo, String nomeArquivo);
    InputStream ler(String caminhoArquivo);
    void deletar(String caminhoArquivo);
    Path obterCaminhoCompleto(String caminhoArquivo);
    boolean existe(String caminhoArquivo);
}
