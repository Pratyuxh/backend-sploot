package com.sploot.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${emailer.protocol}")
    private String emailerProtocol;
    @Value("${emailer.debug}")
    private String emailerDebugEnable;
    @Value("${emailer.user.name}")
    private String userName;
    @Value("${emailer.password}")
    private String password;
    @Value("${emailer.host}")
    private String host;
    @Value("${emailer.port}")
    private String port;

    @Bean
    public JavaMailSender getJavaMailSender() throws Exception {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost(host);
//        mailSender.setPort(Integer.parseInt(port));
//        mailSender.setUsername(userName);
//        mailSender.setPassword(password);
//        mailSender.setProtocol(emailerProtocol);
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", emailerProtocol);
//        props.setProperty("mail.smtps.auth", String.valueOf(true));
//        props.setProperty("mail.smtp.ssl.enable", String.valueOf(false));
//        props.setProperty("mail.smtp.starttls.enable", String.valueOf(false));
//        props.setProperty("mail.smtps.socketFactory.port", String.valueOf(port));
//        props.put("mail.debug", emailerDebugEnable);
//        props.setProperty("mail.smtps.ssl.socketFactory.class", "com.java.ncp.radonwebservice.ssl.TrustSSLContextFactory");
//        props.put("mail.smtps.ssl.trust", "*");
//        props.put("mail.smtp.ssl.trust", "*");

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("wammet.wammet@gmail.com");
        mailSender.setPassword("kzextsrdntloqsbp");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
