package main.service;

import lombok.RequiredArgsConstructor;
import main.config.SecurityConfig;
import main.model.User;
import main.repository.CaptchaRepository;
import main.repository.UsersRepository;
import main.request.PasswordRequest;
import main.response.api.EditResponse;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RestoreService {

    private final UsersRepository usersRepository;
    private final JavaMailSender sender;
    private final CaptchaRepository captchaRepository;

    @Value("${register.min_password_length}")
    private int minPasswordLength;

    public EditResponse restore(String email) {

        Logger logger = LogManager.getLogger(RestoreService.class);

        EditResponse response = new EditResponse();

        User user = usersRepository.findByEmail(email);

        if (user != null) {
            SimpleMailMessage message = new SimpleMailMessage();
            String hash = generateHash();
            message.setFrom("noreply@devpub.ru");
            message.setTo(email);
            message.setSubject("Восстановление пароля");
            message.setText("/login/change-password/" + hash);

            logger.log(Level.INFO, "Link to restore: " + message.getText());

            user.setCode(hash);
            usersRepository.save(user);
            sender.send(message);
            response.setResult(true);
        }

        return response;
    }

    public EditResponse changePassword(PasswordRequest request) {

        EditResponse response = new EditResponse();
        ConcurrentHashMap<String, String> errors = new ConcurrentHashMap<>();

        String password = request.getPassword();
        User user = usersRepository.findByCode(request.getCode());

        if (user == null) {
            errors.put("code", "Ссылка для восстановления пароля устарела.\n <a href=\n \\\"/auth/restore\\\">Запросить ссылку снова</a>");
        }
        if (password.length() < minPasswordLength) {
            errors.put("password", "Пароль короче 6-ти символов");
        }
        if (!request.getCaptcha().equals(captchaRepository.findCaptchaBySecretCode(request.getCaptchaSecret()).getCode())) {
            errors.put("captcha", "Код с картинки введён неверно");
        }

        if (errors.isEmpty()) {
            user.setPassword(SecurityConfig.encoder().encode(password));
            usersRepository.save(user);
            response.setResult(true);
        }

        response.setErrors(errors);
        return response;
    }

    private String generateHash() {
        StringBuilder hash = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            char letter = (char) Math.round(('a' + Math.random() * ('z' - 'a')));
            int number = (int) (1 + Math.random() * 8);
            int index = (int) (hash.length() * Math.random());
            if (Math.random() > 0.3) {
                hash.insert(index, letter);
            } else {
                hash.insert(index, number);
            }
        }
        return hash.toString();
    }
}
