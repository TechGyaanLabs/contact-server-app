package com.careerit.cbook.exception;

/**
 * Exception thrown when trying to create a contact that already exists
 */
public class ContactAlreadyExistsException extends ContactAppException {
    
    public ContactAlreadyExistsException(String message) {
        super(message);
    }
    
    public ContactAlreadyExistsException(String field, String value) {
        super("Contact with " + field + " " + value + " already exists");
    }
    
    public ContactAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
