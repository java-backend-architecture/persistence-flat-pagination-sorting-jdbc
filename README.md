# persistence-flat-pagination-sorting-jdbc

Extends [persistence-flat-pagination-jdbc](https://github.com/java-backend-architecture/persistence-flat-pagination-jdbc) with multi-field sorting — no Spring Data, pure SQL, clean architecture.

## What it shows

* Multi-field sorting via `SortRequest` with domain field names — no SQL aliases in the application layer
* SQL alias mapping lives in the infrastructure adapter — `FIELD_MAP` translates domain names to SQL
* Dynamic `ORDER BY` built safely at the infrastructure boundary
* Offset-based pagination with `LIMIT / OFFSET` and `COUNT(*)`
* Using `package-private` classes as an encapsulation boundary inside the infrastructure layer

## Stack

* Java 25
* Spring Boot
* Spring JDBC (`JdbcClient`)
* H2 (in-memory database)

## Structure

```
application/
    OwnerReadRepository   ← port (interface)
    OwnerView             ← read model
    PageQuery             ← pagination + sort input
    PageResult<T>         ← pagination output
    SortRequest           ← sort field + direction with whitelist validation

infrastructure/
    JdbcOwnerReadRepository   ← JDBC implementation
    OwnerProjection           ← internal, never leaks out
    ViewMapper                ← projection → view
```

## How it works

```java
// no sorting — default ORDER BY o.id ASC
PageQuery.of(0, 10);

// single field
PageQuery.of(0, 10, List.of(SortRequest.asc("name")));

// multiple fields
PageQuery.of(0, 10, List.of(
    SortRequest.asc("name"),
    SortRequest.desc("id")
));
```

## Why a whitelist

`SortRequest` validates the field against an allowed set of domain names — invalid fields throw `IllegalArgumentException`:

```java
private static final Set<String> ALLOWED_FIELDS = Set.of("id", "name");
```

The mapping to SQL aliases happens in the infrastructure adapter:

```java
private static final Map<String, String> FIELD_MAP = Map.of(
    "id",   "o.id",
    "name", "o.name"
);
```

This keeps the application layer free of SQL details. The allowed set and the field map are the only places to extend when new sortable fields are added.

`PageQuery`, `PageResult`, `SortRequest` have zero framework dependencies — copy them into any Java project.

## Tests

Integration tests in `src/test/java` cover pagination correctness, sorting by single and multiple fields, default sort order, and SQL injection protection via the whitelist.

## Related

* [persistence-flat-pagination-jdbc](https://github.com/java-backend-architecture/persistence-flat-pagination-jdbc) — pagination only, no sorting
* [persistence-graph-pagination-jdbc](https://github.com/java-backend-architecture/persistence-graph-pagination-jdbc) — pagination over a multi-level object graph

## Run

```bash
./mvnw spring-boot:run
```
