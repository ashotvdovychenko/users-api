package com.example.users.api.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.users.api.domain.User;
import com.example.users.api.exception.UserAlreadyExistsException;
import com.example.users.api.repository.UserRepository;
import com.example.users.api.security.JwtTokenProvider;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  @Getter
  @Setter
  @Value("${min.user.age}")
  private int minAge;

  @Override
  @Transactional
  public User create(User user) {
    if (userRepository.existsByUsername(user.getUsername())) {
      throw new UserAlreadyExistsException(
          "Username %s is already in use".formatted(user.getUsername()));
    }
    if (isAgeNotAllowed(user.getBirthDate())) {
      throw new IllegalArgumentException(
          "Min age must be equal or higher than %d".formatted(minAge));
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  @Override
  public Optional<DecodedJWT> signIn(String username, String password) {
    if (!existsByCredentials(username, password)) {
      throw new BadCredentialsException("Invalid username/password supplied");
    }
    return jwtTokenProvider.toDecodedJWT(jwtTokenProvider.generateToken(username));
  }

  @Override
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public User update(User updatedUser) {
    if (isUsernameInUse(updatedUser)) {
      throw new UserAlreadyExistsException(
          "Username %s is already in use".formatted(updatedUser.getUsername()));
    }
    if (isAgeNotAllowed(updatedUser.getBirthDate())) {
      throw new IllegalArgumentException(
          "Min age must be equal or higher than %d".formatted(minAge));
    }
    updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
    return userRepository.save(updatedUser);
  }

  @Override
  public Optional<User> findById(Long id) {
    return userRepository.findById(id);
  }

  @Override
  public List<User> findAllByBirthDateRange(LocalDate birthDateFrom, LocalDate birthDateTo) {
    if (birthDateFrom.isAfter(birthDateTo)) {
      throw new IllegalArgumentException(
          "Date %s is not later than %s".formatted(birthDateTo, birthDateFrom));
    }
    return userRepository.findAllByBirthDateBetween(birthDateFrom, birthDateTo);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  @Transactional
  public void deleteByUsername(String username) {
    userRepository.deleteByUsername(username);
  }

  private boolean isUsernameInUse(User user) {
    return userRepository.findByUsername(user.getUsername())
        .filter(found -> !found.getId().equals(user.getId())).isPresent();
  }

  private boolean isAgeNotAllowed(LocalDate birthDate) {
    return birthDate.isAfter(LocalDate.now().minusYears(minAge));
  }

  private Optional<User> findByCredentials(String username, String password) {
    var maybeUser = userRepository.findByUsername(username);
    if (maybeUser.isPresent()) {
      if (passwordEncoder.matches(password, maybeUser.get().getPassword())) {
        return maybeUser;
      }
    }
    return Optional.empty();
  }

  private boolean existsByCredentials(String username, String password) {
    return findByCredentials(username, password).isPresent();
  }
}
