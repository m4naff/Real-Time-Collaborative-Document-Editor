package com.devlab.docseditor.exception;

/**
 * Exception thrown when a user attempts to edit a document they do not have access to.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Email already exists.";

    /**
     * Constructs a new EmailAlreadyExistsException with the default message.
     */
    public EmailAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a new EmailAlreadyExistsException with the specified detail message.
     *
     * @param message the detail message
     */
    public EmailAlreadyExistsException(String message) {
        super(message);
    }

}
