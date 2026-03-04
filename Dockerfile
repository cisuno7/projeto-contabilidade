# Build da API Java - contexto: raiz do repositório
# Stage 1: build JAR com Maven
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /build

# Copia todo o backend de uma vez (evita erro /src não encontrado no Render)
COPY backend /build/backend
WORKDIR /build/backend
RUN mvn package -DskipTests -B

# Stage 2: runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/backend/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
