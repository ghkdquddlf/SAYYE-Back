# ── Build Stage ──────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# ── Run Stage ────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S sayye && adduser -S sayye -G sayye

COPY --from=builder /app/build/libs/*.jar app.jar

RUN chown sayye:sayye app.jar
USER sayye

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Xms256m", "-Xmx512m", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
