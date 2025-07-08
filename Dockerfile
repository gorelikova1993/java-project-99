# Стадия 1: Сборка приложения
   FROM eclipse-temurin:21-jdk AS builder

   # Устанавливаем рабочую директорию
   WORKDIR /app

   # Копируем все необходимые файлы
   COPY . .

   # Делаем gradlew исполняемым
   RUN chmod +x gradlew

   # Собираем приложение без тестов и Gradle Daemon
   RUN ./gradlew clean build -x test --no-daemon && ls -l build/libs

   # Стадия 2: Запуск приложения
   FROM eclipse-temurin:21-jre

   # Устанавливаем рабочую директорию
   WORKDIR /app

   # Копируем JAR-файл из стадии сборки
   COPY --from=builder /app/build/libs/app-0.0.1-SNAPSHOT.jar app.jar

   # Указываем команду для запуска приложения
   CMD ["java", "-Xmx256m", "-Xms128m", "-jar", "app.jar", "--spring.profiles.active=prod"]