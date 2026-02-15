# Carrega variaveis do .env e inicia o backend
$envFile = Join-Path $PSScriptRoot ".env"
if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        if ($_ -match '^([^#][^=]+)=(.*)$') {
            $key = $matches[1].Trim()
            $value = $matches[2].Trim()
            Set-Item -Path "Env:$key" -Value $value -Force
        }
    }
    Write-Host "Variaveis do .env carregadas."
} else {
    Write-Warning ".env nao encontrado em $envFile"
}
$profile = if ($args[0]) { $args[0] } else { "prod" }
mvn spring-boot:run "-Dspring-boot.run.profiles=$profile"
