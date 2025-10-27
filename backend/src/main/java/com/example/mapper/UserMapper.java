package com.example.mapper;

import com.example.model.UserBo;
import com.example.model.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jsr330")
public interface UserMapper {

    UserDto toDto(UserBo bo);

    UserBo toBo(UserDto dto);
}
