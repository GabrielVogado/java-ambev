
### Desafio Java Spring Ambev


# Projeto: Gestão de Pedidos

Este projeto é um exemplo de uma aplicação de gestão de pedidos utilizando Spring Boot, Kafka e PostgreSQL. A aplicação permite criar, listar, atualizar e deletar pedidos através de uma API REST.

## Funcionalidades

- **Criar Pedido**: Endpoint para criar um novo pedido.
- **Listar Pedidos:** Endpoint para listar todos os pedidos.
- **Atualizar Pedido:** Endpoint para atualizar um pedido existente.
- **Deletar Pedido:** Endpoint para deletar um pedido.

## Tecnologias Utilizadas

- **Spring Boot:** Framework para criação da aplicação.
- **Kafka:** Sistema de mensagens utilizado para monitoramento e reprocessamento de requisições.
- **PostgreSQL:** Banco de dados utilizado para armazenamento dos pedidos.
- **Docker:** Utilizado para containerização da aplicação e seus serviços.

## Estrutura do Projeto

- **Controller:** Contém os endpoints da API.
- **Service:** Contém a lógica de negócio para gestão dos pedidos.
- **Infrastructure:** Contém testes e integração com Kafka.

## Configuração e Execução

### Pré-requisitos

- Docker e Docker Compose instalados.

### Passos para Execução

1. **Clone o Repositório:**

   ```bash
   git clone https://github.com/seu-usuario/seu-repositorio.git
   cd seu-repositorio
   ```

2. **Construa e Inicie os Contêineres:**

   ```bash
   docker-compose up --build
   ```

   Isso iniciará os seguintes serviços:
    - **db:** Servidor PostgreSQL.
    - **zookeeper:** Necessário para o Kafka.
    - **kafka:** Servidor Kafka.
    - **app:** Aplicação Spring Boot.
    - **kafdrop:** Interface web para monitorar o Kafka.

3. **Verifique os Logs:**

   Você pode verificar os logs de qualquer serviço utilizando o comando:

   ```bash
   docker logs nome-do-servico
   ```

   Por exemplo, para verificar os logs do `app`:

   ```bash
   docker logs app
   ```

### Endpoints Disponíveis

A aplicação expõe os seguintes endpoints através do contêiner `app`:

- **Criar Pedido:**
    - **URL:** `http://localhost:8080/orders`
    - **Método:** `POST`
    - **Descrição:** Cria um novo pedido.

- **Listar Pedidos:**
    - **URL:** `http://localhost:8080/orders`
    - **Método:** `GET`
    - **Descrição:** Lista todos os pedidos.

- **Atualizar Pedido:**
    - **URL:** `http://localhost:8080/orders/{id}`
    - **Método:** `PUT`
    - **Descrição:** Atualiza um pedido existente pelo ID.

- **Deletar Pedido:**
    - **URL:** `http://localhost:8080/orders/{id}`
    - **Método:** `DELETE`
    - **Descrição:** Deleta um pedido existente pelo ID.

### Monitoramento do Kafka

A interface web Kafdrop está disponível para monitorar os tópicos do Kafka:

- **URL:** `http://localhost:8085`

## Testes via Postman
Para testar as requisições via Postman, utilize as collections disponíveis na pasta collections localizada na raiz do projeto.

### Importe a Collection para o Postman:

```bash
Abra o Postman.
```

```bash
Clique em Import e selecione os arquivos da pasta collections.
```
```bash
Execute as Requisições:
```

```bash
Utilize os endpoints fornecidos para testar a criação, listagem, atualização e deleção de pedidos.
```

## Contribuições

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues e enviar pull requests.