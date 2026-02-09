# Configuração da API OpenAI

A chave da API OpenAI foi configurada no arquivo `application-local.yml`.

## Como usar:

### Opção 1: Usar o arquivo application-local.yml (JÁ CONFIGURADO ✅)
O Spring Boot carrega automaticamente o arquivo `application-local.yml` se ele existir.
A chave já está configurada neste arquivo.

**Apenas execute:**
```bash
mvn spring-boot:run
```

### Opção 2: Usar variável de ambiente
Se preferir usar variável de ambiente, execute o script PowerShell:

```powershell
cd backend
.\set-env.ps1
mvn spring-boot:run
```

Ou configure manualmente no PowerShell:
```powershell
$env:AI_API_KEY="sua-chave-aqui"
```

## ⚠️ Importante:
- O arquivo `application-local.yml` está no `.gitignore` e **NÃO será commitado** no Git
- Nunca compartilhe sua chave de API publicamente
- Se precisar regenerar a chave, acesse: https://platform.openai.com/api-keys

## Verificação:
Para verificar se a chave está sendo carregada, procure no log da aplicação por:
```
ai.service.api-key=sk-...
```

Se aparecer, está funcionando! ✅
