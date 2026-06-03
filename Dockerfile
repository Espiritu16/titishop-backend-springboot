# syntax=docker/dockerfile:1.7

##
## Etapa 1: build del JAR con Maven + Java 21
##
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src ./src
RUN ./mvnw -q clean package -DskipTests

##
## Etapa 2: runtime liviano solo JRE
##
FROM eclipse-temurin:21-jre

WORKDIR /app

RUN useradd -m -u 1001 springuser

COPY --from=builder /app/target/*.jar /app/app.jar

ENV JAVA_OPTS=""

EXPOSE 8080

USER springuser

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
