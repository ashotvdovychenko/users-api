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
import java.security.Principal;
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
  @Operation(summary = "Get all users by birth date range", responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "400", content = @Content),
      @ApiResponse(responseCode = "403", content = @Content)
  })
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
      @ApiResponse(responseCode = "403", content = @Content),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<UserDto> findById(@PathVariable Long id) {
    return ResponseEntity.of(userService.findById(id).map(userMapper::toPayload));
  }

  @PatchMapping("/self")
  @Operation(summary = "Update your user account partially", responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "400", content = @Content),
      @ApiResponse(responseCode = "403", content = @Content),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<UserDto> partialUpdateSelf(@RequestBody @Valid UserUpdateDto userDto,
                                                   Principal principal) {
    return ResponseEntity.of(userService.findByUsername(principal.getName())
        .map(user -> userMapper.partialUpdate(userDto, user))
        .map(userService::update)
        .map(userMapper::toPayload));
  }

  @PutMapping("/self")
  @Operation(summary = "Update your user account fully", responses = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "400", content = @Content),
      @ApiResponse(responseCode = "403", content = @Content),
      @ApiResponse(responseCode = "404", content = @Content)
  })
  public ResponseEntity<UserDto> fullUpdateSelf(@RequestBody @Valid UserCreationDto userDto,
                                                Principal principal) {
    return ResponseEntity.of(userService.findByUsername(principal.getName())
        .map(user -> userMapper.fullUpdate(userDto, user))
        .map(userService::update)
        .map(userMapper::toPayload));
  }

  @DeleteMapping("/self")
  @Operation(summary = "Delete your user account", responses = {
      @ApiResponse(responseCode = "204", content = @Content),
      @ApiResponse(responseCode = "403", content = @Content),
  })
  public ResponseEntity<Void> deleteSelf(Principal principal) {
    userService.deleteByUsername(principal.getName());
    return ResponseEntity.noContent().build();
  }
}
