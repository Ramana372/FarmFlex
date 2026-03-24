package com.example.Exception;

/**
 * DuplicateEmailException - Thrown when attempting to register with an existing email
 */
public class DuplicateEmailException extends RuntimeException {
    private String email;

    public DuplicateEmailException(String message, String email) {
        super(message);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
