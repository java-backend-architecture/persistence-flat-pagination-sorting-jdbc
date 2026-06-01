package dev.dmitriirussu.flat_pagination_sorting_jdbc.infrastructure;

import dev.dmitriirussu.flat_pagination_sorting_jdbc.application.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dev.dmitriirussu.flat_pagination_sorting_jdbc.application.OwnerReadRepository;
import dev.dmitriirussu.flat_pagination_sorting_jdbc.application.OwnerView;
import dev.dmitriirussu.flat_pagination_sorting_jdbc.application.PageQuery;
import dev.dmitriirussu.flat_pagination_sorting_jdbc.application.PageResult;

/**
 * JDBC implementation of {@link OwnerReadRepository}.
 */
@Repository
public class JdbcOwnerReadRepository implements OwnerReadRepository {

    private final JdbcClient jdbc;

    JdbcOwnerReadRepository(JdbcClient jdbc) { this.jdbc = jdbc; }

    /**
     * Maps domain field names to SQL column aliases.
     * SQL knowledge lives here — never in the application layer.
     */
    private static final Map<String, String> FIELD_MAP = Map.of(
            "id",   "o.id",
            "name", "o.name"
    );

    private interface Sql {
        String SELECT_PAGE = """
            SELECT o.id   AS owner_id,
                   o.name AS owner_name
            FROM owners o
            ORDER BY %s
            LIMIT :limit OFFSET :offset
            """;

        String COUNT_ALL = """
            SELECT COUNT(*) FROM owners
            """;
    }

    /**
     * Builds ORDER BY clause from domain sort requests.
     * orderBy contains only fields from FIELD_MAP + enum direction — no SQL injection possible.
     */
    private String buildOrderBy(PageQuery request) {
        if (request.sort().isEmpty()) return "o.id ASC";
        return request.sort().stream()
                .map(s -> FIELD_MAP.get(s.field()) + " " + s.direction().name())
                .collect(Collectors.joining(", "));
    }

    @Override
    public PageResult<OwnerView> findAllFlat(PageQuery request) {
        int offset = request.page() * request.size();
        String orderBy = buildOrderBy(request);

        List<OwnerView> content = jdbc.sql(Sql.SELECT_PAGE.formatted(orderBy))
                .param("limit",  request.size())
                .param("offset", offset)
                .query(OwnerProjection.class)
                .stream()
                .map(ViewMapper::toView)
                .toList();

        long total = jdbc.sql(Sql.COUNT_ALL).query(Long.class).single();

        return new PageResult<>(content, request.page(), request.size(), total);
    }
}