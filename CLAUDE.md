| Value Object | Noun | Money, Address |
| Repository | `{Entity}Repository` | OrderRepository |
| Request DTO | `{Verb}{Domain}Request` | CreateOrderRequest |
| Response DTO | `{Domain}Response` | OrderResponse |
| Exception | `{Domain}{Reason}Exception` | OrderNotFoundException |

#### Method Names

| Type | Pattern | Example |
|------|---------|---------|
| Application Service | Use-case verb | createOrder, cancelOrder |
| Domain Service | Domain rule verb | calculateTotalPrice, reserveStock |
| Entity | Business action verb | cancel, confirm, ship |
| Repository | find, save, delete | findByCustomerId, save |