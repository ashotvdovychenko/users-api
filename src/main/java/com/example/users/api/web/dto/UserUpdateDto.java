package com.example.users.api.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
public class UserUpdateDto {
  @Pattern(regexp = "^[a-zA-Z-]+$", message = "You can use a-z and dashes")
  @Schema(example = "John")
  private String firstName;

  @Pattern(regexp = "^[a-zA-Z-]+$", message = "You can use a-z and dashes")
  @Schema(example = "Doe")
  private String lastName;

  @Pattern(regexp = "^\\w+$", message = "You can use a-z, 0-9 and underscores")
  @Size(min = 4, max = 32, message = "Enter at least 4 and less than 32 characters")
  @Schema(example = "string")
  private String username;

  @Email(message = "Enter correct email")
  @Size(min = 5, message = "Enter at least 5 characters")
  @Schema(example = "string")
  private String email;

  @Past(message = "Birth date must be a past")
  @Schema(example = "2012-12-12")
  private LocalDate birthDate;

  @Size(min = 6, max = 32, message = "Enter at least 6 and less than 32 characters")
  @Schema(example = "string")
  private String password;

  @Schema(example = "string")
  private String address;

  @Schema(example = "+380123456789")
  private String phoneNumber;
}
