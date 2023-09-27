package com.example.users.api.web.controller;

import com.example.users.api.service.UserService;
import com.example.users.api.web.dto.UserCreationDto;
import com.example.users.api.web.dto.UserDto;
import com.example.users.api.web.dto.UserUpdateDto;
import com.example.users.api.web.mapper.UserMapper;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserMapper userMapper;

  @GetMapping("/{id}")
  public ResponseEntity<UserDto> findById(@PathVariable Long id) {
    return ResponseEntity.of(userService.findById(id).map(userMapper::toPayload));
  }

  @PostMapping
  public ResponseEntity<UserDto> create(@RequestBody UserCreationDto userDto) {
    var newUser = userService.create(userMapper.toEntity(userDto));
    return new ResponseEntity<>(userMapper.toPayload(newUser), HttpStatus.CREATED);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<UserDto> partialUpdate(@PathVariable Long id,
                                               @RequestBody UserUpdateDto userDto) {
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
