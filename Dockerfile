# Etapa 1: Construir a aplicação usando Maven
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Copie o arquivo pom.xml e baixe as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copie o código fonte e construa o projeto
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Criar a imagem final usando OpenJDK
FROM openjdk:17-jdk-oracle
WORKDIR /app

# Copiar o jar construído da fase anterior
COPY --from=build /app/target/java-ambev-1.0-SNAPSHOT.jar /app/java-ambev.jar

# Exponha a porta 8080
EXPOSE 8080

# Defina o comando padrão para executar o jar da aplicação
ENTRYPOINT ["java", "-jar", "/app/java-ambev.jar"]
