package com.empresa.contabil.domain.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.empresa.contabil.domain.model.NCM;
import com.empresa.contabil.domain.repository.NCMRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NCMValidatorService {

    private final NCMRepository ncmRepository;

    public Optional<NCM> validar(String codigoNcm) {

        if (codigoNcm == null || codigoNcm.isBlank()) {
            log.warn("Validação NCM falhou: código nulo ou vazio.");
            return Optional.empty();
        }

        String codigoNormalizado = codigoNcm.trim();

        Optional<NCM> resultado = ncmRepository.findByCodigo(codigoNormalizado);

        if (resultado.isEmpty()) {
            log.warn("NCM não encontrado: {}", codigoNormalizado);
        }

        return resultado;
    }
}
