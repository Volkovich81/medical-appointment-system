FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mkdir -p ./src/main/resources/static
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static
RUN if [ -d ./src/main/resources/static/assets ]; then \
      cp -r ./src/main/resources/static/assets/* ./src/main/resources/static/ && \
      rm -rf ./src/main/resources/static/assets; \
    fi
RUN echo "=== Content of static ===" && ls -lR ./src/main/resources/static
RUN mvn clean package -DskipTests