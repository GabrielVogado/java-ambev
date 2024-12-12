# Use a imagem oficial do Maven para construir a aplicação
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Copie o arquivo pom.xml e baixe as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copie o código fonte e construa o projeto
COPY src ./src
RUN mvn clean package -DskipTests

# Use uma imagem oficial do OpenJDK 17 da Oracle como base para a execução
FROM openjdk:17-jdk-oracle
WORKDIR /app

# Copie o jar construído da fase anterior
COPY --from=build /app/target/java-ambev-1.0-SNAPSHOT.jar /app/java-ambev.jar

# Exponha a porta 8080
EXPOSE 8080

# Defina o comando padrão para executar o jar da aplicação
ENTRYPOINT ["java", "-jar", "/app/java-ambev.jar"]
