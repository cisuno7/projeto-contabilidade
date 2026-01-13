package com.empresa.contabil.application.usecase;

import com.empresa.contabil.application.dto.PlanilhaDTO;
import com.empresa.contabil.application.dto.ProcessarPlanilhaRequest;

public interface ProcessarPlanilhaUseCase {
    PlanilhaDTO executar(ProcessarPlanilhaRequest request);
}
