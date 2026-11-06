# Etapa 1: Compilación del proyecto con Gradle y Java 21
FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon -x test

# Etapa 2: Imagen liviana de ejecución
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
