package com.example.demo;

import java.util.stream.Stream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	@Bean
	ApplicationListener<ApplicationReadyEvent> basicsApplicationListener(TodoRepository repository) {
		return event->repository
			.saveAll(Stream.of("A", "B", "C").map(name->new Todo("configuration", "congratulations, you have set up correctly!", true)).toList())
			.forEach(System.out::println);
	}
}