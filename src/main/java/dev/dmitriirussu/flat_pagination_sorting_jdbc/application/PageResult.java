package dev.dmitriirussu.flat_pagination_sorting_jdbc.application;

import java.util.List;
import java.util.function.Function;

/**
 * Generic page result wrapper.
 *
 * @param <T> type of content in page
 */
public record PageResult<T>(List<T> content, int page, int size, long total) {}