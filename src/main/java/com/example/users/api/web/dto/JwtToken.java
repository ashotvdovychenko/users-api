package com.example.users.api.web.dto;

import lombok.Data;

@Data
public class JwtToken {
  private String token;
  private String type;
  private String algorithm;
  private String expiresAt;
}
