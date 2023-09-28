package com.example.users.api.service;

import com.example.users.api.domain.User;
import com.example.users.api.exception.UserAlreadyExistsException;
import com.example.users.api.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  @Transactional
  public User create(User user) {
    if (userRepository.existsByUsername(user.getUsername())) {
      throw new UserAlreadyExistsException(
          "Username %s is already in use".formatted(user.getUsername()));
    }
    return userRepository.save(user);
  }

  @Override
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public User update(User updatedUser) {
    if (isUsernameInUse(updatedUser)) {
      throw new UserAlreadyExistsException(
          "Username %s is already in use".formatted(updatedUser.getUsername()));
    }
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
  public void deleteById(Long id) {
    userRepository.deleteById(id);
  }

  private boolean isUsernameInUse(User user) {
    return userRepository.findByUsername(user.getUsername())
        .filter(found -> !found.getId().equals(user.getId())).isPresent();
  }
}
