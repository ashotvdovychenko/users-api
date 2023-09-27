package com.example.users.api.service;

import com.example.users.api.domain.User;
import com.example.users.api.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

  private final UserRepository userRepository;

  @Override
  public User create(User user) {
    return userRepository.save(user);
  }

  @Override
  public User updateOne(User updatedUser) {
    return userRepository.save(updatedUser);
  }

  @Override
  public Optional<User> findById(Long id) {
    return userRepository.findById(id);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public void deleteOne(Long id) {
    userRepository.deleteById(id);
  }
}
