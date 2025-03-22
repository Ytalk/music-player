package com.apolo.model;


/**
 * The `musicException` class represents an exception specific to the music player application.
 * It extends the RuntimeException class and provides custom error handling functionality.
 */
public class MusicException extends RuntimeException {

    private String message;
    private String errorName;

    /**
     * Constructs a new musicException object with the specified error message and name.
     * @param message The error message.
     * @param errorName The name of the error.
     */
    public MusicException(String message, String errorName) {
        this.message = message;
        this.errorName = errorName;
    }

    /**
     * Returns the error message.
     * @return The error message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the error name.
     * @return The error name.
     */
    public String getErrorName() {
        return errorName;
    }

}
