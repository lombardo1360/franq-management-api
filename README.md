# Franq Management API

REST API for managing franchises, branches and products.

## Deployed API

The API is deployed on AWS ECS.

**Base URL:**
http://44.192.102.140:8080

## Technologies

* Java 21
* Spring Boot
* Spring WebFlux
* DynamoDB
* AWS ECS
* AWS ECR
* Docker
* Terraform
* JUnit 5
* Mockito
* Postman

## Features

* Create a franchise
* List franchises
* Find a franchise by ID
* Update franchise name
* Create a branch
* Update branch name
* Create a product
* Update product name
* Update product stock
* Delete a product
* Find the product with the highest stock for each branch of a specific franchise

## Run locally

### Requirements

* Java 21
* Maven
* Docker
* Docker Compose

### Start DynamoDB locally

```bash
docker compose up -d
```

### Run the application

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

## Run tests

```bash
./mvnw clean test
```

On Windows:

```powershell
.\mvnw.cmd clean test
```

## API Endpoints

### Franchises

| Method | Endpoint                         | Description           |
| ------ | -------------------------------- | --------------------- |
| POST   | `/franchises`                    | Create a franchise    |
| GET    | `/franchises`                    | Get all franchises    |
| GET    | `/franchises/{id}`               | Get a franchise       |
| PATCH  | `/franchises/{franchiseId}/name` | Update franchise name |

### Branches

| Method | Endpoint                                             | Description        |
| ------ | ---------------------------------------------------- | ------------------ |
| POST   | `/franchises/{franchiseId}/branches`                 | Create a branch    |
| PATCH  | `/franchises/{franchiseId}/branches/{branchId}/name` | Update branch name |

### Products

| Method | Endpoint                                                                  | Description                                       |
| ------ | ------------------------------------------------------------------------- | ------------------------------------------------- |
| POST   | `/franchises/{franchiseId}/branches/{branchId}/products`                  | Create a product                                  |
| PATCH  | `/franchises/{franchiseId}/branches/{branchId}/product/{productId}/name`  | Update product name                               |
| PATCH  | `/franchises/{franchiseId}/branches/{branchId}/product/{productId}/stock` | Update product stock                              |
| DELETE | `/franchises/{franchiseId}/branches/{branchId}/product/{productId}`       | Delete a product                                  |
| GET    | `/franchises/{franchiseId}/products/top-stock`                            | Get the product with the highest stock per branch |

## Postman

A Postman collection is included in the project for testing the API endpoints.

Import the collection into Postman and configure the API base URL:

```text
http://44.192.102.140:8080
```

For local execution:

```text
http://localhost:8080
```
