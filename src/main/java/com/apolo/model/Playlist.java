package com.apolo.model;

import com.apolo.gui.ApoloTaggerListCellRenderer;

import java.io.Serializable;

import java.awt.Font;
import java.awt.Color;
import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.DefaultListModel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;


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


    public DefaultListModel getListModel(){
        return listModel;
    }

    /**
     * Returns the JList component containing the music files.
     *
     * @return The JList component containing the music files.
     */
    public JList<String> getMp3List() {
        return mp3pathlist;
    }

}