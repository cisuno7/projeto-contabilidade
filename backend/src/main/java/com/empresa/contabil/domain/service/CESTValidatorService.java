package com.empresa.contabil.domain.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.empresa.contabil.domain.model.CEST;
import com.empresa.contabil.domain.model.NCM;
import com.empresa.contabil.domain.repository.CESTRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CESTValidatorService {

    private final CESTRepository cestRepository;

    public Optional<CEST> validar(String codigoCest, NCM ncm) {

        if (codigoCest == null || codigoCest.isBlank() || ncm == null) {
            return Optional.empty();
        }

        return cestRepository.findByCodigoAndNcm(codigoCest, ncm);
    }
}
