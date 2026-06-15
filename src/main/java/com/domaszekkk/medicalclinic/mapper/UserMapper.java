package com.domaszekkk.medicalclinic.mapper;

import com.domaszekkk.medicalclinic.dto.AddUserCommand;
import com.domaszekkk.medicalclinic.dto.UpdateUserRequest;
import com.domaszekkk.medicalclinic.dto.UserDto;
import com.domaszekkk.medicalclinic.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto mapToDto(User user);
    List<UserDto> mapToDtoList(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patients", ignore = true)
    User mapToEntity(AddUserCommand request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patients", ignore = true)
    User mapToEntity(UpdateUserRequest request);
}