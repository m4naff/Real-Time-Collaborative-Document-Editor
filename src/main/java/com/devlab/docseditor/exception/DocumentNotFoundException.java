package com.devlab.docseditor.exception;

/**
 * Exception thrown when a user attempts to edit a document they do not have access to.
 */
public class DocumentNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Document not found.";

    /**
     * Constructs a new DocumentNotFoundException with the default message.
     */
    public DocumentNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a new DocumentNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public DocumentNotFoundException(String message) {
        super(message);
    }

}
