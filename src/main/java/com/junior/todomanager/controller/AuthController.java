package com.junior.todomanager.controller;

import com.junior.todomanager.domain.User;
import com.junior.todomanager.dto.request.LoginRequestDto;
import com.junior.todomanager.dto.request.RegisterUserRequestDto;
import com.junior.todomanager.dto.response.LoginResponseDto;
import com.junior.todomanager.dto.response.RegisterUserResponseDto;
import com.junior.todomanager.security.TokenService;
import com.junior.todomanager.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        UsernamePasswordAuthenticationToken userAndPassword = new UsernamePasswordAuthenticationToken(loginRequestDto.email(), loginRequestDto.password());
        Authentication authenticate = authenticationManager.authenticate(userAndPassword);

        User user = (User) authenticate.getPrincipal();
        String token = tokenService.generateToken(user);

        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDto> register(@RequestBody @Valid RegisterUserRequestDto request) {
        Optional<RegisterUserResponseDto> registerUserResponseDto = userService.registerUser(request);

        if (registerUserResponseDto.isEmpty()) return ResponseEntity.badRequest().build();

        return ResponseEntity.status(HttpStatus.CREATED).body(registerUserResponseDto.get());
    }
}
