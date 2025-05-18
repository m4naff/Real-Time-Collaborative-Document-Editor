package com.devlab.docseditor.exception;

/**
 * Exception thrown when a user attempts to edit a document they do not have access to.
 */
public class UsernameAlreadyExistsException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Username already exists.";

    /**
     * Constructs a new UsernameAlreadyExistsException with the default message.
     */
    public UsernameAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a new UsernameAlreadyExistsException with the specified detail message.
     *
     * @param message the detail message
     */
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
