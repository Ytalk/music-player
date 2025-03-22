package com.apolo.gui;

import java.io.Serializable;

import java.awt.Component;
import java.awt.Color;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;


/**
 * Custom list cell renderer for displaying elements in a JList (mainList) with alternating background colors.
 * Background color alternates between two colors for better visual distinction.
 */
public class ApoloListCellRenderer extends DefaultListCellRenderer implements Serializable {
    private static final long serialVersionUID = 6L;

    /**
     * Overrides the getListCellRendererComponent method to customize the appearance of list cells.
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
