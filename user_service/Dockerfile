FROM gradle:8.5-jdk21 AS builder
COPY . /app
WORKDIR /app
RUN gradle build -x test --no-daemon

FROM openjdk:21-jdk-slim
COPY --from=builder /app/build/libs/user_service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
