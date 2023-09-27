package com.example.users.api.web.dto;

import lombok.Data;

@Data
public class UserDto {
  private Long id;
  private String firstName;
  private String lastName;
  private String username;
  private String email;
  private String birthDate;
  private String address;
  private String phoneNumber;
}
