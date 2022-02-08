package main.service;

import main.response.api.AuthCheckResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthLogoutService {

    public AuthCheckResponse logout() {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            SecurityContextHolder.clearContext();
        }

        return new AuthCheckResponse(true);
    }
}
