package com.example.users.api.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationDto {
  @NotBlank(message = "Specify first name")
  @Schema(example = "John")
  private String firstName;

  @NotBlank(message = "Specify last name")
  @Schema(example = "Doe")
  private String lastName;

  @NotBlank(message = "Specify username")
  @Pattern(regexp = "^\\w+$", message = "You can use a-z, 0-9 and underscores")
  @Size(min = 4, max = 32, message = "Enter at least 4 and less than 32 characters")
  @Schema(example = "string")
  private String username;

  @NotBlank(message = "Specify email")
  @Email(message = "Enter correct email")
  @Schema(example = "string@mail.com")
  private String email;

  @NotBlank(message = "Specify password")
  @Size(min = 6, max = 32, message = "Enter at least 6 and less than 32 characters")
  @Schema(example = "string")
  private String password;

  @NotNull(message = "Specify birth date")
  @Past(message = "Birth date must be a past")
  @Schema(example = "2012-12-12")
  private LocalDate birthDate;

  @Schema(example = "string")
  private String address;

  @Schema(example = "+380123456789")
  private String phoneNumber;
}
