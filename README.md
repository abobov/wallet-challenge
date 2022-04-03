# WALLET MICROSERVICE

A simple wallet microservice running on the JVM that manages credit/debit transactions on behalf of
players.

## Implementation details

- Current balance stored in Player entity.
- No service to manage players. Player created on first transaction.
- Not existing player treat as a player with 0 current balance and no transactions.
- Optimistic locking with retry used to perform concurrent transactions.
- Additional header field could be used to do idempotent transactions (see [requests example file][request-examples]). Idempotent keys are stored in memory, in
  production could be used more reliable storage.

### Running microservice

To run microservice with disposable database:

```shell
./mvnw spring-boot:run
```

To run with persistent storage (will store database files in current directory):

```shell
./mvnw spring-boot:run -Dspring-boot.run.profiles=persist
```

### API Endpoints

All URIs are relative to `http://localhost:8080/`

| HTTP request                          | Description                         |
|---------------------------------------|-------------------------------------|
| `GET /players/{player}`               | Get current balance per player.     |
| `GET /players/{player}/transactions`  | Get list of transaction per player. |
| `POST /players/{player}/transactions` | Add transaction per player.         |

Examples could be found in IntelliJ IDEA HTTP Client
format [file wallet-requests.http][request-examples].

### API models

#### Transaction

```json
{
  "id": "transaction-id",
  "type": "Credit",
  "amount": 10
}
```

- `id` - any string representing transaction id
- `type` - transaction type string, one of: Credit, Debit
- `amount` - transaction amount, number >= 0

All fields are required.

#### Balance

```json
{
  "currentBalance": 10
}
```

- `currentBalance` - player current balance, number >= 0

[request-examples]: wallet-requests.http
