package com.empresa.contabil.infrastructure.services;

import com.empresa.contabil.domain.model.Campo;
import com.empresa.contabil.domain.model.Planilha;
import com.empresa.contabil.domain.service.InterpretadorPlanilhaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InterpretadorPlanilhaServiceImpl implements InterpretadorPlanilhaService {

    private static final Set<String> VARIACOES_CODIGONCM = Set.of("codigoncm", "ncm");
    private static final Set<String> VARIACOES_CEST = Set.of("cest", "codigocest");

    /**
     * Normaliza nome de coluna (remove acentos, espaços, lowercase) para comparação.
     */
    private String normalizarParaComparacao(String nome) {
        if (nome == null || nome.isEmpty()) return "";
        String s = Normalizer.normalize(nome, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("\\s+", "")
                .toLowerCase();
        return s;
    }

    /**
     * Retorna o nome canônico da coluna se for uma variação conhecida de CODIGONCM ou CEST.
     */
    private String nomeCanonicoColuna(String nomeOriginal) {
        String norm = normalizarParaComparacao(nomeOriginal);
        if (VARIACOES_CODIGONCM.contains(norm)) return "CODIGONCM";
        if (VARIACOES_CEST.contains(norm)) return "CEST";
        return nomeOriginal;
    }
    
    @Override
    public Planilha lerPlanilha(InputStream arquivo, String nomeArquivo, String tipoArquivo) {
        log.info("Lendo planilha: {} (tipo: {})", nomeArquivo, tipoArquivo);
        
        UUID planilhaId = UUID.randomUUID();
        Planilha planilha = Planilha.builder()
                .id(planilhaId)
                .nomeArquivo(nomeArquivo)
                .tipoArquivo(tipoArquivo)
                .status(Planilha.StatusPlanilha.RECEBIDA)
                .dataUpload(LocalDateTime.now())
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
        
        List<Campo> campos = "CSV".equalsIgnoreCase(tipoArquivo)
                ? lerCsv(arquivo, planilhaId)
                : lerXlsx(arquivo, planilhaId);
        
        campos.forEach(planilha::adicionarCampo);
        
        log.info("Planilha {} lida com sucesso. Linhas: {}, Campos: {}", 
                planilhaId,
                campos.stream().map(Campo::getLinha).distinct().count(),
                campos.size());
        
        return planilha;
    }
    
    private List<Campo> lerCsv(InputStream inputStream, UUID planilhaId) {
        List<Campo> campos = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                log.warn("CSV vazio");
                return campos;
            }
            
            String delimiter = headerLine.contains(";") ? ";" : ",";
            String[] headers = headerLine.split(delimiter, -1);
            
            String line;
            int linha = 2; // 1 = cabeçalho
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(delimiter, -1);
                for (int col = 0; col < headers.length; col++) {
                    String nomeColuna = headers[col].trim();
                    if (nomeColuna.isEmpty()) {
                        continue;
                    }
                    String valor = col < values.length ? values[col] : "";
                    String nomeFinal = nomeCanonicoColuna(nomeColuna);
                    
                    Campo campo = Campo.builder()
                            .id(UUID.randomUUID())
                            .nome(nomeFinal)
                            .tipo("TEXTO")
                            .valor(valor != null ? valor.trim() : "")
                            .linha(linha)
                            .coluna(col + 1)
                            .planilhaId(planilhaId)
                            .build();
                    
                    campos.add(campo);
                }
                linha++;
            }
        } catch (IOException e) {
            log.error("Erro ao ler CSV", e);
            throw new RuntimeException("Erro ao ler CSV: " + e.getMessage(), e);
        }
        
        return campos;
    }
    
    private List<Campo> lerXlsx(InputStream inputStream, UUID planilhaId) {
        List<Campo> campos = new ArrayList<>();
        
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) {
                log.warn("Arquivo XLSX sem abas");
                return campos;
            }
            
            DataFormatter dataFormatter = new DataFormatter();
            Iterator<Row> rowIterator = sheet.rowIterator();
            if (!rowIterator.hasNext()) {
                log.warn("Aba XLSX vazia");
                return campos;
            }
            
            // Cabeçalho
            Row headerRow = rowIterator.next();
            Map<Integer, String> cabecalhos = new LinkedHashMap<>();
            for (Cell cell : headerRow) {
                String header = dataFormatter.formatCellValue(cell).trim();
                if (!header.isEmpty()) {
                    cabecalhos.put(cell.getColumnIndex(), nomeCanonicoColuna(header));
                }
            }
            
            int linha = headerRow.getRowNum() + 2; // próxima linha 1-based para dados
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                
                boolean linhaVazia = true;
                for (Integer colIndex : cabecalhos.keySet()) {
                    Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    String valor = cell != null ? dataFormatter.formatCellValue(cell) : "";
                    if (valor != null && !valor.trim().isEmpty()) {
                        linhaVazia = false;
                    }
                    
                    Campo campo = Campo.builder()
                            .id(UUID.randomUUID())
                            .nome(cabecalhos.get(colIndex))
                            .tipo("TEXTO")
                            .valor(valor != null ? valor.trim() : "")
                            .linha(linha)
                            .coluna(colIndex + 1)
                            .planilhaId(planilhaId)
                            .build();
                    
                    campos.add(campo);
                }
                
                if (linhaVazia) {
                    // opcional: parar se encontrar muitas linhas vazias seguidas
                }
                
                linha++;
            }
        } catch (Exception e) {
            log.error("Erro ao ler XLSX", e);
            throw new RuntimeException("Erro ao ler XLSX: " + e.getMessage(), e);
        }
        
        return campos;
    }
    
    @Override
    public Map<String, Object> extrairDadosEstruturados(Planilha planilha) {
        log.info("Extraindo dados estruturados da planilha: {}", planilha.getId());
        
        if (planilha.getCampos() == null || planilha.getCampos().isEmpty()) {
            return Collections.emptyMap();
        }
        
        // Agrupar por linha em uma lista de mapas {nomeColuna -> valor}
        Map<Integer, Map<String, String>> porLinha = new TreeMap<>();
        for (Campo campo : planilha.getCampos()) {
            porLinha
                    .computeIfAbsent(
                            campo.getLinha() != null ? campo.getLinha() : -1,
                            k -> new LinkedHashMap<>())
                    .put(campo.getNome(), campo.getValor());
        }
        
        List<Map<String, String>> linhas = porLinha.values().stream().collect(Collectors.toList());
        Set<String> colunas = planilha.getCampos().stream()
                .map(Campo::getNome)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("linhas", linhas);
        resultado.put("totalLinhas", linhas.size());
        resultado.put("colunas", colunas);
        
        return resultado;
    }
    
    @Override
    public void validarEstrutura(Planilha planilha) {
        log.info("Validando estrutura da planilha: {}", planilha.getId());
        
        if (planilha.getCampos() == null || planilha.getCampos().isEmpty()) {
            throw new IllegalArgumentException("Planilha sem campos carregados");
        }
        
        Set<String> colunas = planilha.getCampos().stream()
                .map(Campo::getNome)
                .filter(Objects::nonNull)
                .map(String::trim)
                .collect(Collectors.toSet());
        
        List<String> obrigatorias = Arrays.asList("CODIGONCM", "CEST");
        List<String> faltando = obrigatorias.stream()
                .filter(req -> colunas.stream().noneMatch(c -> c.equalsIgnoreCase(req)))
                .collect(Collectors.toList());
        
        if (!faltando.isEmpty()) {
            throw new IllegalArgumentException("Estrutura da planilha inválida. Colunas obrigatórias ausentes: " + faltando);
        }
        
        log.info("Estrutura da planilha {} validada com sucesso. Colunas encontradas: {}", planilha.getId(), colunas);
    }
    
    @Override
    public byte[] gerarExcel(Planilha planilha) {
        if (planilha == null || planilha.getCampos() == null || planilha.getCampos().isEmpty()) {
            throw new IllegalArgumentException("Planilha sem campos para gerar Excel");
        }
        
        Map<Integer, List<Campo>> camposPorLinha = planilha.getCampos().stream()
                .collect(Collectors.groupingBy(c -> c.getLinha() != null ? c.getLinha() : -1, TreeMap::new, Collectors.toList()));
        
        if (camposPorLinha.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma linha encontrada na planilha");
        }
        
        // Ordem das colunas a partir da primeira linha, ordenada por coluna
        List<String> headers = camposPorLinha.values().iterator().next().stream()
                .sorted(Comparator.comparingInt(c -> c.getColuna() != null ? c.getColuna() : 0))
                .map(Campo::getNome)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Planilha");
            
            // Cabeçalho
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
            }
            
            // Linhas de dados
            int rowIndex = 1;
            for (Map.Entry<Integer, List<Campo>> entry : camposPorLinha.entrySet()) {
                Integer linha = entry.getKey();
                if (linha == null || linha < 0) continue;
                
                Map<String, String> valores = entry.getValue().stream()
                        .collect(Collectors.toMap(
                                c -> c.getNome() != null ? c.getNome() : "",
                                c -> c.getValor() != null ? c.getValor() : "",
                                (a, b) -> a,
                                LinkedHashMap::new));
                
                Row row = sheet.createRow(rowIndex++);
                for (int col = 0; col < headers.size(); col++) {
                    Cell cell = row.createCell(col);
                    String valor = valores.getOrDefault(headers.get(col), "");
                    cell.setCellValue(valor);
                }
            }
            
            workbook.write(out);
            log.info("Excel gerado com sucesso para planilha {}. Linhas: {}", planilha.getId(), rowIndex - 1);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Erro ao gerar Excel", e);
            throw new RuntimeException("Erro ao gerar Excel: " + e.getMessage(), e);
        }
    }
}
