# Script para configurar variáveis de ambiente no PowerShell
# Execute este script antes de rodar a aplicação: .\set-env.ps1

# Carrega variáveis do arquivo .env
if (Test-Path .env) {
    Get-Content .env | ForEach-Object {
        if ($_ -match '^\s*([^#][^=]+)=(.*)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
            Write-Host "✓ Configurado: $name" -ForegroundColor Green
        }
    }
    Write-Host "`n✅ Variáveis de ambiente configuradas com sucesso!" -ForegroundColor Green
    Write-Host "Agora você pode executar: mvn spring-boot:run`n" -ForegroundColor Yellow
} else {
    Write-Host "❌ Arquivo .env não encontrado!" -ForegroundColor Red
}
