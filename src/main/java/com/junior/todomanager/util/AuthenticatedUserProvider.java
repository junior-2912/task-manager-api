package com.junior.todomanager.util;

import com.junior.todomanager.domain.User;
import com.junior.todomanager.exceptions.UserNotAuthenticatedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserProvider {
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }
        return (User) authentication.getPrincipal();
    }
}
