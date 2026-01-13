# Guia de Instalação do Apache Maven no Windows

## Pré-requisito ✅
Java 17 já está instalado e funcionando!

## Passo 1: Baixar o Apache Maven

1. Acesse o site oficial do Apache Maven: https://maven.apache.org/download.cgi
2. Baixe a versão mais recente do **Binary zip archive** (ex: `apache-maven-3.9.5-bin.zip`)

## Passo 2: Extrair o Maven

1. Crie uma pasta para o Maven (recomendado: `C:\Program Files\Apache\maven`)
2. Extraia o arquivo ZIP baixado para essa pasta
3. Você deve ter uma estrutura como: `C:\Program Files\Apache\maven\apache-maven-3.9.5`

## Passo 3: Configurar Variáveis de Ambiente

### Opção A: Via Interface Gráfica (Recomendado)

1. Pressione `Win + R` e digite `sysdm.cpl`, depois pressione Enter
2. Clique na aba **"Avançado"**
3. Clique em **"Variáveis de Ambiente"**
4. Em **"Variáveis do sistema"**, clique em **"Novo..."**
5. Crie a variável:
   - **Nome da variável:** `MAVEN_HOME`
   - **Valor da variável:** `C:\Program Files\Apache\maven\apache-maven-3.9.5` (ajuste o caminho conforme necessário)
6. Selecione a variável **"Path"** na lista de variáveis do sistema
7. Clique em **"Editar..."**
8. Clique em **"Novo"** e adicione: `%MAVEN_HOME%\bin`
9. Clique em **"OK"** em todas as janelas
10. **IMPORTANTE:** Feche e abra um NOVO terminal/PowerShell para que as mudanças tenham efeito

### Opção B: Via PowerShell (Como Administrador)

Abra o PowerShell como Administrador e execute:

```powershell
# Defina o caminho do Maven (ajuste conforme necessário)
$MAVEN_HOME = "C:\Program Files\Apache\maven\apache-maven-3.9.5"

# Criar variável MAVEN_HOME
[System.Environment]::SetEnvironmentVariable("MAVEN_HOME", $MAVEN_HOME, [System.EnvironmentVariableTarget]::Machine)

# Adicionar ao PATH
$currentPath = [System.Environment]::GetEnvironmentVariable("Path", [System.EnvironmentVariableTarget]::Machine)
$newPath = "$MAVEN_HOME\bin;$currentPath"
[System.Environment]::SetEnvironmentVariable("Path", $newPath, [System.EnvironmentVariableTarget]::Machine)
```

**IMPORTANTE:** Feche e abra um NOVO terminal após executar os comandos.

## Passo 4: Verificar Instalação

Abra um **NOVO** terminal/PowerShell e execute:

```bash
mvn -version
```

Você deve ver uma saída similar a:
```
Apache Maven 3.9.5
Maven home: C:\Program Files\Apache\maven\apache-maven-3.9.5
Java version: 17.0.17
...
```

## Passo 5: Testar no Projeto

Após a instalação bem-sucedida, você pode executar no diretório do backend:

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

## Solução de Problemas

### O comando 'mvn' ainda não é reconhecido
- Certifique-se de fechar e abrir um NOVO terminal após configurar as variáveis
- Verifique se o caminho no PATH está correto
- Reinicie o computador se necessário

### Erro: JAVA_HOME not set
Se você receber este erro, também precisa configurar JAVA_HOME:
1. Encontre o caminho da instalação do Java (geralmente `C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot`)
2. Crie a variável de ambiente `JAVA_HOME` apontando para esse diretório
3. Adicione `%JAVA_HOME%\bin` ao PATH (se ainda não estiver)

## Links Úteis

- Site oficial do Maven: https://maven.apache.org/
- Documentação: https://maven.apache.org/guides/
