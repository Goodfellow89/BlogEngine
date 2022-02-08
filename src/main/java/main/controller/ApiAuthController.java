package main.controller;

import lombok.RequiredArgsConstructor;
import main.request.AuthLoginRequest;
import main.request.AuthRegisterRequest;
import main.request.PasswordRequest;
import main.response.api.AuthCaptchaResponse;
import main.response.api.AuthCheckResponse;
import main.response.api.EditResponse;
import main.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ApiAuthController {

    private final AuthCheckService authCheckService;
    private final AuthCaptchaService authCaptchaService;
    private final AuthRegisterService authRegisterService;
    private final AuthLoginService authLoginService;
    private final AuthLogoutService authLogoutService;
    private final RestoreService restoreService;

    @GetMapping("/check")
    public AuthCheckResponse authCheck() {
        return authCheckService.getAuthCheckResponse();
    }

    @GetMapping("/captcha")
    public AuthCaptchaResponse authCaptcha() {
        return authCaptchaService.getAuthCaptchaResponse();
    }

    @PostMapping("/register")
    public ResponseEntity<EditResponse> authRegister(@RequestBody AuthRegisterRequest request) {
        EditResponse response = authRegisterService.register(request);
        if (response == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return new ResponseEntity<>(authRegisterService.register(request), HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthCheckResponse authLogin(@RequestBody AuthLoginRequest request) {
        return authLoginService.login(request);
    }

    @GetMapping("/logout")
    public AuthCheckResponse authLogout() {
        return authLogoutService.logout();
    }

    @PostMapping("/restore")
    public EditResponse restore(@RequestBody Map<String, String> email) {
        return restoreService.restore(email.get("email"));
    }

    @PostMapping("/password")
    public EditResponse changePassword(@RequestBody PasswordRequest request) {
        return restoreService.changePassword(request);
    }
}
