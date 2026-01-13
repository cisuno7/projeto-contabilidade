package com.empresa.contabil.interfaces.rest;

import com.empresa.contabil.application.dto.PlanilhaDTO;
import com.empresa.contabil.application.dto.ProcessarPlanilhaRequest;
import com.empresa.contabil.application.dto.UploadPlanilhaRequest;
import com.empresa.contabil.application.usecase.BaixarPlanilhaUseCase;
import com.empresa.contabil.application.usecase.ProcessarPlanilhaUseCase;
import com.empresa.contabil.application.usecase.UploadPlanilhaUseCase;
import com.empresa.contabil.domain.repository.PlanilhaRepository;
import com.empresa.contabil.interfaces.mapper.PlanilhaDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/planilhas")
@RequiredArgsConstructor
public class PlanilhaController {
    
    private final UploadPlanilhaUseCase uploadPlanilhaUseCase;
    private final ProcessarPlanilhaUseCase processarPlanilhaUseCase;
    private final BaixarPlanilhaUseCase baixarPlanilhaUseCase;
    private final PlanilhaRepository planilhaRepository;
    private final PlanilhaDTOMapper planilhaDTOMapper;
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlanilhaDTO> uploadPlanilha(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam("clienteId") UUID clienteId,
            @RequestParam(value = "nomeArquivo", required = false) String nomeArquivo) {
        
        UploadPlanilhaRequest request = UploadPlanilhaRequest.builder()
                .clienteId(clienteId)
                .nomeArquivo(nomeArquivo != null ? nomeArquivo : arquivo.getOriginalFilename())
                .build();
        
        PlanilhaDTO planilha = uploadPlanilhaUseCase.executar(request, arquivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(planilha);
    }
    
    @PostMapping("/processar")
    public ResponseEntity<PlanilhaDTO> processarPlanilha(@RequestBody ProcessarPlanilhaRequest request) {
        PlanilhaDTO planilha = processarPlanilhaUseCase.executar(request);
        return ResponseEntity.ok(planilha);
    }
    
    @GetMapping
    public ResponseEntity<List<PlanilhaDTO>> listarPlanilhas() {
        List<PlanilhaDTO> planilhas = planilhaRepository.buscarTodos().stream()
                .map(planilhaDTOMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(planilhas);
    }
    
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> baixarPlanilha(@PathVariable UUID id) {
        byte[] arquivo = baixarPlanilhaUseCase.executar(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "planilha.xlsx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(arquivo);
    }
}
