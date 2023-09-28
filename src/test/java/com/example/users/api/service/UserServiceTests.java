package com.example.users.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.users.api.domain.User;
import com.example.users.api.exception.UserAlreadyExistsException;
import com.example.users.api.repository.UserRepository;
import com.example.users.api.security.JwtTokenProvider;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
  @Mock
  private UserRepository userRepository;

  @Spy
  private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

  @Spy
  private JwtTokenProvider jwtTokenProvider = new JwtTokenProvider("B7B52", "users-api");

  @InjectMocks
  private UserServiceImpl userService;

  private User firstUser;

  private User secondUser;

  @BeforeEach
  public void init() {
    userService.setMinAge(18);
    firstUser =
        getUser("first_username", "password", "first@mail.com",
            LocalDate.parse("2000-12-12"), "John", "Doe");
    secondUser = getUser("second_username", "password", "second@mail.com",
        LocalDate.parse("2002-12-12"), "John", "Doe");
  }

  private User getUser(String username, String password, String email,
                       LocalDate birthDate, String firstName, String lastName) {
    var user = new User();
    user.setPassword(username);
    user.setUsername(password);
    user.setEmail(email);
    user.setBirthDate(birthDate);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    return user;
  }

  @Test
  public void creatingWithUnusedUsernameAndCorrectBirthDate() {
    var rawPassword = firstUser.getPassword();
    when(userRepository.save(any(User.class))).thenAnswer(invoke -> invoke.getArgument(0));
    when(userRepository.existsByUsername(any(String.class))).thenReturn(false);

    var createdUser = userService.create(firstUser);

    assertThat(createdUser).isNotNull();
    assertThat(passwordEncoder.matches(rawPassword, createdUser.getPassword())).isTrue();
  }

  @Test
  public void creatingWithUsedUsername() {
    when(userRepository.existsByUsername(any(String.class))).thenReturn(true);

    assertThatThrownBy(() -> userService.create(firstUser))
        .isInstanceOf(UserAlreadyExistsException.class)
        .hasMessage("Username %s is already in use".formatted(firstUser.getUsername()));
  }

  @Test
  public void creatingWithNotAllowedBirthDate() {
    when(userRepository.existsByUsername(any(String.class))).thenReturn(false);
    firstUser.setBirthDate(LocalDate.now());

    assertThatThrownBy(() -> userService.create(firstUser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Min age must be equal or higher than 18");
  }

  @Test
  public void signingInWithMatchingCredentials() {
    var rawPassword = firstUser.getPassword();
    firstUser.setPassword(passwordEncoder.encode(rawPassword));
    when(userRepository.findByUsername(firstUser.getUsername())).thenReturn(Optional.of(firstUser));

    var token = userService.signIn(firstUser.getUsername(), rawPassword);

    assertThat(token
        .map(DecodedJWT::getToken)
        .map(jwtTokenProvider::getUsernameFromToken))
        .hasValue(firstUser.getUsername());
  }

  @Test
  public void signingInWithNotMatchingCredentials() {
    when(userRepository.findByUsername(firstUser.getUsername())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.signIn(firstUser.getUsername(), firstUser.getPassword()))
        .isInstanceOf(BadCredentialsException.class)
        .hasMessage("Invalid username/password supplied");
  }

  @Test
  public void updatingWithNewUsernameUnused() {
    var rawPassword = firstUser.getPassword();
    when(userRepository.save(any(User.class))).thenAnswer(invoke -> invoke.getArgument(0));
    when(userRepository.findByUsername(firstUser.getUsername())).thenReturn(Optional.empty());

    var updatedUser = userService.update(firstUser);

    assertThat(updatedUser).isNotNull();
    assertThat(passwordEncoder.matches(rawPassword, updatedUser.getPassword())).isTrue();
  }

  @Test
  public void updatingWithNewUsernameUsedByThisUser() {
    firstUser.setId(1L);
    var rawPassword = firstUser.getPassword();
    when(userRepository.save(any(User.class))).thenAnswer(invoke -> invoke.getArgument(0));
    when(userRepository.findByUsername(firstUser.getUsername())).thenReturn(Optional.of(firstUser));

    var updatedUser = userService.update(firstUser);

    assertThat(updatedUser).isNotNull();
    assertThat(passwordEncoder.matches(rawPassword, updatedUser.getPassword())).isTrue();
  }

  @Test
  public void updatingWithNewUsernameUsedByOtherUser() {
    var otherUser = new User();
    otherUser.setId(2L);
    when(userRepository.findByUsername(firstUser.getUsername())).thenReturn(Optional.of(otherUser));

    assertThatThrownBy(() -> userService.update(firstUser))
        .isInstanceOf(UserAlreadyExistsException.class)
        .hasMessage("Username %s is already in use".formatted(firstUser.getUsername()));
  }

  @Test
  public void updatingWithNewDateIsNotAllowed() {
    firstUser.setBirthDate(LocalDate.now());
    when(userRepository.findByUsername(firstUser.getUsername())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.update(firstUser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Min age must be equal or higher than 18");
  }

  @Test
  public void findingAllUsersByBirthDateRangeWithAllUsersInRange() {
    var startDate = firstUser.getBirthDate().minusYears(1);
    var endDate = secondUser.getBirthDate().plusYears(1);
    when(userRepository.findAllByBirthDateBetween(startDate, endDate))
        .thenReturn(List.of(firstUser, secondUser));

    var users = userService.findAllByBirthDateRange(startDate, endDate);

    assertThat(users).hasSize(2);
    assertThat(users.get(0)).matches(
        user -> user.getBirthDate().isAfter(startDate) && user.getBirthDate().isBefore(endDate));
    assertThat(users.get(1)).matches(
        user -> user.getBirthDate().isAfter(startDate) && user.getBirthDate().isBefore(endDate));
  }

  @Test
  public void findingAllUsersByBirthDateRangeWithOneUserInRange() {
    var startDate = firstUser.getBirthDate().plusYears(1);
    var endDate = secondUser.getBirthDate().plusYears(1);
    when(userRepository.findAllByBirthDateBetween(startDate, endDate))
        .thenReturn(List.of(secondUser));

    var users = userService.findAllByBirthDateRange(startDate, endDate);

    assertThat(users).hasSize(1);
    assertThat(users.get(0)).matches(
        user -> user.getBirthDate().isAfter(startDate) && user.getBirthDate().isBefore(endDate));
  }

  @Test
  public void findingAllUsersByBirthDateRangeWithoutUsersInRange() {
    var startDate = firstUser.getBirthDate().plusYears(1);
    var endDate = secondUser.getBirthDate().minusYears(1);
    when(userRepository.findAllByBirthDateBetween(startDate, endDate))
        .thenReturn(Collections.emptyList());

    var users = userService.findAllByBirthDateRange(startDate, endDate);

    assertThat(users).hasSize(0);
  }
}
