package com.example.users.api.service;

import com.example.users.api.domain.User;
import java.util.Optional;

public interface UserService {
  User create(User user);

  User updateOne(User updatedUser);

  Optional<User> findById(Long id);

  Optional<User> findByUsername(String username);

  void deleteOne(Long id);
}
