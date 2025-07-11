package com.apolo.view;

import javax.swing.JOptionPane;

public class ApoloPopUp {

    public void showError(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void showWarning(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public boolean showDeleteConfirmation(String playlistName) {
        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the '" + playlistName + "' playlist?", "Confirm Playlist Deletion", JOptionPane.YES_NO_OPTION);
        return option == JOptionPane.YES_OPTION;
    }

}
