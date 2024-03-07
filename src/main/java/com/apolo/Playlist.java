package com.apolo;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.DefaultListModel;
import javax.swing.SwingConstants;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.images.Artwork;

/**
 * The `Playlist` class represents a playlist containing a list of music files.
 * It provides methods to add, remove, and display music files within the playlist.
 * Each playlist is displayed as a JPanel with a scrollable list of music files.
 */
public class Playlist implements Serializable {

    private static final long serialVersionUID = 6L;

    private JScrollPane mp3pathlist_scroll; // Scroll pane for the list of music files
    private DefaultListModel<String> listModel; // Model for managing the playlist
    private JList<String> mp3pathlist; // List of music files
    private JLabel playlist_label; // Label displaying the name of the playlist
    private JPanel playlist; // Panel containing playlist information

    /**
     * Constructs a new Playlist object with the specified name.
     *
     * @param name The name of the playlist.
     */
    public Playlist(String name) {
        //initialize the playlist label
        playlist_label = new JLabel(name);
        playlist_label.setHorizontalAlignment(SwingConstants.CENTER);
        playlist_label.setVerticalAlignment(SwingConstants.CENTER);
        playlist_label.setFont(new Font("Arial", Font.BOLD, 20));
        playlist_label.setForeground(Color.WHITE);

        // Initialize the list model and list of music files
        listModel = new DefaultListModel<>();
        mp3pathlist = new JList<>();
        mp3pathlist.setModel(listModel);
        mp3pathlist.setCellRenderer(new ApoloTaggerListCellRenderer());
        mp3pathlist.setBackground(new Color(64, 64, 64));
        mp3pathlist_scroll = new JScrollPane(mp3pathlist);
        mp3pathlist_scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Initialize the playlist panel
        playlist = new JPanel(new BorderLayout());
        playlist.setBackground(Color.BLACK);
        playlist.add(playlist_label, BorderLayout.NORTH);
        playlist.add(mp3pathlist_scroll, BorderLayout.CENTER);
    }

    /**
     * Returns the JPanel representing the playlist.
     *
     * @return The JPanel representing the playlist.
     */
    public JPanel getPlaylist() {
        return playlist;
    }

    /**
     * Adds a music file to the playlist.
     *
     * @param path The path of the music file to be added.
     */
    public void addMusic(String path) {
        listModel.addElement(path);
    }

    /**
     * Returns the JList component containing the music files.
     *
     * @return The JList component containing the music files.
     */
    public JList<String> getMp3List() {
        return mp3pathlist;
    }

    /**
     * Removes the selected music file from the playlist.
     */
    public void removeSelectedMusic() {
        int selectedIndex = mp3pathlist.getSelectedIndex();
        listModel.remove(selectedIndex);
    }

    /**
     * Custom cell renderer for displaying file names in the playlist.
     */
    public class ApoloTaggerListCellRenderer extends DefaultListCellRenderer implements Serializable {
        private static final long serialVersionUID = 6L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JPanel musicPanel = new JPanel(new BorderLayout());
            musicPanel.setBackground(index % 2 == 0 ? new Color(64, 64, 64) : new Color(40, 40, 40));
            musicPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            if (value instanceof String) {
                String filePath = (String) value;
                String fileName = new File(filePath).getName().replaceFirst("[.][^.]+$", "");
                ImageIcon albumArt = getMP3AlbumArtwork(filePath);
                String title = getMP3Title(filePath);
                String duration = getMP3Duration(filePath);

                JPanel infoPanel = new JPanel(new BorderLayout());
                infoPanel.setBackground(musicPanel.getBackground());

                JLabel titleLabel = new JLabel(title != null && !title.isEmpty() ? title : fileName);
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


    private String getMP3Duration(String filePath) {
            try {
                AudioFile audioFile = AudioFileIO.read(new File(filePath));
                int trackLength = audioFile.getAudioHeader().getTrackLength(); // Duração da faixa em segundos

                int minutes = trackLength / 60;
                int seconds = trackLength % 60;
                return String.format("%02d:%02d", minutes, seconds);
            } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
                e.printStackTrace();
            }
            return null;
        }

        private String getMP3Title(String filePath) {
            try {
                AudioFile audioFile = AudioFileIO.read(new File(filePath));
                Tag tag = audioFile.getTag();
                if (tag != null) {
                    return tag.getFirst(FieldKey.TITLE);
                }
            } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
                e.printStackTrace();
            }
            return null;
        }


        private ImageIcon getMP3AlbumArtwork(String filePath) {
            try {
                AudioFile audioFile = AudioFileIO.read(new File(filePath));
                Tag tag = audioFile.getTag();
                if (tag != null) {
                    Artwork artwork = tag.getFirstArtwork();
                    if (artwork != null) {
                        byte[] imageData = artwork.getBinaryData();
                        if (imageData != null) {
                            return new ImageIcon(new ImageIcon(imageData).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}