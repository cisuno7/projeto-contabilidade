package com.empresa.contabil.domain.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.empresa.contabil.application.dto.CESTImportDTO;
import com.empresa.contabil.domain.model.CEST;
import com.empresa.contabil.domain.model.NCM;
import com.empresa.contabil.domain.repository.CESTRepository;
import com.empresa.contabil.domain.repository.NCMRepository;
import com.empresa.contabil.infrastructure.parser.CESTSPDFParser;
import com.empresa.contabil.infrastructure.parser.NCMXlsxParser;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarregadorTabelasReferenciaService {

    private static final int BATCH_SIZE = 500;

    private final NCMRepository ncmRepository;
    private final CESTRepository cestRepository;

    private final NCMXlsxParser ncmParser;
    private final CESTSPDFParser cestParser;

    // =============================
    // CARGA NCM
    // =============================
    @Transactional
    public void carregarNCM(InputStream inputStream) {

        List<NCM> listaImportada = ncmParser.parse(inputStream);

        log.info("Iniciando carga de NCM - Total recebido: {}", listaImportada.size());

        List<NCM> paraSalvar = new ArrayList<>();

        for (NCM ncmImportado : listaImportada) {

            Optional<NCM> existenteOpt =
                    ncmRepository.findByCodigo(ncmImportado.getCodigo());

            if (existenteOpt.isPresent()) {

                NCM existente = existenteOpt.get();

                // Atualiza o próprio objeto gerenciado pelo JPA
                atualizarNcm(existente, ncmImportado);

                paraSalvar.add(existente);

            } else {

                paraSalvar.add(ncmImportado);
            }

            if (paraSalvar.size() >= BATCH_SIZE) {
                ncmRepository.saveAll(paraSalvar);
                paraSalvar.clear();
            }
        }

        if (!paraSalvar.isEmpty()) {
            ncmRepository.saveAll(paraSalvar);
        }

        log.info("Carga de NCM finalizada com sucesso.");
    }

    private void atualizarNcm(NCM existente, NCM novo) {

        existente.atualizarDescricao(novo.getDescricao());

        existente.atualizarVigencia(
                novo.getDataInicioVigencia(),
                novo.getDataFimVigencia()
        );

        existente.atualizarAtoLegal(
                novo.getAtoLegalInicio(),
                novo.getNumero(),
                novo.getAno()
        );
    }

    // =============================
    // CARGA CEST
    // =============================
    @Transactional
    public void carregarCEST(InputStream inputStream) {

        List<CESTImportDTO> listaImportada = cestParser.parse(inputStream);

        log.info("Iniciando carga de CEST - Total recebido: {}", listaImportada.size());

        List<CEST> paraSalvar = new ArrayList<>();

        for (CESTImportDTO dto : listaImportada) {

            Optional<NCM> ncmOpt =
                    ncmRepository.findByCodigo(dto.codigoNcm());

            if (ncmOpt.isEmpty()) {

                log.warn("NCM {} não encontrado para CEST {}",
                        dto.codigoNcm(),
                        dto.codigoCest());

                continue;
            }

            NCM ncm = ncmOpt.get();

            Optional<CEST> existenteOpt =
                    cestRepository.findByCodigoAndNcm(dto.codigoCest(), ncm);

            if (existenteOpt.isPresent()) {

                CEST existente = existenteOpt.get();
                existente.atualizarDescricao(dto.descricao());

                paraSalvar.add(existente);

            } else {

                CEST novo = new CEST(
                        dto.codigoCest(),
                        ncm,
                        dto.descricao()
                );

                paraSalvar.add(novo);
            }

            if (paraSalvar.size() >= BATCH_SIZE) {
                cestRepository.saveAll(paraSalvar);
                paraSalvar.clear();
            }
        }

        if (!paraSalvar.isEmpty()) {
            cestRepository.saveAll(paraSalvar);
        }

        log.info("Carga de CEST finalizada com sucesso.");
    }
}
