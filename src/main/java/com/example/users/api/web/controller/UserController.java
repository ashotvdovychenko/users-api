package com.example.users.api.web.controller;

import com.example.users.api.service.UserService;
import com.example.users.api.web.dto.UserCreationDto;
import com.example.users.api.web.dto.UserDto;
import com.example.users.api.web.dto.UserUpdateDto;
import com.example.users.api.web.mapper.UserMapper;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserMapper userMapper;

  @GetMapping(params = {"birth_date_from", "birth_date_to"})
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
  public ResponseEntity<UserDto> findById(@PathVariable Long id) {
    return ResponseEntity.of(userService.findById(id).map(userMapper::toPayload));
  }

  @PostMapping
  public ResponseEntity<UserDto> create(@RequestBody @Valid UserCreationDto userDto) {
    var newUser = userService.create(userMapper.toEntity(userDto));
    return new ResponseEntity<>(userMapper.toPayload(newUser), HttpStatus.CREATED);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<UserDto> partialUpdate(@PathVariable Long id,
                                               @RequestBody @Valid UserUpdateDto userDto) {
    return ResponseEntity.of(userService.findById(id)
        .map(user -> userMapper.partialUpdate(userDto, user))
        .map(userService::update)
        .map(userMapper::toPayload));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable Long id) {
    userService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
