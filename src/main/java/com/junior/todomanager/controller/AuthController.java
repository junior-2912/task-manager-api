package com.junior.todomanager.controller;

import com.junior.todomanager.domain.User;
import com.junior.todomanager.dto.request.LoginRequestDto;
import com.junior.todomanager.dto.request.RegisterUserRequestDto;
import com.junior.todomanager.dto.response.LoginResponseDto;
import com.junior.todomanager.dto.response.RegisterUserResponseDto;
import com.junior.todomanager.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository, PasswordEncoder encoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        UsernamePasswordAuthenticationToken userAndPassword = new UsernamePasswordAuthenticationToken(loginRequestDto.email(), loginRequestDto.password());
        Authentication authenticate = authenticationManager.authenticate(userAndPassword);


        return null;
    }

    @PostMapping
    public ResponseEntity<RegisterUserResponseDto> register(RegisterUserRequestDto request) {
        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterUserResponseDto(user.getName(), user.getEmail()));
    }
}
