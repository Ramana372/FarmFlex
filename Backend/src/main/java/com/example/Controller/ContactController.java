package com.example.Controller;

import com.example.Service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ContactController - Handles contact form submissions and inquiries
 */
@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ContactController {

    private final EmailService emailService;

    /**
     * Submit contact form
     */
    @PostMapping("/send-message")
    public ResponseEntity<?> sendContactMessage(@RequestBody ContactRequest request) {
        try {
            // Validate input
            if (request.getEmail() == null || request.getEmail().isEmpty() ||
                request.getName() == null || request.getName().isEmpty() ||
                request.getMessage() == null || request.getMessage().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Name, email, and message are required"));
            }

            // Send contact form emails
            emailService.sendContactFormEmail(
                    request.getEmail(),
                    request.getName(),
                    request.getSubject() != null ? request.getSubject() : "General Inquiry",
                    request.getMessage()
            );

            log.info("Contact form submitted by: {}", request.getEmail());
            return ResponseEntity.ok(Map.of(
                    "message", "Thanks for reaching out! We'll get back to you soon from FarmFlex.",
                    "email", request.getEmail()
            ));
        } catch (Exception e) {
            log.error("Error processing contact form: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to send message. Please try again later."));
        }
    }

    /**
     * Send equipment inquiry email
     */
    @PostMapping("/inquiry")
    public ResponseEntity<?> sendEquipmentInquiry(@RequestBody EquipmentInquiry inquiry) {
        try {
            String message = String.format(
                    "I am interested in your equipment: %s\n\n" +
                    "Inquiry Details:\n" +
                    "Budget: %s\n" +
                    "Required Date: %s\n" +
                    "Additional Details: %s\n\n" +
                    "Contact me at: %s",
                    inquiry.getEquipmentName(),
                    inquiry.getBudget(),
                    inquiry.getRequiredDate(),
                    inquiry.getAdditionalDetails(),
                    inquiry.getContactNumber()
            );

            emailService.sendContactFormEmail(
                    inquiry.getEmail(),
                    inquiry.getFullName(),
                    "Equipment Inquiry - " + inquiry.getEquipmentName(),
                    message
            );

            log.info("Equipment inquiry sent for: {}", inquiry.getEquipmentName());
            return ResponseEntity.ok(Map.of(
                    "message", "Inquiry sent successfully! Seller will contact you soon."
            ));
        } catch (Exception e) {
            log.error("Error sending equipment inquiry: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to send inquiry"));
        }
    }

    // DTOs
    public static class ContactRequest {
        private String name;
        private String email;
        private String subject;
        private String message;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class EquipmentInquiry {
        private String fullName;
        private String email;
        private String contactNumber;
        private String equipmentName;
        private String budget;
        private String requiredDate;
        private String additionalDetails;

        // Getters and Setters
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getContactNumber() { return contactNumber; }
        public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

        public String getEquipmentName() { return equipmentName; }
        public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }

        public String getBudget() { return budget; }
        public void setBudget(String budget) { this.budget = budget; }

        public String getRequiredDate() { return requiredDate; }
        public void setRequiredDate(String requiredDate) { this.requiredDate = requiredDate; }

        public String getAdditionalDetails() { return additionalDetails; }
        public void setAdditionalDetails(String additionalDetails) { this.additionalDetails = additionalDetails; }
    }
}
