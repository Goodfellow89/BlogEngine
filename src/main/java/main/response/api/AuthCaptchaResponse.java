package main.response.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthCaptchaResponse {

    private String secret;
    private String image;
}
