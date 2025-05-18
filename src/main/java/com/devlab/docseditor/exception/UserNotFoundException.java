package com.devlab.docseditor.exception;

/**
 * Exception thrown when a user attempts to edit a document they do not have access to.
 */
public class UserNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "User not found";

    /**
     * Constructs a new UserNotFoundException with the default message.
     */
    public UserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a new UserNotFoundException with the specified message.
     * @param message the detail message
     */
    public UserNotFoundException(String message) {
        super(message);
    }

}
