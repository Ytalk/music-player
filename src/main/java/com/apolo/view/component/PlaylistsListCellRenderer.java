package com.apolo.view.component;

import java.io.Serializable;

import java.awt.Component;
import java.awt.Color;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;


/**
 * Custom list cell renderer for displaying playlists names in a JList (mainList/playlistsList) with background alternating between two colors.
 */
public class PlaylistsListCellRenderer extends DefaultListCellRenderer implements Serializable {
    private static final long serialVersionUID = 6L;

    /**
     * Overrides the {@code getListCellRendererComponent} method to customize the appearance of list cells.
     * This method is responsible for setting the background and foreground colors of each cell
     * based on its selection state and index (for alternating row colors).
     *
     * @param list           The JList object being rendered.
     * @param value          The value to be rendered.
     * @param index          The index of the value in the list.
     * @param isSelected     True if the cell is selected, false otherwise.
     * @param cellHasFocus   True if the cell has focus, false otherwise.
     * @return A component representing the rendered cell.
     */
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (isSelected) {
            setBackground(new Color(129, 13, 175));
            setForeground(Color.BLACK);
        }
        else {
            setBackground(index % 2 == 0 ? new Color(64, 64, 64) : new Color(40, 40, 40));
            setForeground(Color.BLACK);
        }

        return this;
    }
}