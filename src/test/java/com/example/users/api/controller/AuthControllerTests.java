package com.example.users.api.controller;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.users.api.repository.UserRepository;
import com.example.users.api.web.dto.Credentials;
import com.example.users.api.web.dto.UserCreationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthControllerTests {
  private final String signUp = "/auth/sign-up";

  private final String signIn = "/auth/sign-in";

  private final ObjectMapper mapper = new ObjectMapper();

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @AfterEach
  public void cleanAll() {
    userRepository.deleteAll();
  }

  @Test
  public void signUp() throws Exception {
    var user = getUser("first", "password", "email@mail.com",
        LocalDate.parse("2000-11-11"), "John", "Doe",
        "First user address", "(404) 12-34-456");
    mapper.findAndRegisterModules();
    var json = mapper.writeValueAsString(user);

    var result = mockMvc.perform(post(signUp)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));

    result.andExpectAll(
        status().isCreated(),
        jsonPath("$.id").value(1L),
        jsonPath("$.username").value(user.getUsername()),
        jsonPath("$.firstName").value(user.getFirstName()),
        jsonPath("$.lastName").value(user.getLastName()),
        jsonPath("$.email").value(user.getEmail()),
        jsonPath("$.birthDate").value(user.getBirthDate().format(ofPattern("dd-MM-yyyy"))),
        jsonPath("$.phoneNumber").value(user.getPhoneNumber()),
        jsonPath("$.address").value(user.getAddress()));
  }

  @Test
  @Sql("/users-create.sql")
  public void signUpWithAlreadyUsedUsername() throws Exception {
    var user = getUser("first", "password", "email@mail.com",
        LocalDate.parse("2000-11-11"), "John", "Doe",
        "First user address", "(404) 12-34-456");
    mapper.findAndRegisterModules();
    var json = mapper.writeValueAsString(user);

    var result = mockMvc.perform(post(signUp)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));

    result.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.message").value("Username %s is already in use".formatted(user.getUsername())));
  }

  @Test
  public void signUpWithNotAllowedBirthDate() throws Exception {
    var user = getUser("username", "asdasdasd", "mail@mail.com",
        LocalDate.now().minusYears(1), "Robert", "Smith",
        "User address", "(204) 12-34-456");
    mapper.findAndRegisterModules();
    var json = mapper.writeValueAsString(user);

    var result = mockMvc.perform(post(signUp)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));

    result.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.message").value("Min age must be equal or higher than 18"));
  }

  @Test
  @Sql("/users-create.sql")
  public void signInWithCorrectCredentials() throws Exception {
    var credentials = new Credentials("first", "password");
    var json = mapper.writeValueAsString(credentials);

    var result = mockMvc.perform(post(signIn)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));

    result.andExpectAll(
        status().isOk(),
        jsonPath("$.token").value(notNullValue()));
  }

  @Test
  public void signInWithNotExistingUsername() throws Exception {
    var credentials = new Credentials("first", "password");
    var json = mapper.writeValueAsString(credentials);

    var result = mockMvc.perform(post(signIn)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));

    result.andExpectAll(
        status().isForbidden(),
        jsonPath("$.message").value("Invalid username/password supplied"));
  }

  @Test
  @Sql("/users-create.sql")
  public void signInWithInvalidPassword() throws Exception {
    var credentials = new Credentials("first", "invalid_password");
    var json = mapper.writeValueAsString(credentials);

    var result = mockMvc.perform(post(signIn)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));

    result.andExpectAll(
        status().isForbidden(),
        jsonPath("$.message").value("Invalid username/password supplied"));
  }

  private UserCreationDto getUser(String username, String password, String email,
                                  LocalDate birthDate, String firstName, String lastName,
                                  String address, String phoneNumber) {
    return new UserCreationDto(firstName, lastName, username, email,
        password, birthDate, address, phoneNumber);
  }
}
