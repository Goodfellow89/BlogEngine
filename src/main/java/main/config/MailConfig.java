package main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        Properties properties = new Properties();

        properties.put("mail.properties.mail.smtp.auth", false);
        properties.put("mail.properties.mail.smtp.starttls.enable", false);

        sender.setJavaMailProperties(properties);
        sender.setHost("localhost");
        sender.setPort(25);
        sender.setProtocol("smtp");
        sender.setUsername("");
        sender.setPassword("");

        return sender;
    }
}