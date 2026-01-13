package com.empresa.contabil.application.usecase;

import java.util.UUID;

public interface BaixarPlanilhaUseCase {
    byte[] executar(UUID planilhaId);
}
