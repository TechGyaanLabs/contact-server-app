package com.careerit.cbook.exception;

import java.util.UUID;

/**
 * Exception thrown when a contact is not found
 */
public class ContactNotFoundException extends ContactAppException {
    
    public ContactNotFoundException(String message) {
        super(message);
    }
    
    public ContactNotFoundException(UUID id) {
        super("Contact not found with id: " + id);
    }
    
    public ContactNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
