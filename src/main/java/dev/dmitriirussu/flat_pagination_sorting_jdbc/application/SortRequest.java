package dev.dmitriirussu.flat_pagination_sorting_jdbc.application;

import java.util.Set;

/**
 * Sort request model.
 *
 * <p>Uses domain field names — SQL mapping happens in the infrastructure layer.
 */
public record SortRequest(String field, Direction direction) {

    public enum Direction { ASC, DESC }

    private static final Set<String> ALLOWED_FIELDS = Set.of("id", "name");

    public SortRequest {
        if (!ALLOWED_FIELDS.contains(field))
            throw new IllegalArgumentException("Invalid sort field: " + field);
    }

    public static SortRequest asc(String field) {
        return new SortRequest(field, Direction.ASC);
    }

    public static SortRequest desc(String field) {
        return new SortRequest(field, Direction.DESC);
    }
}