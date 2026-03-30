# ── Etapa 1: Build ──
FROM gradle:9.2-jdk21 AS build
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY src ./src

RUN gradle shadowJar --no-daemon

# ── Etapa 2: Runtime ──
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/build/libs/*-all.jar app.jar

EXPOSE 7000

ENTRYPOINT ["java", "-jar", "app.jar"]