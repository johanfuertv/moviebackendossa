package com.johanfuertv.movietheaterbackend.service;

import com.johanfuertv.movietheaterbackend.entity.Purchase;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    private final String fromEmail = "johanrayfirck@gmail.com";
    private final String fixedNotificationEmail = "johanrayfirck@gmail.com";

    
    public void sendPurchaseConfirmation(Purchase purchase) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(purchase.getCustomer().getEmail());
            helper.setFrom(fromEmail);
            helper.setSubject("Purchase Confirmation - Movie Theater");
            
            String htmlContent;
            try {
                Context context = new Context();
                context.setVariable("customerName", purchase.getCustomer().getFullName());
                context.setVariable("movieTitle", purchase.getMovie().getTitle());
                context.setVariable("quantity", purchase.getQuantity());
                context.setVariable("totalAmount", purchase.getTotalAmount());
                context.setVariable("status", purchase.getStatus().name());
                context.setVariable("purchaseId", purchase.getId().toString());
                context.setVariable("purchaseDate", purchase.getCreatedAt());
                context.setVariable("movieGenre", purchase.getMovie().getGenre());
                context.setVariable("movieDuration", purchase.getMovie().getDurationFormatted());
                htmlContent = templateEngine.process("purchase-confirmation", context);
            } catch (Exception templateError) {
                logger.warn("Email template not found or failed to render. Falling back to simple HTML.");
                htmlContent = "<html><body>"
                    + "<h2>Gracias por tu compra</h2>"
                    + "<p>Hola " + purchase.getCustomer().getFullName() + ",</p>"
                    + "<p>Has comprado " + purchase.getQuantity() + " entrada(s) para <strong>"
                    + purchase.getMovie().getTitle() + "</strong>.</p>"
                    + "<p>Total: <strong>" + purchase.getTotalAmount() + "</strong></p>"
                    + "<p>Estado: " + purchase.getStatus().name() + "</p>"
                    + "<p>ID de compra: " + purchase.getId() + "</p>"
                    + "</body></html>";
            }
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            logger.info("Purchase confirmation email sent to: {}", purchase.getCustomer().getEmail());
            
        } catch (MessagingException e) {
            logger.error("Error sending purchase confirmation email to: {}", purchase.getCustomer().getEmail(), e);
        }

        // Additionally, always notify the fixed Gmail with a simple thank-you message
        try {
            MimeMessage simpleMessage = mailSender.createMimeMessage();
            MimeMessageHelper simpleHelper = new MimeMessageHelper(simpleMessage, true, "UTF-8");
            simpleHelper.setTo(fixedNotificationEmail);
            simpleHelper.setFrom(fromEmail);
            simpleHelper.setSubject("Gracias por tu compra");
            String body = "<html><body><p>Gracias por tu compra</p></body></html>";
            simpleHelper.setText(body, true);
            mailSender.send(simpleMessage);
            logger.info("Fixed notification email sent to: {}", fixedNotificationEmail);
        } catch (MessagingException e) {
            logger.error("Error sending fixed notification email to: {}", fixedNotificationEmail, e);
        }
    }
    
    public void sendWelcomeEmail(String customerEmail, String customerName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(customerEmail);
            helper.setFrom(fromEmail);
            helper.setSubject("Welcome to Movie Theater!");
            
            // Simple welcome message
            String htmlContent = String.format(
                "<html><body>" +
                "<h2>Welcome to Movie Theater, %s!</h2>" +
                "<p>Thank you for registering with us. You can now browse our movie catalog and purchase tickets.</p>" +
                "<p>Enjoy your movie experience!</p>" +
                "<p>Best regards,<br/>Movie Theater Team</p>" +
                "</body></html>",
                customerName
            );
            
            helper.setText(htmlContent, true);
            mailSender.send(message);
            
            logger.info("Welcome email sent to: {}", customerEmail);
            
        } catch (MessagingException e) {
            logger.error("Error sending welcome email to: {}", customerEmail, e);
        }
    }
}