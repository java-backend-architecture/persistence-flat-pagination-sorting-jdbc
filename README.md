# persistence-flat-pagination-sorting-jdbc

Extends [persistence-flat-pagination-jdbc](https://github.com/java-backend-architecture/persistence-flat-pagination-jdbc) with multi-field sorting — no Spring Data, pure SQL, clean architecture.

## What it shows

* Multi-field sorting via `SortRequest` with whitelist validation against SQL injection
* Dynamic `ORDER BY` built safely from an allowed fields set
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
    PageRequest           ← pagination + sort input
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
PageRequest.of(0, 10);

// single field
PageRequest.of(0, 10, SortRequest.asc("o.name"));

// multiple fields
PageRequest.of(0, 10, List.of(
    SortRequest.asc("o.name"),
    SortRequest.desc("o.id")
));
```

## Why a whitelist

`SortRequest` validates the field against an allowed set — invalid fields throw `IllegalArgumentException`:

```java
private static final Set<String> ALLOWED_FIELDS = Set.of("o.id", "o.name");
```

This prevents SQL injection via the sort parameter. The allowed set is the only place to extend when new sortable fields are added.

`PageRequest`, `PageResult`, `SortRequest` have zero framework dependencies — copy them into any Java project.

## Tests

Integration tests in `src/test/java` cover pagination correctness, sorting by single and multiple fields, default sort order, and SQL injection protection via the whitelist.

## Related

* [persistence-flat-pagination-jdbc](https://github.com/java-backend-architecture/persistence-flat-pagination-jdbc) — pagination only, no sorting
* [persistence-graph-pagination-jdbc](https://github.com/java-backend-architecture/persistence-graph-pagination-jdbc) — pagination over a multi-level object graph

## Run

```bash
./mvnw spring-boot:run
```
