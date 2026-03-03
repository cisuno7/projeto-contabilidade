package com.empresa.contabil.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private UUID id;
    private String name;
    private String documentNumber;
    private String estado;      // UF do estado
    private String regime;      // Simples Nacional, Lucro Real, Lucro Presumido
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void atualizar(String name, String estado, String regime) {
        this.name = name;
        this.estado = estado;
        this.regime = regime;
        this.updatedAt = LocalDateTime.now();
    }

    public void desativar() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void ativar() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }
}
