# Sistema de Automação Inteligente para Preenchimento de Planilhas Contábeis

Sistema desenvolvido seguindo **Clean Architecture** com **DDD (Domain-Driven Design)** e princípios **SOLID**.

## 🚀 Primeiros Passos

**Não tem uma conta?** Crie uma conta diretamente no frontend da aplicação!

1. Acesse a aplicação no navegador
2. Procure pela opção "Registrar-se" ou "Criar Conta"
3. Preencha username, email e senha
4. Faça login e comece a usar o sistema

O sistema possui autenticação completa com JWT, então sua conta será segura e você poderá acessar todas as funcionalidades.

## 🏗️ Arquitetura

O projeto está estruturado em camadas bem definidas:

### Backend (Java Spring Boot)

```
src/main/java/com/empresa/contabil/
├── domain/           # Camada de Domínio (Core Business Logic)
│   ├── model/        # Entidades de domínio
│   ├── repository/   # Interfaces de repositório
│   └── service/      # Interfaces de serviços de domínio
├── application/      # Camada de Aplicação (Use Cases)
│   ├── usecase/      # Casos de uso
│   └── dto/          # Data Transfer Objects
├── infrastructure/   # Camada de Infraestrutura
│   ├── persistence/  # Implementações JPA/Hibernate
│   ├── filestorage/  # Serviço de armazenamento de arquivos
│   ├── ai/           # Integração com APIs de IA
│   └── config/       # Configurações do Spring
└── interfaces/       # Camada de Interface
    ├── rest/         # Controllers REST
    └── mapper/       # Conversão Entity <-> DTO
```

### Frontend (React + TypeScript + Vite)

```
src/
├── pages/        # Páginas principais (Dashboard, Upload, Histórico)
├── components/   # Componentes reutilizáveis
├── services/     # Serviços de API
├── types/        # Definições TypeScript
├── hooks/        # Custom React Hooks
└── utils/        # Funções utilitárias
```

## 🚀 Tecnologias

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **PostgreSQL** (produção) / **H2** (desenvolvimento)
- **Apache POI** (manipulação de Excel)
- **Lombok**
- **Maven**

### Frontend
- **React 19**
- **TypeScript**
- **Vite**
- **React Router**
- **Axios**

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- Node.js 18+ e npm
- PostgreSQL (para produção)
- Variáveis de ambiente configuradas (ver seção de configuração)

## ⚙️ Configuração

### Backend

1. Clone o repositório
2. Configure as variáveis de ambiente no `application.yml` ou através de variáveis de ambiente:

```bash
# Para desenvolvimento (H2)
# Não é necessário configuração adicional

# Para produção (PostgreSQL)
export DB_URL=jdbc:postgresql://localhost:5432/contabil_db
export DB_USERNAME=postgres
export DB_PASSWORD=postgres

# Configuração de IA
export AI_API_KEY=sua_chave_api
export AI_BASE_URL=https://api.openai.com/v1
export AI_MODEL=gpt-4

# Armazenamento de arquivos
export FILE_STORAGE_PATH=./uploads
```

3. Execute o backend:

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

O backend estará disponível em: `http://localhost:8080/api`

### Frontend

1. Instale as dependências:

```bash
cd frontend
npm install
```

2. Execute o frontend:

```bash
npm run dev
```

O frontend estará disponível em: `http://localhost:5173`

## 📚 Endpoints da API

### Autenticação
- `POST /api/auth/register` - Criar nova conta de usuário
- `POST /api/auth/login` - Fazer login e obter token JWT

### Planilhas (requer autenticação)

- `POST /api/planilhas/upload` - Upload de planilha
- `POST /api/planilhas/processar` - Processar planilha
- `GET /api/planilhas/{id}/download` - Download de planilha processada

### Dashboard (requer autenticação)

- `GET /api/dashboard/estatisticas` - Estatísticas do dashboard

### Clientes (requer autenticação)

- `GET /api/clientes` - Listar todos os clientes

## 📋 Como Usar a API

### 1. Criar uma Conta (se não tiver)

```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "meu_usuario",
  "email": "meu@email.com",
  "password": "minha_senha"
}
```

### 2. Fazer Login

```bash
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "meu_usuario",
  "password": "minha_senha"
}
```

**Resposta:**
```json
{
  "userId": "uuid-do-usuario",
  "username": "meu_usuario",
  "email": "meu@email.com",
  "role": "OPERATOR",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer"
}
```

### 3. Usar outros endpoints

Para usar os outros endpoints, inclua o token JWT no header:

```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Exemplo:**
```bash
GET /api/dashboard/estatisticas
Authorization: Bearer SEU_TOKEN_JWT_AQUI
```

### 💡 Dica Rápida

Para testar rapidamente, você pode:

1. **Criar conta** via POST `/api/auth/register`
2. **Fazer login** via POST `/api/auth/login`
3. **Usar o token JWT** retornado para acessar outros endpoints

**Lembre-se:** Se você não tem uma conta, simplesmente crie uma no frontend ou via API!

## 🎯 Funcionalidades

### Funcionalidades Implementadas
- ✅ **Autenticação Completa**: Registro, login e controle de usuários
- ✅ **Gestão de Usuários**: Usuários ADMIN e OPERATOR
- ✅ **Upload de Planilhas**: Suporte a Excel/CSV
- ✅ **Processamento com IA**: Integração com APIs de IA
- ✅ **Dashboard**: Estatísticas e métricas
- ✅ **Histórico**: Planilhas processadas
- ✅ **Download**: Resultados processados

### Funcionalidades em Desenvolvimento
- 🔄 Interface completa no frontend
- 🔄 Validações avançadas
- 🔄 Relatórios detalhados

## 📝 Notas de Desenvolvimento

## 🔐 Autenticação e Autorização

O sistema possui autenticação JWT completa implementada. Se você não tem uma conta, pode criar uma conta diretamente no frontend da aplicação.

### Endpoints de Autenticação

- `POST /api/auth/register` - Criar nova conta de usuário
- `POST /api/auth/login` - Fazer login na aplicação

### Como Criar uma Conta

1. Acesse o frontend da aplicação
2. Se você não tem uma conta, procure pela opção "Criar Conta" ou "Registrar-se"
3. Preencha os dados necessários (username, email, senha)
4. Após criar a conta, faça login normalmente

### Funcionalidades de Autenticação

- ✅ Registro de novos usuários
- ✅ Login com username ou email
- ✅ Senhas criptografadas com BCrypt
- ✅ Tokens JWT com expiração de 24h
- ✅ Controle de usuários ativos/inativos
- ✅ Validação de unicidade (username e email)

### Próximos Passos

1. ✅ Implementar as classes de implementação dos repositórios JPA
2. ✅ Implementar os casos de uso (UploadPlanilhaUseCase, ProcessarPlanilhaUseCase, etc.)
3. ✅ Implementar o serviço de armazenamento de arquivos
4. ✅ Implementar a integração completa com API de IA
5. ✅ Adicionar testes unitários e de integração
6. ✅ Implementar autenticação e autorização (COMPLETADO)
7. Adicionar tratamento de erros mais robusto
8. Implementar logging adequado
9. Criar interface de usuário para registro/login no frontend
10. Implementar recuperação de senha
11. Adicionar perfis de usuário personalizáveis

### Estrutura de Domínio

As entidades principais do domínio são:
- **Planilha**: Representa uma planilha enviada pelo usuário
- **Campo**: Representa um campo dentro de uma planilha
- **RegraPreenchimento**: Regras para preenchimento automático
- **Cliente**: Cliente que utiliza o sistema

## 🎨 Frontend

O frontend está sendo desenvolvido em **React + TypeScript + Vite** e possui as seguintes páginas:

- **Login**: Página de autenticação
- **Register**: Página para criar nova conta (se não tiver uma)
- **Dashboard**: Visão geral com estatísticas
- **Upload**: Upload e processamento de planilhas
- **Histórico**: Histórico de planilhas processadas

### Como acessar o frontend

```bash
cd frontend
npm install
npm run dev
```

O frontend estará disponível em: `http://localhost:5173`

## 📄 Licença

Este projeto é privado e de uso interno.
