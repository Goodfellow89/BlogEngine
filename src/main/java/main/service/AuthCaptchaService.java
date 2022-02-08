package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import lombok.RequiredArgsConstructor;
import main.model.CaptchaCode;
import main.repository.CaptchaRepository;
import main.response.api.AuthCaptchaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthCaptchaService {

    private final CaptchaRepository captchaRepository;

    @Value("${captcha.aging_time_in_minutes}")
    private int agingTime;

    @Value("${captcha.length}")
    private int captchaLength;

    public AuthCaptchaResponse getAuthCaptchaResponse() {

        AuthCaptchaResponse authCaptchaResponse = new AuthCaptchaResponse();
        CaptchaCode captcha = new CaptchaCode();

        String captchaKey = generateCaptchaKey();
        String captchaSecretKey = generateCaptchaSecretKey();

        captcha.setCode(captchaKey);
        captcha.setSecretCode(captchaSecretKey);
        captcha.setTime(ZonedDateTime.now());
        captchaRepository.save(captcha);

        Cage cage = new GCage();
        authCaptchaResponse.setImage("data:image/png;base64, " + Base64.getEncoder().encodeToString(cage.draw(captchaKey)));
        authCaptchaResponse.setSecret(captchaSecretKey);

        return authCaptchaResponse;
    }

    @Scheduled(fixedRateString = "PT" + "${captcha.fixed_rate_in_minutes}" + "M")
    private void deleteOldCaptcha() {
        captchaRepository.deleteOldCaptcha(agingTime);
    }

    private String generateCaptchaKey() {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < captchaLength; i++) {
            char letter = (char) Math.round(('A' + Math.random() * ('Z' - 'A')));
            int number = (int) (1 + Math.random() * 8);
            if (Math.random() > 0.3 && letter != 'O') {
                key.append(letter);
            } else {
                key.append(number);
            }
        }
        return key.toString();
    }

    private String generateCaptchaSecretKey() {
        StringBuilder key = new StringBuilder();
        key.append(System.currentTimeMillis());
        for (int i = 0; i < 12; i++) {
            char letter = (char) Math.round(('A' + Math.random() * ('Z' - 'A')));
            int number = (int) (1 + Math.random() * 8);
            int index = (int) (key.length() * Math.random());
            if (Math.random() > 0.3) {
                key.insert(index, letter);
            } else {
                key.insert(index, number);
            }
        }
        return key.toString();
    }
}
