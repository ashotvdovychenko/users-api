package com.example.users.api.web.controller;

import com.example.users.api.service.UserService;
import com.example.users.api.web.dto.UserCreationDto;
import com.example.users.api.web.dto.UserDto;
import com.example.users.api.web.dto.UserUpdateDto;
import com.example.users.api.web.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Controller")
@RestController
@RequestMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserMapper userMapper;

  @GetMapping(params = {"birth_date_from", "birth_date_to"})
  @Operation(summary = "Get all users by birth date range")
  public ResponseEntity<List<UserDto>> findByBirthDateRange(@RequestParam(name = "birth_date_from")
                                                            LocalDate birthDateFrom,
                                                            @RequestParam(name = "birth_date_to")
                                                            LocalDate birthDateTo) {
    return ResponseEntity.ok(userService.findAllByBirthDateRange(birthDateFrom, birthDateTo)
        .stream()
        .map(userMapper::toPayload)
        .toList());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get user by id", responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<UserDto> findById(@PathVariable Long id) {
    return ResponseEntity.of(userService.findById(id).map(userMapper::toPayload));
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Update user partially", responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<UserDto> partialUpdate(@PathVariable Long id,
                                               @RequestBody @Valid UserUpdateDto userDto) {
    return ResponseEntity.of(userService.findById(id)
        .map(user -> userMapper.partialUpdate(userDto, user))
        .map(userService::update)
        .map(userMapper::toPayload));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update user fully", responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<UserDto> fullUpdate(@PathVariable Long id,
                                            @RequestBody @Valid UserCreationDto userDto) {
    return ResponseEntity.of(userService.findById(id)
        .map(user -> userMapper.fullUpdate(userDto, user))
        .map(userService::update)
        .map(userMapper::toPayload));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete user by id",
      responses = @ApiResponse(responseCode = "204", content = @Content))
  public ResponseEntity<Void> deleteById(@PathVariable Long id) {
    userService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
