package com.junior.todomanager.security;

import lombok.Builder;

@Builder
public record JWTUserData(Long userId, String email) {
}
