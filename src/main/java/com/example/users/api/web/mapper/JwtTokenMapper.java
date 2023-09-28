package com.example.users.api.web.mapper;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.users.api.web.dto.JwtToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface JwtTokenMapper {
  @Mapping(target = "expiresAt", dateFormat = "dd-MM-yyyy HH:mm:ss")
  JwtToken toPayload(DecodedJWT jwt);
}
