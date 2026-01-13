# Sistema de Automação Inteligente para Preenchimento de Planilhas Contábeis

Sistema desenvolvido seguindo **Clean Architecture** com **DDD (Domain-Driven Design)** e princípios **SOLID**.

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

### Planilhas

- `POST /api/planilhas/upload` - Upload de planilha
- `POST /api/planilhas/processar` - Processar planilha
- `GET /api/planilhas/{id}/download` - Download de planilha processada

### Dashboard

- `GET /api/dashboard/estatisticas` - Estatísticas do dashboard

## 🎯 Funcionalidades

- ✅ Upload de planilhas (Excel/CSV)
- ✅ Processamento de planilhas com IA
- ✅ Dashboard com estatísticas
- ✅ Histórico de planilhas processadas
- ✅ Download de planilhas processadas

## 📝 Notas de Desenvolvimento

### Próximos Passos

1. Implementar as classes de implementação dos repositórios JPA
2. Implementar os casos de uso (UploadPlanilhaUseCase, ProcessarPlanilhaUseCase, etc.)
3. Implementar o serviço de armazenamento de arquivos
4. Implementar a integração completa com API de IA
5. Adicionar testes unitários e de integração
6. Implementar autenticação e autorização
7. Adicionar tratamento de erros mais robusto
8. Implementar logging adequado

### Estrutura de Domínio

As entidades principais do domínio são:
- **Planilha**: Representa uma planilha enviada pelo usuário
- **Campo**: Representa um campo dentro de uma planilha
- **RegraPreenchimento**: Regras para preenchimento automático
- **Cliente**: Cliente que utiliza o sistema

## 📄 Licença

Este projeto é privado e de uso interno.
