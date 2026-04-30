# Этап 1 — сборка React
FROM node:22-alpine AS frontend-build
WORKDIR /app/frontend
COPY medical-appointment-client/package*.json ./
RUN npm install
COPY medical-appointment-client/ ./
ARG VITE_API_URL=""
ENV VITE_API_URL=$VITE_API_URL
RUN npm run build
# Удаляем CSP мета-тег
RUN node -e "const fs=require('fs'); let html=fs.readFileSync('dist/index.html','utf8'); html=html.replace(/<meta[^>]*http-equiv=['\"]Content-Security-Policy['\"][^>]*>/gi,''); fs.writeFileSync('dist/index.html',html);"

# Этап 2 — сборка Spring Boot
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
# Копируем СОДЕРЖИМОЕ dist (включая папку assets) прямо в static
COPY --from=frontend-build /app/frontend/dist/ ./src/main/resources/static/
RUN echo "=== Static files ===" && ls -lR ./src/main/resources/static
RUN mvn clean package -DskipTests

# Этап 3 — финальный образ
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]