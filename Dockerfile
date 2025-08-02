# Стадия 1: Сборка приложения
FROM eclipse-temurin:21-jdk AS builder

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем всё содержимое проекта в контейнер
COPY . .

# Делаем gradlew исполняемым
RUN chmod +x gradlew

# Собираем приложение без тестов и без демона
RUN ./gradlew clean build -x test --no-daemon && ls -l build/libs

# Стадия 2: Запуск приложения
FROM eclipse-temurin:21-jre

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный jar-файл из предыдущей стадии
COPY --from=builder /app/build/libs/*.jar app.jar

# Команда запуска приложения
CMD ["java", "-Xmx256m", "-Xms128m", "-jar", "app.jar"]
