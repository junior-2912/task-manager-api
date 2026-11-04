package com.junior.todomanager.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.junior.todomanager.domain.User;
import com.junior.todomanager.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try {
            if (user == null) throw new ResourceNotFoundException("User cannot be null");
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withClaim("userId", user.getId())
                    .withSubject(user.getEmail())
                    .withExpiresAt(Instant.now().plusSeconds(1200))
                    .withIssuedAt(Instant.now())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error while generating token", e);
        }
    }

    public Optional<JWTUserData> validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .build()
                    .verify(token);

            return Optional.of(JWTUserData.builder()
                    .userId(decodedJWT.getClaim("userId").asLong())
                    .email(decodedJWT.getSubject())
                    .build());
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }
}
