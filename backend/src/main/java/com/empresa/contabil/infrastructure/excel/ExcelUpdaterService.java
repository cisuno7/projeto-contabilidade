package com.empresa.contabil.infrastructure.excel;

import com.empresa.contabil.domain.model.Planilha;

import java.io.InputStream;

public interface ExcelUpdaterService {

    byte[] aplicarCorrecoes(InputStream inputStream, Planilha planilha);
}
