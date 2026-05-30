# persistence-flat-pagination-sorting-jdbc

Offset-based pagination with multi-field sorting over JDBC — no Spring Data, pure SQL, clean architecture.

## What's inside

- `PageRequest` — page, size, and an ordered list of `SortRequest`
- `SortRequest` — field + direction (`ASC` / `DESC`) with whitelist validation against SQL injection
- `PageResult<T>` — content + pagination metadata, framework-agnostic
- `OwnerReadRepository` — port in the application layer, JDBC implementation in infrastructure
- SQL with `LIMIT / OFFSET` and dynamic `ORDER BY` built safely from the whitelist

## How it works

```java
// no sorting — default ORDER BY o.id ASC
PageRequest.of(0, 10);

// single field
PageRequest.of(0, 10, SortRequest.asc("o.name"));

// multiple fields
PageRequest.of(0, 10, List.of(
    SortRequest.asc("o.name"),
    SortRequest.desc("o.id")
));
```

## Architecture

```
application/
  OwnerReadRepository   ← port (interface)
  OwnerView             ← read model
  PageRequest           ← pagination + sort input
  PageResult<T>         ← pagination output
  SortRequest           ← sort field + direction

infrastructure/
  JdbcOwnerReadRepository   ← JDBC implementation
  OwnerProjection           ← internal, never leaks out
  ViewMapper                ← projection → view
```

`PageRequest`, `PageResult`, `SortRequest` have zero framework dependencies — copy them into any Java project.

## Previous

[persistence-flat-pagination-jdbc](https://github.com/java-backend-architecture/persistence-flat-pagination-jdbc) — pagination only, no sorting.
