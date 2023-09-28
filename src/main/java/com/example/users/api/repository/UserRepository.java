package com.example.users.api.repository;

import com.example.users.api.domain.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  List<User> findAllByBirthDateBetween(LocalDate fromDate, LocalDate toDate);

  boolean existsByUsername(String username);
}
