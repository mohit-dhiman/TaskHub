package md.java.taskhub.notification.service;

import org.springframework.stereotype.Service;

@Service
public class EmailSender {

    public void sendEmail(String to, String subject, String body) {
        System.out.println("Sending email to: " + to + " subject: " + subject + " body: " + body);
    }
}
