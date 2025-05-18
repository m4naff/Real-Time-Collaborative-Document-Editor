package com.devlab.docseditor.exception;

/**
 * Exception thrown when a user attempts to edit a document they do not have access to.
 */
public class OwnerAccessException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "User do not have permission to delete this document.";

    /**
     * Constructs a new DeleteAccessException with the default message.
     */
    public OwnerAccessException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a new DeleteAccessException with the specified detail message.
     *
     * @param message the detail message
     */
    public OwnerAccessException(String message) {
        super(message);
    }

}
