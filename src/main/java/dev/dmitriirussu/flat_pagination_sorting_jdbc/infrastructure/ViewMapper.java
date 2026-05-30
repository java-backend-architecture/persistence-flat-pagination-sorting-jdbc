package dev.dmitriirussu.flat_pagination_sorting_jdbc.infrastructure;

import dev.dmitriirussu.flat_pagination_sorting_jdbc.application.OwnerView;

/**
 * Maps persistence projections to application read models.
 */
final class ViewMapper {

    private ViewMapper() {}

    public static OwnerView toView(OwnerProjection owner) {
        return new OwnerView(owner.id(), owner.name());
    }
}