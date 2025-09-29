package com.careerit.cbook.exception;

/**
 * Base exception class for all Contact application exceptions
 */
public class ContactAppException extends RuntimeException {
    
    public ContactAppException(String message) {
        super(message);
    }
    
    public ContactAppException(String message, Throwable cause) {
        super(message, cause);
    }
}
