package dev.dmitriirussu.flat_pagination_sorting_jdbc.application;

import java.util.List;

/**
 * Pagination request model.
 *
 * @param page zero-based page index
 * @param size number of items per page
 * @param sort ordered list of sort requests
 */
public record PageQuery(int page, int size, List<SortRequest> sort) {

    public PageQuery {
        sort = List.copyOf(sort);
    }

    public static PageQuery of(int page, int size) {
        return new PageQuery(page, size, List.of());
    }

    public static PageQuery of(int page, int size, List<SortRequest> sort) {
        return new PageQuery(page, size, sort);
    }
}