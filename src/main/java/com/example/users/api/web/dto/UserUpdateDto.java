package com.example.users.api.web.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class UserUpdateDto {
  private String firstName;
  private String lastName;
  private String username;
  private String email;
  private LocalDate birthDate;
  private String address;
  private String phoneNumber;
}
