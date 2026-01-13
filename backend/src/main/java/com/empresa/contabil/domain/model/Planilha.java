package com.empresa.contabil.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Planilha {
    private UUID id;
    private String nomeArquivo;
    private String tipoArquivo; // XLSX, CSV
    private String caminhoArquivo;
    private StatusPlanilha status;
    private UUID clienteId;
    private LocalDateTime dataUpload;
    private LocalDateTime dataProcessamento;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    
    @Builder.Default
    private List<Campo> campos = new ArrayList<>();
    
    public enum StatusPlanilha {
        UPLOADED,
        PROCESSANDO,
        PROCESSADA,
        ERRO,
        CONCLUIDA
    }
    
    public void iniciarProcessamento() {
        this.status = StatusPlanilha.PROCESSANDO;
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public void finalizarProcessamento() {
        this.status = StatusPlanilha.PROCESSADA;
        this.dataProcessamento = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public void marcarComErro() {
        this.status = StatusPlanilha.ERRO;
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public void concluir() {
        this.status = StatusPlanilha.CONCLUIDA;
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public void adicionarCampo(Campo campo) {
        if (this.campos == null) {
            this.campos = new ArrayList<>();
        }
        this.campos.add(campo);
    }
}
