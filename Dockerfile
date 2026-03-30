# Build completo: frontend (Vite) + API (Spring Boot)
# O JAR serve o React em /api/* junto com a API (server.servlet.context-path=/api).
#
# Build-arg opcional:
#   VITE_DEPLOY_EMBEDDED=true (padrão) — base /api/ e front na mesma origem.
# Contexto do build: raiz do repositório.

# ---------- Frontend ----------
FROM node:20-alpine AS frontend
WORKDIR /fe

ARG VITE_DEPLOY_EMBEDDED=true
ENV VITE_DEPLOY_EMBEDDED=$VITE_DEPLOY_EMBEDDED

COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build

# ---------- Backend (inclui static do React em src/main/resources/static) ----------
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /build

COPY backend /build/backend
COPY --from=frontend /fe/dist /build/backend/src/main/resources/static

WORKDIR /build/backend
RUN mvn package -DskipTests -B

# ---------- Runtime ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/backend/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
