# Базовий образ з JRE
FROM eclipse-temurin:21-jre-alpine

# Назва робочої директорії всередині контейнера
WORKDIR /app

COPY target/*.jar app.jar

# Команда для запуску застосунку
ENTRYPOINT ["java", "-jar", "app.jar"]