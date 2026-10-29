package com.junior.todomanager.mapper;

import com.junior.todomanager.domain.User;
import com.junior.todomanager.dto.request.RegisterUserRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User registerUserDtoToUserEntity(RegisterUserRequestDto registerUserRequestDto);
}
