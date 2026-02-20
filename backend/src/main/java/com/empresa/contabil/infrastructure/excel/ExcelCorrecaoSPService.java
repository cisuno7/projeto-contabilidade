package com.empresa.contabil.infrastructure.excel;

import java.io.InputStream;

public interface ExcelCorrecaoSPService {

    byte[] corrigirExcelSP(InputStream inputStream) throws Exception;

}
