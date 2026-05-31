package dev.dmitriirussu.flat_pagination_sorting_jdbc.infrastructure;


import dev.dmitriirussu.flat_pagination_sorting_jdbc.application.OwnerView;
import dev.dmitriirussu.flat_pagination_sorting_jdbc.application.PageRequest;
import dev.dmitriirussu.flat_pagination_sorting_jdbc.application.PageResult;
import dev.dmitriirussu.flat_pagination_sorting_jdbc.application.SortRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
class JdbcOwnerReadRepositoryTest {

    @Autowired
    JdbcClient jdbc;

    JdbcOwnerReadRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JdbcOwnerReadRepository(jdbc);
    }

    // ── pagination ─────────────────────────────────────────────

    @Test
    void findAllFlat_returnsRequestedPage() {
        PageResult<OwnerView> result = repository.findAllFlat(PageRequest.of(0, 2));

        assertThat(result.content()).hasSize(2);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.total()).isEqualTo(10);
    }

    @Test
    void findAllFlat_returnsNextPage() {
        PageResult<OwnerView> result = repository.findAllFlat(PageRequest.of(1, 2));

        assertThat(result.content()).hasSize(2);
        assertThat(result.content().get(0).name()).isEqualTo("jack3");
        assertThat(result.content().get(1).name()).isEqualTo("jack4");
    }

    @Test
    void findAllFlat_returnsLastPage_withRemainder() {
        PageResult<OwnerView> result = repository.findAllFlat(PageRequest.of(3, 3));

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).name()).isEqualTo("jack10");
        assertThat(result.total()).isEqualTo(10);
    }

    @Test
    void findAllFlat_returnsEmptyContent_whenPageBeyondTotal() {
        PageResult<OwnerView> result = repository.findAllFlat(PageRequest.of(99, 10));

        assertThat(result.content()).isEmpty();
        assertThat(result.total()).isEqualTo(10);
    }

    @Test
    void findAllFlat_totalIsAlwaysCorrect_regardlessOfPage() {
        PageResult<OwnerView> page0 = repository.findAllFlat(PageRequest.of(0, 3));
        PageResult<OwnerView> page1 = repository.findAllFlat(PageRequest.of(1, 3));

        assertThat(page0.total()).isEqualTo(10);
        assertThat(page1.total()).isEqualTo(10);
    }

    // ── sorting ────────────────────────────────────────────────

    @Test
    void findAllFlat_noSort_defaultsToIdAsc() {
        PageResult<OwnerView> result = repository.findAllFlat(PageRequest.of(0, 3));

        assertThat(result.content().get(0).id()).isEqualTo(1L);
        assertThat(result.content().get(1).id()).isEqualTo(2L);
        assertThat(result.content().get(2).id()).isEqualTo(3L);
    }

    @Test
    void findAllFlat_sortByIdDesc() {
        PageResult<OwnerView> result = repository.findAllFlat(
                PageRequest.of(0, 3, SortRequest.desc("o.id"))
        );

        assertThat(result.content().get(0).id()).isEqualTo(10L);
        assertThat(result.content().get(1).id()).isEqualTo(9L);
        assertThat(result.content().get(2).id()).isEqualTo(8L);
    }

    @Test
    void findAllFlat_sortByNameAsc() {
        PageResult<OwnerView> result = repository.findAllFlat(
                PageRequest.of(0, 3, SortRequest.asc("o.name"))
        );

        // jack1, jack10, jack2 — lexicographic order
        assertThat(result.content().get(0).name()).isEqualTo("jack1");
        assertThat(result.content().get(1).name()).isEqualTo("jack10");
        assertThat(result.content().get(2).name()).isEqualTo("jack2");
    }

    @Test
    void findAllFlat_multiFieldSort() {
        PageResult<OwnerView> result = repository.findAllFlat(
                PageRequest.of(0, 3, List.of(
                        SortRequest.asc("o.name"),
                        SortRequest.desc("o.id")
                ))
        );

        assertThat(result.content()).hasSize(3);
        assertThat(result.total()).isEqualTo(10);
    }

    // ── SortRequest validation ─────────────────────────────────

    @Test
    void sortRequest_throwsOnInvalidField() {
        assertThatThrownBy(() -> SortRequest.asc("o.invalid_field"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid sort field");
    }

    @Test
    void sortRequest_throwsOnSqlInjectionAttempt() {
        assertThatThrownBy(() -> SortRequest.asc("o.id; DROP TABLE owners"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid sort field");
    }
}

