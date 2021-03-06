package com.prodyna.pac.eternity.common.model.exception.functional;

/**
 * Functional exception signaling that a time booking (partially) exists for a user and project.
 */
public class DuplicateTimeBookingException extends Exception {

    /**
     * Default constructor.
     */
    public DuplicateTimeBookingException() {

    }

    /**
     * Default exception constructor with the root cause.
     *
     * @param cause the root cause
     */
    public DuplicateTimeBookingException(final Throwable cause) {

        super(cause);
    }

    /**
     * Default exception constructor with a message.
     *
     * @param message the exception message
     */
    public DuplicateTimeBookingException(final String message) {

        super(message);
    }

    /**
     * Default exception constructor with a message and the root cause.
     *
     * @param message the exception message
     * @param cause   the root cause
     */
    public DuplicateTimeBookingException(final String message, final Throwable cause) {

        super(message, cause);
    }

}
