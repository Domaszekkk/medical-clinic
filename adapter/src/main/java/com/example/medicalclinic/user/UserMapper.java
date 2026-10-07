package com.example.medicalclinic.user;

import com.example.medicalclinic.User;
import com.example.medicalclinic.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(target = "patients", ignore = true)
    @Mapping(target = "doctors", ignore = true)
    UserEntity toEntity(User user);

    User toModel(UserEntity entity);
}
