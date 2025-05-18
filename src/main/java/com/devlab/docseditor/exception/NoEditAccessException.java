package com.devlab.docseditor.exception;

/**
 * Exception thrown when a user attempts to edit a document they do not have access to.
 */
public class NoEditAccessException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "User does not have edit access to this document.";

    /**
     * Constructs a new NoEditAccessException with the default message.
     */
    public NoEditAccessException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a new NoEditAccessException with the specified detail message.
     *
     * @param message the detail message
     */
    public NoEditAccessException(String message) {
        super(message);
    }
}
