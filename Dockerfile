FROM eclipse-temurin:21-jdk-slim

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY src src

RUN chmod +x gradlew

RUN ./gradlew build -x test --no-daemon

COPY build/libs/java-project-99-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-Xmx256m", "-Xms128m", "-jar", "app.jar", "--spring.profiles.active=prod"]