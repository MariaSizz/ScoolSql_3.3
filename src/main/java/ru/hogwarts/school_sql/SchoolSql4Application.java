package ru.hogwarts.school_sql;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition
@SpringBootApplication
public class SchoolSql4Application {

	public static void main(String[] args) {
		SpringApplication.run(SchoolSql4Application.class, args);
	}

}
