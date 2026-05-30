package dev.dmitriirussu.flat_pagination_sorting_jdbc.application;

import java.util.List;

/**
 * Pagination request model.
 *
 * @param page zero-based page index
 * @param size number of items per page
 */
public record PageRequest(int page, int size, List<SortRequest> sort) {

    public PageRequest {
        sort = List.copyOf(sort);
    }

    // без сортировки
    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size, List.of());
    }

    // одна сортировка
    public static PageRequest of(int page, int size, SortRequest sort) {
        return new PageRequest(page, size, List.of(sort));
    }

    // несколько сортировок
    public static PageRequest of(int page, int size, List<SortRequest> sort) {
        return new PageRequest(page, size, sort);
    }
}
