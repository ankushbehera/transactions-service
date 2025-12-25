# Pismo – Transactions Service

A Spring Boot service that manages Accounts and Transactions.

This service allows:
- Creating accounts
- Fetching account details
- Creating transactions with business rules applied

---

## Tech Stack
- Java 21
- Spring Boot 3.5.X
- Spring Data JPA
- PostgreSQL
- Liquibase (schema management)
- Docker + Docker Compose
- JUnit + Testcontainers

---

## Business Rules

Each Account can have multiple Transactions.

Transaction Types:

| ID | Type                     | Sign |
|----|--------------------------|------|
| 1  | Cash Purchase            | -    |
| 2  | Installment Purchase     | -    |
| 3  | Withdrawal               | -    |
| 4  | Payment                  | +    |



- Purchases & Withdrawals are stored as **negative values**
- Payments are stored as **positive values**

---

## Architecture
- Layered architecture (Controller → Service → Repository)
- Clean separation of concerns
- Validation + exception handling included
- Database schema managed via Liquibase

---

## Run the Application

### Option 1 — Run with Docker (Recommended)

Requires Docker installed.

```
docker-compose up
```

This starts:
- PostgreSQL
- Application running on port: **8080**

---

### Option 2 — Run Locally with Maven

Requires PostgreSQL running locally (or update DB config)

```
mvn spring-boot:run
```

---

## API Documentation (Swagger)

Swagger UI is available to explore and test the APIs in the browser.

**http://localhost:8080/swagger-ui/index.html**

---
## API Endpoints

### 1. Create Account
**POST /accounts**

Request:
```json
{
  "document_number": "12345678900"
}
```

Response:
```json
{
  "account_id": 1,
  "document_number": "12345678900"
}
```

---

### 2. Get Account
**GET /accounts/{accountId}**

Response:
```json
{
  "account_id": 1,
  "document_number": "12345678900"
}
```

---

### 3. Create Transaction
**POST /transactions**

Request:
```json
{
  "account_id": 1,
  "operation_type_id": 4,
  "amount": 123.45
}
```

---

## Tests

Run:
```
mvn clean test
```

Includes:
- Unit tests
- Integration tests

---

## Requirements Covered
- Java
- Maintainable structure
- Simple and readable implementation
- Test coverage
- Docker support
- Clear documentation
- Easy to run

---