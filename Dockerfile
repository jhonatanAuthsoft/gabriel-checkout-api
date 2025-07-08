# Estágio 1: Compilação com Maven
FROM maven:3.8.5-openjdk-17 AS builder

WORKDIR /app

# V-- PONTO CRÍTICO #1: A linha COPY deve estar aqui
COPY settings.xml .
COPY pom.xml .

# V-- PONTO CRÍTICO #2: O "-s settings.xml" deve estar no comando
RUN mvn -s settings.xml dependency:go-offline

COPY src ./src
# V-- PONTO CRÍTICO #3: O "-s settings.xml" também deve estar aqui
RUN mvn -s settings.xml package -DskipTests

# Estágio 2: Imagem final de produção
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]