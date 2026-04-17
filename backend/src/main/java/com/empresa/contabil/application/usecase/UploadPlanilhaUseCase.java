package com.empresa.contabil.application.usecase;

import com.empresa.contabil.application.dto.PlanilhaDTO;
import com.empresa.contabil.application.dto.UploadPlanilhaRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UploadPlanilhaUseCase {
    PlanilhaDTO executar(UploadPlanilhaRequest request, MultipartFile arquivo);
}
