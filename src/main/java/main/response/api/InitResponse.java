package main.response.api;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class InitResponse {

    @Value("${init.title}")
    private String title;

    @Value("${init.subtitle}")
    private String subtitle;

    @Value("${init.phone}")
    private String phone;

    @Value("${init.email}")
    private String email;

    @Value("${init.copyright}")
    private String copyright;

    @Value("${init.copyrightFrom}")
    private String copyrightFrom;
}