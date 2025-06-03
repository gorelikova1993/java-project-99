FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN ["./gradlew", "clean", "build", "--stacktrace"]

CMD ["./gradlew", "bootRun"]