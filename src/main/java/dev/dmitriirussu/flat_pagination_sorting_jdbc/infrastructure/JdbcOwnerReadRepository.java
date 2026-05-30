package dev.dmitriirussu.flat_pagination_sorting_jdbc.infrastructure;

import dev.dmitriirussu.flat_pagination_sorting_jdbc.application.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JDBC implementation of {@link OwnerReadRepository}.
 */
@Repository
public class JdbcOwnerReadRepository implements OwnerReadRepository {

    private final JdbcClient jdbc;

    JdbcOwnerReadRepository(JdbcClient jdbc) { this.jdbc = jdbc; }

    /** SQL queries for owner read operations. */
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

    @Override
    public PageResult<OwnerView> findAllFlat(PageRequest request) {

        int offset = request.page() * request.size();

        // если сортировки нет — дефолт
        String orderBy = request.sort().isEmpty()
                ? "o.id ASC"
                : request.sort().stream()
                .map(SortRequest::toSql)
                .collect(Collectors.joining(", "));

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
