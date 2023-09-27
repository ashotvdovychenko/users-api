package com.example.users.api.service;

import com.example.users.api.domain.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserService {
  User create(User user);

  User update(User updatedUser);

  Optional<User> findById(Long id);

  List<User> findAllByBirthDateRange(LocalDate birthDateFrom, LocalDate birthDateTo);

  Optional<User> findByUsername(String username);

  void deleteById(Long id);
}
