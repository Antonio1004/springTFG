Etapa de build con Maven y JDK
FROM maven:3.9.6-eclipse-temurin-17 AS builder

Carpeta de trabajo
WORKDIR /build

Copiamos pom.xml primero para cachear dependencias
COPY pom.xml .

Copiamos el código fuente
COPY src ./src

Construimos el jar (sin tests para agilizar)
RUN mvn clean package -DskipTests

Etapa final con JRE ligero
FROM eclipse-temurin:17-jre-jammy

Copiamos el jar desde la etapa de build
COPY --from=builder /build/target/*.jar app.jar

Exponemos el puerto de Spring Boot
EXPOSE 8080

Comando para ejecutar la app
ENTRYPOINT ["java","-jar","/app.jar"]