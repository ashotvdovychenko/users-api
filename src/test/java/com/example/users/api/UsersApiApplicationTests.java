package com.example.users.api;

import com.example.users.api.testcontainers.TestcontainersInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestcontainersInitializer.class)
class UsersApiApplicationTests {
  @Test
  void contextLoads() {
  }
}
