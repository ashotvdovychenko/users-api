package com.example.users.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenProvider {
  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.issuer}")
  private String jwtIssuer;

  public String generateToken(String username) {
    return JWT.create()
        .withIssuer(jwtIssuer)
        .withSubject(username)
        .withExpiresAt(LocalDate.now()
            .plusDays(15)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant())
        .sign(Algorithm.HMAC256(jwtSecret));
  }

  public Optional<DecodedJWT> toDecodedJWT(String token) {
    try {
      return Optional.of(JWT.require(Algorithm.HMAC256(jwtSecret))
          .withIssuer(jwtIssuer)
          .build()
          .verify(token));
    } catch (JWTVerificationException exception) {
      return Optional.empty();
    }
  }

  public String getUsernameFromToken(String token) {
    return JWT.require(Algorithm.HMAC256(jwtSecret))
        .withIssuer(jwtIssuer)
        .build()
        .verify(token)
        .getSubject();
  }
}