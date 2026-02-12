package com.empresa.contabil.application.usecase;

import org.springframework.web.multipart.MultipartFile;

public interface CarregarTabelasReferenciaUsecase {

    void carregarNcm(MultipartFile file);

    void carregarCest(MultipartFile file);
}


