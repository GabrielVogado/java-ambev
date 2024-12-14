# Use a imagem base do OpenJDK 17
FROM openjdk:17-jdk-slim

# Defina o diretório de trabalho
WORKDIR /app

# Copie o jar do aplicativo para o contêiner
COPY target/java-ambev-1.0.0.jar java-ambev.jar

# Execute o aplicativo
ENTRYPOINT ["java", "-jar", "java-ambev.jar"]
