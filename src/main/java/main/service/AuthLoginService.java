package main.service;

import lombok.RequiredArgsConstructor;
import main.request.AuthLoginRequest;
import main.response.api.AuthCheckResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthLoginService {

    private final AuthenticationManager authManager;
    private final AuthCheckService checkService;

    public AuthCheckResponse login(AuthLoginRequest request) {

        try {
            Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            e.getCause();
        }

        return checkService.getAuthCheckResponse();
    }
}
