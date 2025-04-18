package com.example.finalProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Service for sending email notifications
 * Provides asynchronous email functionality for the application
 */
@Component
public class MailService {

    @Autowired
    private JavaMailSender emailSender;

    /**
     * Sends a simple email message asynchronously
     * Using @Async to prevent blocking the main thread
     *
     * @param to The recipient's email address
     * @param subject The subject line of the email
     * @param text The body content of the email
     */
    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        System.out.println("mail run");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("yogeshpatel@gmail.com"); // Consider moving this to a configuration property
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}