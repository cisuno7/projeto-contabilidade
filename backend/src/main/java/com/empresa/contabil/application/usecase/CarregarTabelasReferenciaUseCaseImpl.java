package com.empresa.contabil.application.usecase;

import java.io.InputStream;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.empresa.contabil.domain.service.CarregadorTabelasReferenciaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CarregarTabelasReferenciaUseCaseImpl
        implements CarregarTabelasReferenciaUsecase {

    private final CarregadorTabelasReferenciaService service;

    @Override
    public void carregarNcm(MultipartFile file) {

        try (InputStream inputStream = file.getInputStream()) {

            service.carregarNCM(inputStream);

            log.info("Upload NCM concluído com sucesso.");

        } catch (Exception e) {
            log.error("Erro ao carregar NCM", e);
            throw new RuntimeException("Erro ao processar arquivo NCM.");
        }
    }

    @Override
    public void carregarCest(MultipartFile file) {

        try (InputStream inputStream = file.getInputStream()) {

            service.carregarCEST(inputStream);

            log.info("Upload CEST concluído com sucesso.");

        } catch (Exception e) {
            log.error("Erro ao carregar CEST", e);
            throw new RuntimeException("Erro ao processar arquivo CEST.");
        }
    }
}
