FROM gradle:8.12.1-jdk17

WORKDIR /app

COPY /app .

RUN ["./gradlew", "clean", "build"]

CMD ["./gradlew", "bootRun"]