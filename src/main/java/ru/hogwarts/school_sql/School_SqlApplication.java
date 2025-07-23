package ru.hogwarts.school_sql;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition
@SpringBootApplication
public class School_SqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(School_SqlApplication.class, args);
	}

}
