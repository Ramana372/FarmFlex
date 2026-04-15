package com.example.Service;

import com.example.Exception.EmailSendingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * EmailService - Handles email sending for verification and notifications
 * Supports asynchronous email operations to prevent blocking main application flow
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@farmflex.com}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void sendVerificationEmail(String toEmail, String username, String verificationToken) {
        try {
            String verificationLink = frontendUrl + "/verify-email?token=" + verificationToken;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("FarmFlex - Verify Your Email");
            message.setText("Hi " + username + ",\n\n" +
                    "Welcome to FarmFlex! Flex Your Farm with us. Please verify your email by clicking the link below:\n\n" +
                    verificationLink + "\n\n" +
                    "This link will expire in 24 hours.\n\n" +
                    "Best regards,\n" +
                    "FarmFlex Team");

            mailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendWelcomeEmail(String toEmail, String username, String role) {
        try {
            String dashboardLink = frontendUrl + "/dashboard";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to FarmFlex!");
            message.setText("Hi " + username + ",\n\n" +
                    "Your email has been verified successfully!\n" +
                    "You are now registered as a " + role + ".\n\n" +
                    "Farm Your Way with FarmFlex. Access your dashboard: " + dashboardLink + "\n\n" +
                    "Best regards,\n" +
                    "FarmFlex Team");

            mailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", toEmail, e);
        }
    }

    public void sendPasswordResetEmail(String toEmail, String username, String resetToken) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("FarmFlex - Reset Your Password");
            message.setText("Hi " + username + ",\n\n" +
                    "Click the link below to reset your password:\n\n" +
                    resetLink + "\n\n" +
                    "This link will expire in 1 hour.\n\n" +
                    "If you didn't request this, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "FarmFlex Team");

            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
        }
    }

    public void sendOrderConfirmationEmail(String toEmail, String buyerName, String equipmentTitle, 
                                           String orderId, String amount, String type, String dates) {
        try {
            String orderLink = frontendUrl + "/orders/" + orderId;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Order Confirmation - FarmFlex #" + orderId);
            message.setText("Hi " + buyerName + ",\n\n" +
                    "Your order has been confirmed!\n\n" +
                    "Equipment: " + equipmentTitle + "\n" +
                    "Order ID: " + orderId + "\n" +
                    "Type: " + type + (type.equals("RENT") ? " - " + dates : "") + "\n" +
                    "Amount: ₹" + amount + "\n\n" +
                    "Track your order: " + orderLink + "\n\n" +
                    "Thank you for Flexing Your Farm with FarmFlex!\n\n" +
                    "Best regards,\n" +
                    "FarmFlex Team");

            mailSender.send(message);
            log.info("Order confirmation email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send order confirmation email to: {}", toEmail, e);
        }
    }

    public void sendFarmerNotificationEmail(String toEmail, String farmerName, String equipmentTitle, 
                                            String buyerName, String buyerPhone, String type) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("New Order Received - " + equipmentTitle);
            message.setText("Hi " + farmerName + ",\n\n" +
                    "Good news! Someone is interested in your equipment.\n\n" +
                    "Equipment: " + equipmentTitle + "\n" +
                    "Type: " + type + "\n" +
                    "Buyer Name: " + buyerName + "\n" +
                    "Contact: " + buyerPhone + "\n\n" +
                    "Please contact the buyer to finalize the transaction on FarmFlex.\n\n" +
                    "Best regards,\n" +
                    "FarmFlex Team");

            mailSender.send(message);
            log.info("Farmer notification email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send farmer notification email to: {}", toEmail, e);
        }
    }

    public void sendContactFormEmail(String senderEmail, String senderName, String subject, String message) {
        try {
            SimpleMailMessage adminEmail = new SimpleMailMessage();
            adminEmail.setFrom(fromEmail);
            adminEmail.setTo("support@farmflex.com");
            adminEmail.setSubject("New Contact Form Inquiry - " + subject);
            adminEmail.setText("New message from contact form:\n\n" +
                    "Name: " + senderName + "\n" +
                    "Email: " + senderEmail + "\n" +
                    "Subject: " + subject + "\n\n" +
                    "Message:\n" + message);

            mailSender.send(adminEmail);

            // Send confirmation to sender
            SimpleMailMessage senderConfirm = new SimpleMailMessage();
            senderConfirm.setFrom(fromEmail);
            senderConfirm.setTo(senderEmail);
            senderConfirm.setSubject("We received your message - FarmFlex");
            senderConfirm.setText("Hi " + senderName + ",\n\n" +
                    "Thank you for contacting FarmFlex. We have received your message and will get back to you soon.\n\n" +
                    "Best regards,\n" +
                    "FarmFlex Support Team");

            mailSender.send(senderConfirm);
            log.info("Contact form email sent from: {}", senderEmail);
        } catch (Exception e) {
            log.error("Failed to send contact form email: {}", e.getMessage(), e);
        }
    }

    public void sendListingStatusEmail(String toEmail, String farmerName, String equipmentTitle, 
                                       String status, String reason) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Listing " + status + " - " + equipmentTitle);
            message.setText("Hi " + farmerName + ",\n\n" +
                    "Your equipment listing has been " + status.toLowerCase() + ".\n\n" +
                    "Equipment: " + equipmentTitle + "\n" +
                    (status.equals("REJECTED") ? "Reason: " + reason + "\n\n" : "\n") +
                    "Farm Your Way with FarmFlex - Your listing is now visible to buyers.\n\n" +
                    "Best regards,\n" +
                    "FarmFlex Team");

            mailSender.send(message);
            log.info("Listing status email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send listing status email to: {}", toEmail, e);
        }
    }

    @Async("taskExecutor")
    public void sendLoginNotificationEmail(String toEmail, String userName, String ipAddress, String userAgent) {
        try {
            if (toEmail == null || toEmail.trim().isEmpty()) {
                log.warn("Cannot send login notification email: recipient email is empty");
                return;
            }

            String loginTime = LocalDateTime.now().format(DATE_FORMATTER);
            String ipInfo = (ipAddress != null && !ipAddress.equalsIgnoreCase("Unknown")) 
                    ? "IP Address: " + ipAddress + "\n" 
                    : "";
            String deviceInfo = (userAgent != null && !userAgent.isEmpty()) 
                    ? "Device: " + userAgent.substring(0, Math.min(100, userAgent.length())) + "\n" 
                    : "";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Security Alert: New Login to Your FarmFlex Account");
            message.setText("Hi " + userName + ",\n\n" +
                    "Your FarmFlex account was just logged in.\n\n" +
                    "LOGIN DETAILS:\n" +
                    "Time: " + loginTime + "\n" +
                    ipInfo +
                    deviceInfo +
                    "\n" +
                    "SECURITY NOTE:\n" +
                    "If this wasn't you, please change your password immediately and contact our support team.\n\n" +
                    "Best practices for securing your account:\n" +
                    "• Use a strong, unique password\n" +
                    "• Enable two-factor authentication if available\n" +
                    "• Log out from untrusted devices\n\n" +
                    "Thank you for using FarmFlex - Flex Your Farm with Confidence!\n\n" +
                    "Best regards,\n" +
                    "FarmFlex Security Team");

            mailSender.send(message);
            log.info("Login notification email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            // Log the error but don't throw - this is an async operation
            // and should not break the login flow
            log.error("Failed to send login notification email to: {}", toEmail, e);
            // Optionally send alert to monitoring system here
        }
    }

    @Async("taskExecutor")
    public void sendSuspiciousLoginAlert(String toEmail, String userName, String ipAddress, String reason) {
        try {
            if (toEmail == null || toEmail.trim().isEmpty()) {
                log.warn("Cannot send suspicious login alert: recipient email is empty");
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("⚠️ URGENT: Suspicious Login Activity Detected on Your Account");
            message.setText("Hi " + userName + ",\n\n" +
                    "We've detected suspicious login activity on your FarmFlex account.\n\n" +
                    "DETAILS:\n" +
                    "IP Address: " + ipAddress + "\n" +
                    "Reason: " + reason + "\n" +
                    "Time: " + LocalDateTime.now().format(DATE_FORMATTER) + "\n\n" +
                    "ACTION REQUIRED:\n" +
                    "If this was you, you can ignore this alert.\n" +
                    "If this was not you, please:\n" +
                    "1. Change your password immediately\n" +
                    "2. Review your login history\n" +
                    "3. Contact our support team if needed\n\n" +
                    "Your account security is important to us.\n\n" +
                    "Best regards,\n" +
                    "FarmFlex Security Team");

            mailSender.send(message);
            log.warn("Suspicious login alert email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send suspicious login alert email to: {}", toEmail, e);
        }
    }

    @Async("taskExecutor")
    public void sendPasswordChangedEmail(String toEmail, String userName) {
        try {
            if (toEmail == null || toEmail.trim().isEmpty()) {
                log.warn("Cannot send password changed email: recipient email is empty");
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Password Changed - FarmFlex Account");
            message.setText("Hi " + userName + ",\n\n" +
                    "Your FarmFlex account password was successfully changed on " + 
                    LocalDateTime.now().format(DATE_FORMATTER) + ".\n\n" +
                    "If you did not perform this action, please contact our support team immediately.\n\n" +
                    "Best regards,\n" +
                    "FarmFlex Security Team");

            mailSender.send(message);
            log.info("Password changed confirmation email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password changed confirmation email to: {}", toEmail, e);
        }
    }
}
