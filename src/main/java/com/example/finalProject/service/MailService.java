package com.example.finalProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class MailService  {

        @Autowired
        private JavaMailSender emailSender;

        public void sendSimpleMessage(
                String to, String subject, String text) {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom("yogeshpatel@gmail.com");
//            message.setTo(to);
//            message.setSubject(subject);
//            message.setText(text);
//            emailSender.send(message);
        }
    }


