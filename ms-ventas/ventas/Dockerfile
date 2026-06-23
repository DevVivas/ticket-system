# Etapa 1: Build - compila el proyecto con Maven
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copia el wrapper de Maven y el pom.xml primero (cache de dependencias)
COPY .mvn/ .mvn/
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .

# Da permisos de ejecucion al wrapper y descarga dependencias
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copia el codigo fuente y compila el JAR (sin correr tests, ya se corrieron antes)
COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

# Etapa 2: Run - imagen liviana solo con el JAR compilado
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia el JAR generado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
