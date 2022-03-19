package main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${mail.host}")
    private String host;

    @Value("${mail.port}")
    private int port;

    @Value("${mail.address}")
    private String address;

    @Value("${mail.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.debug", true);

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setJavaMailProperties(properties);
        sender.setHost(host);
        sender.setPort(port);
        sender.setProtocol("smtp");
        sender.setUsername(address);
        sender.setPassword(password);

        return sender;
    }
}