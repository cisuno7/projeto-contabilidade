package com.empresa.contabil.infrastructure.excel;

import com.empresa.contabil.domain.model.Campo;
import com.empresa.contabil.domain.model.Planilha;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExcelUpdaterServiceImpl implements ExcelUpdaterService {

    @Override
    public byte[] aplicarCorrecoes(InputStream inputStream, Planilha planilha) {

        try (Workbook workbook = new XSSFWorkbook(inputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.getSheetAt(0);

            // Agrupar campos por linha
            Map<Integer, Map<String, Campo>> camposPorLinha =
                    planilha.getCampos().stream()
                            .collect(Collectors.groupingBy(
                                    Campo::getLinha,
                                    Collectors.toMap(
                                            Campo::getNome,
                                            campo -> campo
                                    )
                            ));

            // Percorrer linhas da planilha (ignorando header)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                int numeroLinhaDominio = i + 1;

                Map<String, Campo> camposLinha = camposPorLinha.get(numeroLinhaDominio);
                if (camposLinha == null) continue;

                atualizarCelula(row, camposLinha, "NCM");
                atualizarCelula(row, camposLinha, "CEST");
                atualizarCelula(row, camposLinha, "DESCRICAO");
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao aplicar correções no Excel", e);
        }
    }

    private void atualizarCelula(Row row,
        Map<String, Campo> camposLinha,
        String nomeCampo) {

Campo campo = camposLinha.get(nomeCampo);
if (campo == null) return;

if (row.getCell(campo.getColuna()) == null) return;

switch (campo.getTipo()) {

case "NUMERO":
if (campo.getValorNumerico() != null) {
row.getCell(campo.getColuna())
   .setCellValue(campo.getValorNumerico().doubleValue());
}
break;

case "BOOLEANO":
row.getCell(campo.getColuna())
.setCellValue(Boolean.parseBoolean(campo.getValor()));
break;

case "DATA":
// Aqui podemos evoluir depois para formatar corretamente
row.getCell(campo.getColuna())
.setCellValue(campo.getValor());
break;

case "TEXTO":
default:
row.getCell(campo.getColuna())
.setCellValue(campo.getValor());
}
}

}
