# ================================
# Estágio 1: Compilação com Maven
# ================================
FROM maven:3.8.5-openjdk-17 AS builder

WORKDIR /app

# Copia arquivos de configuração e POM para cache das dependências
COPY settings.xml .
COPY pom.xml .

# Baixa dependências offline usando settings.xml
RUN mvn -s settings.xml dependency:go-offline

# Copia o código fonte
COPY src ./src

# Compila e gera o pacote (sem testes)
RUN mvn -s settings.xml package -DskipTests

# ================================
# Estágio 2: Imagem final de produção
# ================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Instala as dependências necessárias para fontes e AWT (libfreetype etc.)
RUN apk add --no-cache \
    freetype \
    fontconfig \
    ttf-dejavu \
    libx11

# Copia o JAR compilado do estágio anterior
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Usa headless por padrão (opcional, remove se não precisar)
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "app.jar"]
