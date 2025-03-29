package com.apolo.gui;

import com.apolo.model.MusicMetadata;

import java.io.File;
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
 * Custom list cell renderer for displaying music information in a JList with alternating background colors.
 * Uses JAudioTagger to retrieve and display music information in an organized way.
 */
public class ApoloTaggerListCellRenderer extends DefaultListCellRenderer implements Serializable {
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
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        JPanel musicPanel = new JPanel(new BorderLayout());
        musicPanel.setBackground(index % 2 == 0 ? new Color(64, 64, 64) : new Color(40, 40, 40));
        musicPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        if (value instanceof String) {
            String filePath = (String) value;
            String fileName = new File(filePath).getName().replaceFirst("[.][^.]+$", "");

            MusicMetadata metadata = new MusicMetadata(filePath);
            ImageIcon albumArt = metadata.getMP3AlbumArtwork();
            String title = metadata.getMP3Title();
            String duration = metadata.getMP3Duration();

            JPanel infoPanel = new JPanel(new BorderLayout());
            infoPanel.setBackground(musicPanel.getBackground());

            JLabel titleLabel = new JLabel(metadata.getMP3Title());//*
            //JLabel titleLabel = new JLabel(title != null && !title.isEmpty() ? title : fileName);
            titleLabel.setForeground(Color.BLACK);
            infoPanel.add(titleLabel, BorderLayout.CENTER);

            JLabel durationLabel = new JLabel(duration);
            durationLabel.setForeground(Color.BLACK);
            infoPanel.add(durationLabel, BorderLayout.EAST);

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