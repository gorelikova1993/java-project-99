FROM eclipse-temurin:21-jre

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

RUN ./gradlew clean build -x test --no-daemon && ls -l build/libs

COPY build/libs/app-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-Xmx256m", "-Xms128m", "-jar", "app.jar", "--spring.profiles.active=prod"]