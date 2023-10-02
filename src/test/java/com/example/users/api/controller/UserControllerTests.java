package com.example.users.api.controller;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.users.api.repository.UserRepository;
import com.example.users.api.testcontainers.TestcontainersInitializer;
import com.example.users.api.web.dto.UserCreationDto;
import com.example.users.api.web.dto.UserUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestcontainersInitializer.class)
@WithMockUser("first")
public class UserControllerTests {
  private final String url = "users";

  private final String selfUrl = "/users/self";

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
  @Sql("/users-create.sql")
  public void findByIdWhenExists() throws Exception {
    var id = 1L;

    var result = mockMvc.perform(get("/{url}/{id}", url, id));

    result.andExpectAll(
        status().isOk(),
        jsonPath("$.id").value(id));
  }

  @Test
  @Sql("/users-create.sql")
  public void findByIdWhenNotExists() throws Exception {
    var id = 20L;

    var result = mockMvc.perform(get("/{url}/{id}", url, id));

    result.andExpect(status().isNotFound());
  }

  @Test
  @Sql("/users-create.sql")
  public void findByBirthDateRangeWithSomeUsersInRange() throws Exception {
    var from = "1998-01-01";
    var to = "2002-01-01";

    var result = mockMvc
        .perform(get("/{url}?birth_date_from={from}&birth_date_to={to}", url, from, to));

    result.andExpectAll(
        status().isOk(),
        jsonPath("$").value(hasSize(3)));
  }

  @Test
  @Sql("/users-create.sql")
  public void findByBirthDateRangeWithNoUsersInRange() throws Exception {
    var from = "2007-01-01";
    var to = "2012-01-01";

    var result = mockMvc
        .perform(get("/{url}?birth_date_from={from}&birth_date_to={to}", url, from, to));

    result.andExpectAll(
        status().isOk(),
        jsonPath("$").value(hasSize(0)));
  }

  @Test
  @Sql("/users-create.sql")
  public void findByBirthDateRangeWithInvalidRange() throws Exception {
    var from = "2002-01-01";
    var to = "1998-01-01";

    var result = mockMvc
        .perform(get("/{url}?birth_date_from={from}&birth_date_to={to}", url, from, to));

    result.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.message").value("Date %s is not later than %s".formatted(to, from)));
  }

  @Test
  @Sql("/users-create.sql")
  public void partialUpdateSelfWithNewAddress() throws Exception {
    var newAddress = "First user updated address";
    var user = new UserUpdateDto();
    user.setAddress(newAddress);
    var json = mapper.writeValueAsString(user);

    var result = mockMvc.perform(patch(selfUrl)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));

    result.andExpectAll(
        status().isOk(),
        jsonPath("$.id").value(1L),
        jsonPath("$.address").value(newAddress));
  }

  @Test
  @Sql("/users-create.sql")
  public void partialUpdateSelfWithAlreadyUsedUsername() throws Exception {
    var username = "third";
    var user = new UserUpdateDto();
    user.setUsername(username);
    var json = mapper.writeValueAsString(user);

    var result = mockMvc.perform(patch(selfUrl)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));

    result.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.message").value("Username %s is already in use".formatted(username)));
  }

  @Test
  @Sql("/users-create.sql")
  public void partialUpdateSelfWithNotAllowedBirthDate() throws Exception {
    var birthDate = LocalDate.now().minusYears(1);
    var user = new UserUpdateDto();
    user.setBirthDate(birthDate);
    mapper.findAndRegisterModules();
    var json = mapper.writeValueAsString(user);

    var result = mockMvc.perform(patch(selfUrl)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));

    result.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.message").value("Min age must be equal or higher than 18"));
  }

  @Test
  @Sql("/users-create.sql")
  public void fullUpdateSelf() throws Exception {
    var user = getUser("first", "password", "email@mail.com",
        LocalDate.parse("2000-11-11"), "John", "Doe",
        "First user address", "(404) 12-34-456");
    mapper.findAndRegisterModules();
    var json = mapper.writeValueAsString(user);

    var result = mockMvc.perform(put(selfUrl)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));

    result.andExpectAll(
        status().isOk(),
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
  public void fullUpdateSelfWithAlreadyUsedUsername() throws Exception {
    var user = getUser("third", "password", "email@mail.com",
        LocalDate.parse("2000-11-11"), "John", "Doe",
        "First user address", "(404) 12-34-456");
    mapper.findAndRegisterModules();
    var json = mapper.writeValueAsString(user);

    var result = mockMvc.perform(put(selfUrl)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));

    result.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.message").value("Username %s is already in use".formatted(user.getUsername())));
  }

  @Test
  @Sql("/users-create.sql")
  public void fullUpdateSelfWithNotAllowedBirthDate() throws Exception {
    var user = getUser("username", "asdasdasd", "mail@mail.com",
        LocalDate.now().minusYears(1), "Robert", "Smith",
        "User address", "(204) 12-34-456");
    mapper.findAndRegisterModules();
    var json = mapper.writeValueAsString(user);

    var result = mockMvc.perform(put(selfUrl)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));

    result.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.message").value("Min age must be equal or higher than 18"));
  }

  @Test
  @Sql("/users-create.sql")
  public void deleteSelf() throws Exception {
    var result = mockMvc.perform(delete(selfUrl));
    var deletedUser = userRepository.findByUsername("first");

    result.andExpect(status().isNoContent());
    Assertions.assertThat(deletedUser).isEmpty();
  }

  private UserCreationDto getUser(String username, String password, String email,
                                  LocalDate birthDate, String firstName, String lastName,
                                  String address, String phoneNumber) {
    return new UserCreationDto(firstName, lastName, username, email,
        password, birthDate, address, phoneNumber);
  }
}
