package com.empresa.contabil.infrastructure.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import com.empresa.contabil.application.dto.CESTImportDTO;

@Component
public class CESTSPDFParser {

    private static final Pattern PADRAO_LINHA =
            Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{2})\\s+(\\d{4}\\.\\d{2}\\.\\d{2})\\s+(.+)");

    public List<CESTImportDTO> parse(InputStream inputStream) {

        List<CESTImportDTO> lista = new ArrayList<>();

        try (PDDocument document = PDDocument.load(inputStream)) {

            PDFTextStripper stripper = new PDFTextStripper();
            String texto = stripper.getText(document);

            String[] linhas = texto.split("\\r?\\n");

            for (String linha : linhas) {

                Matcher matcher = PADRAO_LINHA.matcher(linha);

                if (matcher.find()) {

                    String codigoCest = matcher.group(1);
                    String codigoNcm = matcher.group(2);
                    String descricao = matcher.group(3).trim();

                    lista.add(new CESTImportDTO(
                            codigoCest,
                            codigoNcm,
                            descricao
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar PDF CEST", e);
        }

        return lista;
    }
}
