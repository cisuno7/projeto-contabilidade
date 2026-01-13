package com.empresa.contabil.domain.service;

import com.empresa.contabil.domain.model.Planilha;

import java.io.InputStream;
import java.util.Map;

public interface InterpretadorPlanilhaService {
    Planilha lerPlanilha(InputStream arquivo, String nomeArquivo, String tipoArquivo);
    Map<String, Object> extrairDadosEstruturados(Planilha planilha);
    void validarEstrutura(Planilha planilha);
}
