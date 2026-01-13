package com.empresa.contabil.interfaces.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas() {
        // TODO: Implementar lógica para obter estatísticas
        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("totalPlanilhas", 0);
        estatisticas.put("planilhasProcessadas", 0);
        estatisticas.put("planilhasEmProcessamento", 0);
        estatisticas.put("taxaSucesso", 0.0);
        
        return ResponseEntity.ok(estatisticas);
    }
}
