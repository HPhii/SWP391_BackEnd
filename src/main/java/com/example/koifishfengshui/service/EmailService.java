package com.example.koifishfengshui.service;

import com.example.koifishfengshui.model.response.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class EmailService {
    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    JavaMailSender mailSender;

    /**
     * General method to send emails with custom templates and dynamic data.
     *
     * @param emailDetails    The email details including receiver and subject.
     * @param templateName    The name of the Thymeleaf template to use.
     * @param contextVariables Map of dynamic variables to set in the email template.
     */
    public void sendMail(EmailDetails emailDetails, String templateName, Map<String, Object> contextVariables) {
        try {
            // Create the context for Thymeleaf template
            Context context = new Context();
            context.setVariables(contextVariables); // Set dynamic variables from the map

            // Process the template
            String templateContent = templateEngine.process(templateName, context);

            // Creating a simple mail message
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            // Setting up necessary details
            mimeMessageHelper.setFrom("admin@gmail.com");
            mimeMessageHelper.setTo(emailDetails.getReceiver().getEmail());
            mimeMessageHelper.setText(templateContent, true);
            mimeMessageHelper.setSubject(emailDetails.getSubject());

            // Send mail
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.out.println("ERROR MAIL SENT: " + e.getMessage());
        }
    }
}
