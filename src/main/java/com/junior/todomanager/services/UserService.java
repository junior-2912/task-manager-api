package com.junior.todomanager.services;

import com.junior.todomanager.domain.User;
import com.junior.todomanager.dto.request.RegisterUserRequestDto;
import com.junior.todomanager.dto.response.RegisterUserResponseDto;
import com.junior.todomanager.mapper.UserMapper;
import com.junior.todomanager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public Optional<RegisterUserResponseDto> registerUser(RegisterUserRequestDto registerUserRequestDto) {
        if (repository.findByEmail(registerUserRequestDto.email()).isPresent()) return Optional.empty();
        User user = userMapper.registerUserDtoToUserEntity(registerUserRequestDto);
        user.setPassword(passwordEncoder.encode(registerUserRequestDto.password()));
        RegisterUserResponseDto registerUserResponseDto = userMapper.entityUserToResponseDto(repository.save(user));
        return Optional.of(registerUserResponseDto);
    }
}
