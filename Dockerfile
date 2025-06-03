FROM gradle:8.12.1-jdk17

WORKDIR /app

COPY . .

RUN ["./gradlew", "clean", "build", "--stacktrace"]

CMD ["./gradlew", "bootRun"]