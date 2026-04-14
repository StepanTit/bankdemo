# bankdemo

A sample Spring Boot application for users, accounts, deposits, withdrawals, transfers, and account closure. Data is stored in PostgreSQL via Spring Data JPA.

## Requirements

- **PostgreSQL** with a database created (default: `bankdemo` on `localhost:5432`)

## Configuration

The application listens on port **8080** (`server.port`)

Connection and business settings are defined in `src/main/resources/application.properties`:

| Property | Purpose |
|----------|---------|
| `spring.datasource.url`, `username`, `password` | PostgreSQL connection |
| `account.default-amount` | Initial balance for a new account |
| `account.transfer-commission` | Fee for transfers **between different users**, as a percentage of the transfer amount |

POSTMAN collection for testing in `postman\bankdemo.postman_collection.json`





