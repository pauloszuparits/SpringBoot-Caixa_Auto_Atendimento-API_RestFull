# Etapa de build
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app

# Copiar arquivos necessários
COPY pom.xml ./
COPY src ./src

# Construir o projeto
RUN mvn clean package -DskipTests

# Etapa de execução
FROM openjdk:17-jdk-slim
WORKDIR /app

# Expor a porta do aplicativo
EXPOSE 8080

# Copiar o JAR gerado na etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]
