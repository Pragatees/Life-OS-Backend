package com.lifeos.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${sendgrid.sender.email}")
    private String senderEmail;

    @Value("${sendgrid.sender.name}")
    private String senderName;

    public void sendEmail(String to, String subject, String body) {

        Email from = new Email(senderEmail, senderName);
        Email recipient = new Email(to);

        Content content = new Content("text/plain", body);

        Mail mail = new Mail(from, subject, recipient, content);

        SendGrid sendGrid = new SendGrid(apiKey);

        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            if (response.getStatusCode() >= 400) {
                throw new RuntimeException(
                        "Failed to send email: " + response.getBody()
                );
            }

        } catch (IOException e) {
            throw new RuntimeException("Error sending email", e);
        }
    }
}