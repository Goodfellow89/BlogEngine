package main.service;

import lombok.RequiredArgsConstructor;
import main.config.SecurityConfig;
import main.model.User;
import main.repository.CaptchaRepository;
import main.repository.SettingsRepository;
import main.repository.UsersRepository;
import main.request.AuthRegisterRequest;
import main.response.api.EditResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthRegisterService {

    private final UsersRepository usersRepository;
    private final CaptchaRepository captchaRepository;
    private final SettingsRepository settingsRepository;

    @Value("${register.min_password_length}")
    private int minPasswordLength;

    public EditResponse register(AuthRegisterRequest request) {

        if (settingsRepository.getSetting("MULTIUSER_MODE").getValue().equals("NO")) {
            return null;
        }

        EditResponse response = new EditResponse();
        ConcurrentHashMap<String, String> errors = new ConcurrentHashMap<>();

        String name = request.getName();
        String email = request.getEmail();
        String password = request.getPassword();
        String captcha = request.getCaptcha();
        String captchaSecret = request.getCaptchaSecret();

        if (name.trim().isEmpty() || !name.replaceAll("[A-Za-zА-Яа-я\\s]", "").isEmpty()) {
            errors.put("name", "Имя указано неверно");
        }
        if (usersRepository.emailCount(email) > 0) {
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }
        if (password.length() < minPasswordLength) {
            errors.put("password", "Пароль короче 6-ти символов");
        }
        if (!captcha.equals(captchaRepository.findCaptchaBySecretCode(captchaSecret).getCode())) {
            errors.put("captcha", "Код с картинки введён неверно");
        }

        if (errors.isEmpty()) {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(SecurityConfig.encoder().encode(password));
            user.setRegTime(ZonedDateTime.now());
            usersRepository.save(user);

            response.setResult(true);
            return response;
        }

        response.setResult(false);
        response.setErrors(errors);
        return response;
    }
}
