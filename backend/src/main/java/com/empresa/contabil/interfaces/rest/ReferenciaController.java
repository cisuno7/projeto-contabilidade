package com.empresa.contabil.interfaces.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.empresa.contabil.application.usecase.CarregarTabelasReferenciaUsecase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/referencia")
@RequiredArgsConstructor
public class ReferenciaController {

    private final CarregarTabelasReferenciaUsecase useCase;

    @PostMapping("/ncm")
    public ResponseEntity<String> carregarNcm(
            @RequestParam("file") MultipartFile file) {

        useCase.carregarNcm(file);
        return ResponseEntity.ok("NCM carregado com sucesso.");
    }

    @PostMapping("/cest")
    public ResponseEntity<String> carregarCest(
            @RequestParam("file") MultipartFile file) {

        useCase.carregarCest(file);
        return ResponseEntity.ok("CEST carregado com sucesso.");
    }
}
