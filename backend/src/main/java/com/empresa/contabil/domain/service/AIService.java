package com.empresa.contabil.domain.service;

import java.util.Map;

public interface AIService {
    String processarPreenchimento(String prompt, Map<String, Object> contexto);
    Map<String, Object> interpretarDadosPlanilha(String dadosPlanilha, String instrucoes);
    boolean isDisponivel();
}
