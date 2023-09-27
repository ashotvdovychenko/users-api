package com.example.users.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(title = "Users API", version = "0.1",
        description = """
            An API for the application implementing abilities to create, read, manage and other
            general users manipulations."""
    )
)
public class UsersApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(UsersApiApplication.class, args);
  }
}
