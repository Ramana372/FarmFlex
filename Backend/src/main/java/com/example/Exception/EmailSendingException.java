package com.example.Exception;

/**
 * EmailSendingException - Thrown when email sending fails
 * This is a non-critical exception that shouldn't break the main flow
 */
public class EmailSendingException extends RuntimeException {
    public EmailSendingException(String message) {
        super(message);
    }

    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
