# Franq Management API

REST API for managing franchises, branches and products.

## Deployed API

The API is deployed on AWS ECS using Amazon Fargate.

**Base URL:**

```text
http://franq-management-api-1605062523.us-east-1.elb.amazonaws.com
```

## Technologies

* Java 21
* Spring Boot 4.1.1
* Spring WebFlux
* DynamoDB
* AWS ECS / Fargate
* AWS ECR
* AWS CloudWatch
* Docker
* Docker Compose
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

---

# Project Structure

The project uses separate Spring profiles for each execution environment:

| Environment          | Profile  | DynamoDB endpoint       |
| -------------------- | -------- | ----------------------- |
| Local from IDE/Maven | `local`  | `http://localhost:8000` |
| Docker Compose       | `docker` | `http://dynamodb:8000`  |
| AWS ECS              | `aws`    | AWS DynamoDB            |

This separation prevents the local Docker hostname `dynamodb` from being used when the application runs in AWS.

---

# Run Locally

## Requirements

Install the following:

* Java 21
* Maven
* Docker
* Docker Compose

The project includes the Maven Wrapper, so Maven does not need to be installed globally.

---

# Option 1 — Run the Complete Environment with Docker Compose

This is the recommended option for reproducing the complete local environment.

Docker Compose starts:

1. DynamoDB Local
2. DynamoDB initialization container
3. Spring Boot API

Architecture:

```text
                    Docker Compose
                         │
          ┌──────────────┴──────────────┐
          │                             │
          ▼                             ▼
   franq-dynamodb                  franq-api
       :8000                          :8080
          ▲                             │
          │                             │
          └────── http://dynamodb:8000 ─┘
```

## Start the environment

From the project root:

```powershell
docker compose up -d --build
```

The `--build` option rebuilds the API image using the current source code.

## Check running containers

```powershell
docker compose ps
```

You should see:

```text
franq-dynamodb
franq-dynamodb-init
franq-api
```

## API URL

The API will be available at:

```text
http://localhost:8080
```

## Health check

```text
GET http://localhost:8080/actuator/health
```

## API example

```text
GET http://localhost:8080/franchises
```

## Stop the environment

```powershell
docker compose down
```

---

# Option 2 — Run DynamoDB Local and Spring Boot from the Host

You can also run DynamoDB Local with Docker while running Spring Boot directly from your IDE or Maven.

Start DynamoDB:

```powershell
docker compose up -d dynamodb dynamodb-init
```

The application must use the `local` Spring profile.

The local configuration uses:

```yaml
aws:
  region: us-east-1
  dynamodb:
    endpoint: http://localhost:8000
```

Run the API:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

The API will be available at:

```text
http://localhost:8080
```

---

# Spring Profiles

## Local profile

File:

```text
src/main/resources/application-local.yml
```

Configuration:

```yaml
aws:
  region: us-east-1
  dynamodb:
    endpoint: http://localhost:8000
```

Used when Spring Boot runs directly on the local machine.

---

## Docker profile

File:

```text
src/main/resources/application-docker.yml
```

Configuration:

```yaml
aws:
  region: us-east-1
  dynamodb:
    endpoint: http://dynamodb:8000
```

Used when the API runs inside Docker Compose.

The hostname `dynamodb` corresponds to the Docker Compose service:

```yaml
dynamodb:
  image: amazon/dynamodb-local:latest
```

The API container must **not** use `localhost:8000` to reach DynamoDB because `localhost` inside the API container refers to the API container itself.

---

## AWS profile

File:

```text
src/main/resources/application-aws.yml
```

Configuration:

```yaml
aws:
  region: us-east-1
```

The AWS profile does not configure a local DynamoDB endpoint.

When running on ECS, the AWS SDK connects to the AWS DynamoDB service.

ECS uses:

```text
SPRING_PROFILES_ACTIVE=aws
```

---

# Docker Configuration

The API Docker image uses a multi-stage Docker build.

The first stage compiles the Spring Boot application:

```dockerfile
FROM eclipse-temurin:21-jdk AS build
```

The second stage runs the application using the JRE:

```dockerfile
FROM eclipse-temurin:21-jre
```

The application listens on:

```text
8080
```

Build and run the complete local environment with:

```powershell
docker compose up -d --build
```

---

# DynamoDB Local

DynamoDB Local is configured in Docker Compose:

```yaml
dynamodb:
  image: amazon/dynamodb-local:latest
  container_name: franq-dynamodb
  command: "-jar DynamoDBLocal.jar -sharedDb -inMemory"
  ports:
    - "8000:8000"
```

The database runs in memory.

This means data is intended for local development/testing and is lost when the DynamoDB Local container is recreated.

The `dynamodb-init` service executes the database initialization script:

```text
docker/dynamodb/init/create-table.sh
```

---

# Run Tests

Run the complete test suite:

```powershell
.\mvnw.cmd clean test
```

On Linux/macOS:

```bash
./mvnw clean test
```

---

# API Endpoints

## Postman

Postman collections and environments are available in:

postman/
├── Franq-Management-API.postman_collection.json
├── Franq-Management-API-Local.postman_environment.json
└── Franq-Management-API-AWS.postman_environment.json
## Franchises

| Method | Endpoint                         | Description           |
| ------ | -------------------------------- | --------------------- |
| POST   | `/franchises`                    | Create a franchise    |
| GET    | `/franchises`                    | Get all franchises    |
| GET    | `/franchises/{id}`               | Get a franchise       |
| PATCH  | `/franchises/{franchiseId}/name` | Update franchise name |

## Branches

| Method | Endpoint                                             | Description        |
| ------ | ---------------------------------------------------- | ------------------ |
| POST   | `/franchises/{franchiseId}/branches`                 | Create a branch    |
| PATCH  | `/franchises/{franchiseId}/branches/{branchId}/name` | Update branch name |

## Products

| Method | Endpoint                                                                  | Description                                       |
| ------ | ------------------------------------------------------------------------- | ------------------------------------------------- |
| POST   | `/franchises/{franchiseId}/branches/{branchId}/products`                  | Create a product                                  |
| PATCH  | `/franchises/{franchiseId}/branches/{branchId}/product/{productId}/name`  | Update product name                               |
| PATCH  | `/franchises/{franchiseId}/branches/{branchId}/product/{productId}/stock` | Update product stock                              |
| DELETE | `/franchises/{franchiseId}/branches/{branchId}/product/{productId}`       | Delete a product                                  |
| GET    | `/franchises/{franchiseId}/products/top-stock`                            | Get the product with the highest stock per branch |

---

# Postman

A Postman collection is included in the project for testing the API endpoints.

Configure the Postman variable:

```text
baseUrl
```

## AWS

```text
http://franq-management-api-1605062523.us-east-1.elb.amazonaws.com
```

Example:

```text
{{baseUrl}}/franchises
```

## Local

```text
http://localhost:8080
```

Example:

```text
{{baseUrl}}/franchises
```

---

# AWS Deployment

The application is deployed using:

```text
Docker
   ↓
Amazon ECR
   ↓
Amazon ECS / Fargate
   ↓
Application Load Balancer
   ↓
Spring Boot API
   ↓
AWS DynamoDB
```

Terraform is used to provision and manage the AWS infrastructure.

## AWS Spring profile

The ECS task definition uses:

```hcl
environment = [
  {
    name  = "SPRING_PROFILES_ACTIVE"
    value = "aws"
  }
]
```

This is important because the `docker` profile is intended for Docker Compose local development.

The AWS environment must use:

```text
SPRING_PROFILES_ACTIVE=aws
```

so that the application connects to AWS DynamoDB instead of trying to resolve the Docker hostname:

```text
dynamodb
```

---

# AWS Infrastructure

The project uses Terraform to manage AWS resources including:

* Amazon ECR
* Amazon ECS
* AWS Fargate
* Application Load Balancer
* Target Group
* CloudWatch Logs
* IAM roles
* DynamoDB

The ECS task definition exposes:

```text
8080
```

The application logs are sent to Amazon CloudWatch Logs.

---

# Useful Docker Commands

## Start

```powershell
docker compose up -d --build
```

## Stop

```powershell
docker compose down
```

## Check containers

```powershell
docker compose ps
```

## View API logs

```powershell
docker compose logs -f api
```

## View DynamoDB logs

```powershell
docker compose logs -f dynamodb
```

## View initialization logs

```powershell
docker compose logs -f dynamodb-init
```

---

# Useful Terraform Commands

Initialize Terraform:

```powershell
terraform init
```

Validate the configuration:

```powershell
terraform validate
```

Generate an execution plan:

```powershell
terraform plan
```

Apply the infrastructure changes:

```powershell
terraform apply
```

---

# Environment Summary

The application supports three execution environments:

```text
┌─────────────────────────────────────────────────────────┐
│                         LOCAL IDE                        │
│                                                         │
│  Profile: local                                         │
│  Spring Boot → localhost:8000 → DynamoDB Local         │
└─────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────┐
│                      DOCKER COMPOSE                     │
│                                                         │
│  Profile: docker                                        │
│  Spring Boot → dynamodb:8000 → DynamoDB Local          │
└─────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────┐
│                          AWS ECS                        │
│                                                         │
│  Profile: aws                                           │
│  Spring Boot → AWS SDK → Amazon DynamoDB                │
└─────────────────────────────────────────────────────────┘
```

This separation keeps local Docker configuration independent from the AWS deployment configuration.
