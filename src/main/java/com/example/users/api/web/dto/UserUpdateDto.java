package com.example.users.api.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
public class UserUpdateDto {
  @Pattern(regexp = "^[a-zA-Z-]+$", message = "You can use a-z and dashes")
  private String firstName;

  @Pattern(regexp = "^[a-zA-Z-]+$", message = "You can use a-z and dashes")
  private String lastName;

  @Pattern(regexp = "^\\w+$", message = "You can use a-z, 0-9 and underscores")
  @Size(min = 4, max = 32, message = "Enter at least 4 and less than 32 characters")
  private String username;

  @Email(message = "Enter correct email")
  @Size(min = 5, message = "Enter at least 5 characters")
  private String email;

  private LocalDate birthDate;

  @Size(min = 6, max = 32, message = "Enter at least 6 and less than 32 characters")
  private String password;

  private String address;

  private String phoneNumber;
}
