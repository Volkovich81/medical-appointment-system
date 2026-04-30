# Этап сборки Spring Boot
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
COPY static ./src/main/resources/static
RUN mvn clean package -DskipTests

# Финальный образ: Nginx + Java
FROM nginx:alpine
# Устанавливаем OpenJDK 17 (Alpine)
RUN apk add --no-cache openjdk17-jre
# Копируем собранный jar
COPY --from=backend-build /app/target/*.jar /app/app.jar
# Копируем статику для Nginx
COPY static /usr/share/nginx/html
# Копируем конфиг Nginx
COPY nginx.conf /etc/nginx/nginx.conf
# Копируем скрипт запуска
RUN echo -e "#!/bin/sh\nnginx\njava -jar /app/app.jar" > /start.sh && chmod +x /start.sh
EXPOSE 80
CMD ["/start.sh"]