FROM eclipse-temurin:21-jre

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY src src

RUN chmod +x gradlew

RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

RUN ./gradlew build -x test --no-daemon

COPY build/libs/java-project-99-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-Xmx256m", "-Xms128m", "-jar", "app.jar", "--spring.profiles.active=prod"]