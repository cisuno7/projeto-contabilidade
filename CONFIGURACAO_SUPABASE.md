# 🔐 Configuração do Supabase - Guia Completo

## 📋 Como Conectar o Supabase ao Backend Java

### 1. Obter Credenciais do Supabase

1. Acesse o [painel do Supabase](https://app.supabase.com)
2. Selecione seu projeto
3. Vá em **Project Settings** → **Database**
4. Anote as seguintes informações:
   - **Host**: `db.xxxxx.supabase.co`
   - **Port**: `5432`
   - **Database**: `postgres`
   - **User**: `postgres`
   - **Password**: (sua senha do banco)

### 2. Configuração no Projeto

#### Opção 1: Variáveis de Ambiente (Recomendado - Mais Seguro)

**Windows (PowerShell):**
```powershell
$env:DB_URL="jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres?sslmode=require"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="sua_senha_aqui"
$env:SPRING_PROFILES_ACTIVE="prod"
```

**Windows (CMD):**
```cmd
set DB_URL=jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres?sslmode=require
set DB_USERNAME=postgres
set DB_PASSWORD=sua_senha_aqui
set SPRING_PROFILES_ACTIVE=prod
```

**Linux/Mac:**
```bash
export DB_URL="jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres?sslmode=require"
export DB_USERNAME="postgres"
export DB_PASSWORD="sua_senha_aqui"
export SPRING_PROFILES_ACTIVE="prod"
```

#### Opção 2: Arquivo application.yml (Desenvolvimento)

Edite o arquivo `backend/src/main/resources/application.yml`:

1. Altere o profile ativo para `prod`:
```yaml
spring:
  profiles:
    active: prod  # Mude de 'dev' para 'prod'
```

2. Configure as credenciais (ou use variáveis de ambiente):
```yaml
spring:
  config:
    activate:
      on-profile: prod
  
  datasource:
    url: ${DB_URL:jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres?sslmode=require}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:sua_senha_aqui}
    driver-class-name: org.postgresql.Driver
```

### 3. Ativar o Profile de Produção

Para usar o Supabase, você precisa ativar o profile `prod`:

**Opção A: Variável de Ambiente (Recomendado)**
```powershell
# Windows PowerShell
$env:SPRING_PROFILES_ACTIVE="prod"
```

```bash
# Linux/Mac
export SPRING_PROFILES_ACTIVE="prod"
```

**Opção B: No application.yml**
```yaml
spring:
  profiles:
    active: prod  # Mude aqui
```

**Opção C: Ao iniciar a aplicação**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### 4. Verificar Conexão

1. Inicie o backend:
```bash
cd backend
mvn spring-boot:run
```

2. Verifique os logs. Você deve ver:
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

3. Se houver erro de conexão, verifique:
   - ✅ Credenciais corretas
   - ✅ URL do Supabase correta
   - ✅ Senha sem caracteres especiais mal codificados
   - ✅ SSL habilitado (`sslmode=require`)

### 5. Estrutura do Banco de Dados

O Spring Boot (com `ddl-auto: update`) vai:
- Criar as tabelas automaticamente baseadas nas suas Entidades JPA
- Atualizar o esquema quando necessário

**Importante:** 
- Se você já tem tabelas no Supabase, o Hibernate vai tentar sincronizar
- Para produção, considere usar `ddl-auto: validate` para evitar mudanças acidentais

### 6. Troubleshooting

#### Erro: "Connection refused"
- Verifique se a URL do Supabase está correta
- Verifique se o Supabase está ativo

#### Erro: "Authentication failed"
- Verifique usuário e senha
- A senha do Supabase pode ter caracteres especiais - use variáveis de ambiente

#### Erro: "SSL required"
- Certifique-se de que a URL tem `?sslmode=require`
- Exemplo: `jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres?sslmode=require`

### 7. Formato da URL JDBC

```
jdbc:postgresql://[HOST]:[PORT]/[DATABASE]?sslmode=require
```

Exemplo:
```
jdbc:postgresql://db.qilwtajqgonurweqlznd.supabase.co:5432/postgres?sslmode=require
```

### 8. Segurança

⚠️ **NUNCA commite credenciais no Git!**

- ✅ Use variáveis de ambiente
- ✅ Use arquivos `.env` (adicionados ao `.gitignore`)
- ✅ Não coloque senhas diretamente no `application.yml` no código

### 9. Comparação: Dev vs Prod

| Aspecto | Dev (H2) | Prod (Supabase) |
|---------|----------|-----------------|
| Database | H2 (memória) | PostgreSQL (Supabase) |
| Persistência | Dados perdidos ao reiniciar | Dados permanentes |
| SSL | Não necessário | Obrigatório (`sslmode=require`) |
| Uso | Desenvolvimento local | Produção/Testes reais |

---

**Dúvidas?** Verifique os logs do Spring Boot para mais detalhes sobre a conexão.
