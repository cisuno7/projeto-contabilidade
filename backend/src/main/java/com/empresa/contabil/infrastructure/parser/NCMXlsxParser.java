package com.empresa.contabil.infrastructure.parser;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.empresa.contabil.domain.model.NCM;

@Component
public class NCMXlsxParser {

    public List<NCM> parse(InputStream inputStream) {

        List<NCM> lista = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (int i = 5; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                String codigo = row.getCell(0).toString().trim();
                String descricao = row.getCell(1).toString().trim();

                LocalDate dataInicio = LocalDate.parse(row.getCell(2).toString().trim(), formatter);
                LocalDate dataFim = LocalDate.parse(row.getCell(3).toString().trim(), formatter);

                String atoLegal = row.getCell(4).toString().trim();

                Integer numero = Integer.valueOf(row.getCell(5).toString().split("\\.")[0]);
                Integer ano = Integer.valueOf(row.getCell(6).toString().split("\\.")[0]);

                NCM ncm = new NCM(
                        codigo,
                        descricao,
                        dataInicio,
                        dataFim,
                        atoLegal,
                        numero,
                        ano
                );

                lista.add(ncm);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar planilha NCM", e);
        }

        return lista;
    }
}
