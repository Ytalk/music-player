package com.apolo.gui;

import javax.swing.JOptionPane;

/**
 * The `musicException` class represents an exception specific to the music player application.
 * It extends the RuntimeException class and provides custom error handling functionality.
 */
public class MusicException extends RuntimeException {

    private String message; //error message
    private String error_name; //name of the error

    /**
     * Constructs a new musicException object with the specified error message and name.
     * @param message The error message.
     * @param error_name The name of the error.
     */
    public MusicException(String message, String error_name) {
        this.message = message;
        this.error_name = error_name;
    }

    /**
     * Displays the error message in a dialog box with the specified error name.
     */
    public void showMessage() {
        JOptionPane.showMessageDialog(null, message, error_name, JOptionPane.ERROR_MESSAGE);
    }
}
