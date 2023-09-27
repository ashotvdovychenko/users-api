package com.example.users.api.web.mapper;

import com.example.users.api.domain.User;
import com.example.users.api.web.dto.UserCreationDto;
import com.example.users.api.web.dto.UserDto;
import com.example.users.api.web.dto.UserUpdateDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public interface UserMapper {

  @Mapping(target = "birthDate", source = "birthDate", dateFormat = "dd-MM-yyyy")
  UserDto toPayload(User user);

  User toEntity(UserCreationDto userDto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  User partialUpdate(UserUpdateDto userDto, @MappingTarget User user);
}
