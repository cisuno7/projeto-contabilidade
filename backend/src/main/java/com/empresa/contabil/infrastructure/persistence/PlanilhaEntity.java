package com.empresa.contabil.infrastructure.persistence;

import com.empresa.contabil.domain.model.Planilha;
import com.empresa.contabil.domain.model.Planilha.StatusPlanilha;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "spreadsheets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanilhaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(name = "processed_storage_path")
    private String processedStoragePath;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", columnDefinition = "status_processamento")
    private StatusPlanilha status;

    @Column(name = "processing_logs")
    private String processingLogs;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ai_metadata")
    private JsonNode aiMetadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "processed_by")
    private UUID processedBy;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = StatusPlanilha.RECEBIDA;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
