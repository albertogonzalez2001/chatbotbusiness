# ARCHIVO CON ARQUITECTURA | MULTI-STAGE BUILD
# FASE 1: BUILD
# Imagen con Maven y JDK 21 para compilar el proyecto
FROM eclipse-temurin:21-jdk AS builder

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Se copia el pom para aprovechar la caché de Docker
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargamos las dependencias sin compilar el proyecto
RUN ./mvnw dependency:go-offline -q

# Se copia el codigo fuente y se compila
COPY src ./src
RUN ./mvnw package -DskipTests -q

# FASE 2: RUNTIME
# Imagen mas ligera que reduce el tamaño significativamente, utilizando jre.
FROM eclipse-temurin:21-jre

WORKDIR /app

# Se copia solo el JAR generado desde la fase de build
COPY --from=builder /app/target/*.jar app.jar

# Puerto que expone el contenedor, que debe coincidir con el server.port del la app spring
EXPOSE 8081

# Comando con el que arranca la app cuando el contenedor inicia
ENTRYPOINT ["java", "-jar", "app.jar"]