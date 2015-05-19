package com.prodyna.pac.eternity.server.exception.technical;

/**
 * Exception signaling that a given FilterRequest is inconsistent.
 */
public class InvalidFilterRequestRuntimeException extends RuntimeException {

    /**
     * Default constructor.
     */
    public InvalidFilterRequestRuntimeException() {

    }

    /**
     * Default exception constructor with the root cause.
     *
     * @param cause the root cause
     */
    public InvalidFilterRequestRuntimeException(Throwable cause) {

        super(cause);
    }

    /**
     * Default exception constructor with a message.
     *
     * @param message the exception message
     */
    public InvalidFilterRequestRuntimeException(String message) {

        super(message);
    }

    /**
     * Default exception constructor with a message and the root cause.
     *
     * @param message the exception message
     * @param cause   the root cause
     */
    public InvalidFilterRequestRuntimeException(String message, Throwable cause) {

        super(message, cause);
    }

}