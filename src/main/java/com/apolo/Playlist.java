package com.apolo;

import javax.swing.*;
import java.awt.*;
import java.awt.BorderLayout;
import java.io.File;
import java.io.Serializable;
import javax.swing.SwingConstants;
import javax.swing.JLabel;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.images.Artwork;
import java.io.IOException;

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
        // Initialize the playlist label
        playlist_label = new JLabel(name);
        playlist_label.setHorizontalAlignment(SwingConstants.CENTER);
        playlist_label.setVerticalAlignment(SwingConstants.CENTER);
        playlist_label.setFont(new Font("Arial", Font.BOLD, 20));
        playlist_label.setForeground(Color.WHITE);

        // Initialize the list model and list of music files
        listModel = new DefaultListModel<>();
        mp3pathlist = new JList<>();
        mp3pathlist.setModel(listModel);
        mp3pathlist.setCellRenderer(new FileNameCellRenderer());
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
        if (selectedIndex >= 0) {
            listModel.remove(selectedIndex);
        }
    }

    /**
     * Custom cell renderer for displaying file names in the playlist.
     */
    public class FileNameCellRenderer extends DefaultListCellRenderer implements Serializable {
        private static final long serialVersionUID = 6L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JPanel panel = new JPanel(new BorderLayout());

            if (value instanceof String) {
                // Obtain only the file name without extension
                String fileName = new File((String) value).getName().replaceFirst("[.][^.]+$", "");

                // Get the album artwork
                String filePath = (String) list.getModel().getElementAt(index);
                ImageIcon albumArt = getMP3AlbumArtwork(filePath);

                // Get the title, artist, and duration of the MP3
                String title = getMP3Title(filePath);
                String duration = getMP3Duration(filePath);
                String artist = getMP3Artist(filePath);


                // Create labels for the title, artist, and duration
                JLabel titleLabel = new JLabel(title);
                titleLabel.setForeground(Color.BLACK);

                JLabel artistLabel = new JLabel(artist);
                artistLabel.setForeground(Color.BLACK);

                JLabel durationLabel = new JLabel(duration);
                durationLabel.setForeground(Color.BLACK);

                // Create a panel to hold the title, artist, and duration labels
                GridLayout gl = new GridLayout(1, 2);
                gl.setHgap(20);
                JPanel infoPanel = new JPanel(gl);
                infoPanel.setBackground(index % 2 == 0 ? new Color(64, 64, 64) : new Color(40, 40, 40));


                /*if( artist != null && !(artist.equals("")) ) {
                    infoPanel.add(artistLabel);
                } else{
                    infoPanel.add(new JLabel("unknown") );
                }*/

                if( title != null ) {
                    infoPanel.add(titleLabel);
                } else {
                    infoPanel.add( new JLabel(fileName) );
                }

                infoPanel.add(durationLabel);

                // Add the album artwork and info panel to the main panel
                if (albumArt != null) {
                    JLabel albumArtLabel = new JLabel(albumArt);
                    panel.add(albumArtLabel, BorderLayout.WEST);
                }
                panel.add(infoPanel, BorderLayout.CENTER);
            } else {
                panel.add(new JLabel(value.toString()), BorderLayout.CENTER);
            }


            // Customize the appearance based on selection and index
            if (isSelected) {
                panel.setBackground(new Color(129, 13, 175));
                panel.setForeground(Color.BLACK);
            } else {
                panel.setForeground(Color.BLACK);
                panel.setBackground(index % 2 == 0 ? new Color(64, 64, 64) : new Color(40, 40, 40));
            }

            // Configure as propriedades de exibição do JLabel
            panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Adicione alguma margem ao redor do texto

            return panel;
        }

        private String getMP3Duration(String filePath) {
            try {
                AudioFile audioFile = AudioFileIO.read(new File(filePath));
                int trackLength = audioFile.getAudioHeader().getTrackLength(); // Duração da faixa em segundos
                long milliseconds = trackLength * 1000; // Convertendo segundos para milissegundos
                long minutes = milliseconds / (1000 * 60);
                long seconds = (milliseconds / 1000) % 60;
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

        private String getMP3Artist(String filePath) {
            try {
                AudioFile audioFile = AudioFileIO.read(new File(filePath));
                Tag tag = audioFile.getTag();
                if (tag != null) {
                    return tag.getFirst(FieldKey.ARTIST);
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
                            // Cria um ImageIcon com a imagem de capa do álbum
                            ImageIcon icon = new ImageIcon(imageData);
                            // Redimensiona a imagem para 50x50 pixels
                            Image scaledImage = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                            // Cria um novo ImageIcon com a imagem redimensionada
                            return new ImageIcon(scaledImage);
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