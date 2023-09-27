package com.example.users.api.service;

import com.example.users.api.domain.User;
import java.util.Optional;

public interface UserService {
  User create(User user);

  User update(User updatedUser);

  Optional<User> findById(Long id);

  Optional<User> findByUsername(String username);

  void deleteById(Long id);
}
