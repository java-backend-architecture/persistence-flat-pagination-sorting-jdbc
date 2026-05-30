package dev.dmitriirussu.flat_pagination_sorting_jdbc;

import dev.dmitriirussu.flat_pagination_sorting_jdbc.application.OwnerReadRepository;
import dev.dmitriirussu.flat_pagination_sorting_jdbc.application.PageRequest;
import dev.dmitriirussu.flat_pagination_sorting_jdbc.application.SortRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class FlatPaginationSortingJdbcApp {
	public static void main(String[] args) {
		SpringApplication.run(FlatPaginationSortingJdbcApp.class, args);
	}
	// Demo output for manual verification of graph extraction queries
	@Bean
	CommandLineRunner demo(OwnerReadRepository repository) {
		return args -> {

			// без сортировки — дефолт o.id ASC
			PageRequest.of(0, 10);
			// по имени
			PageRequest.of(0, 10, SortRequest.asc("o.name"));

			// по нескольким полям
			PageRequest.of(0, 10, List.of(
					SortRequest.asc("o.name"),
					SortRequest.desc("o.id")
			));

			System.out.println(repository.findAllFlat(PageRequest.of(0, 2)));

		};
	}
}
