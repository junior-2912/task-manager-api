package com.junior.todomanager.mapper;

import com.junior.todomanager.domain.User;
import com.junior.todomanager.dto.request.RegisterUserRequestDto;
import com.junior.todomanager.dto.response.RegisterUserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "password", ignore = true)
    User registerUserDtoToUserEntity(RegisterUserRequestDto registerUserRequestDto);

    RegisterUserResponseDto entityUserToResponseDto(User user);
}
