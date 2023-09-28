package com.example.users.api.web.controller;

import com.example.users.api.service.UserService;
import com.example.users.api.web.dto.Credentials;
import com.example.users.api.web.dto.JwtToken;
import com.example.users.api.web.dto.UserCreationDto;
import com.example.users.api.web.dto.UserDto;
import com.example.users.api.web.mapper.JwtTokenMapper;
import com.example.users.api.web.mapper.UserMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication controller")
@RestController
@RequestMapping(path = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private final UserMapper userMapper;
  private final JwtTokenMapper jwtTokenMapper;

  @PostMapping("/sign-up")
  public ResponseEntity<UserDto> signUp(@RequestBody @Valid UserCreationDto userDto) {
    var newUser = userService.create(userMapper.toEntity(userDto));
    return new ResponseEntity<>(userMapper.toPayload(newUser), HttpStatus.CREATED);
  }

  @PostMapping("/sign-in")
  public ResponseEntity<JwtToken> signIn(@RequestBody @Valid Credentials credentials) {
    return ResponseEntity.of(userService
        .signIn(credentials.getUsername(), credentials.getPassword())
        .map(jwtTokenMapper::toPayload));
  }
}
