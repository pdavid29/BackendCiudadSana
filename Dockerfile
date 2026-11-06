# Stage 1: Build execution
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia todo el contenido del repositorio
COPY . .

# Si pom.xml está en una subcarpeta llamada backend, entra primero a ella
# Si está en la raíz, borra "cd backend &&" de la siguiente línea:
RUN cd backend && mvn clean package -DskipTests

# Stage 2: Production environment
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia el JAR generado desde la carpeta objetivo
COPY --from=build /app/backend/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
