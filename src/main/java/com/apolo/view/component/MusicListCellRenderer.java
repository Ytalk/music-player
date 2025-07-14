package com.apolo.view.component;

import com.apolo.model.util.AudioMetadataReader;

import java.io.Serializable;

import java.awt.Component;
import java.awt.Color;
import java.awt.BorderLayout;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

/**
 * Custom list cell renderer for displaying music information in a JList (mp3pathlist/musicList) with background alternating between two colors.
 * Uses {@link AudioMetadataReader} to retrieve and display music information in an organized way.
 */
public class MusicListCellRenderer extends DefaultListCellRenderer implements Serializable {
    private static final long serialVersionUID = 6L;

    /**
     * Overrides the {@code getListCellRendererComponent} method to customize the appearance of list cells.
     * This method is called for each cell to render its content. It creates a JPanel with a BorderLayout
     * and populates it with music metadata if the value is a file path string. It also handles
     * alternating row colors and selection highlighting.
     *
     * @param list           The JList object being rendered.
     * @param value          The value to be rendered.
     * @param index          The index of the value in the list.
     * @param isSelected     True if the cell is selected, false otherwise.
     * @param cellHasFocus   True if the cell has focus, false otherwise.
     * @return A component representing the rendered cell.
     */
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        JPanel musicPanel = new JPanel(new BorderLayout());
        musicPanel.setBackground(index % 2 == 0 ? new Color(64, 64, 64) : new Color(40, 40, 40));
        musicPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        if (value instanceof String) {
            String filePath = (String) value;
            ImageIcon albumArt = AudioMetadataReader.getCoverArt(filePath);
            String title = AudioMetadataReader.getTitle(filePath);
            String duration = AudioMetadataReader.getFormattedDuration(filePath);

            JPanel infoPanel = new JPanel(new BorderLayout());
            infoPanel.setBackground(musicPanel.getBackground());

            JLabel durationLabel = new JLabel(duration);
            durationLabel.setForeground(Color.BLACK);
            infoPanel.add(durationLabel, BorderLayout.EAST);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setForeground(Color.BLACK);
            infoPanel.add(titleLabel, BorderLayout.CENTER);

            if (albumArt != null) {
                JLabel albumArtLabel = new JLabel(albumArt);
                musicPanel.add(albumArtLabel, BorderLayout.WEST);
            }
            musicPanel.add(infoPanel, BorderLayout.CENTER);
        } else {
            JLabel label = new JLabel(value.toString());
            label.setForeground(Color.BLACK);
            musicPanel.add(label, BorderLayout.CENTER);
        }

        musicPanel.setBackground(isSelected ? new Color(129, 13, 175) : musicPanel.getBackground());
        return musicPanel;
    }

}