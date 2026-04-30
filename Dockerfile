FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
# Копируем готовую статику из папки static
COPY static ./src/main/resources/static
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]