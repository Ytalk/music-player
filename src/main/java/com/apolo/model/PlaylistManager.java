package com.apolo.model;

import com.apolo.gui.ApoloListCellRenderer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

import java.awt.Panel;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;

/**
 * The `PlaylistManager` class manages playlists in the music player application.
 * It provides functionality to create, delete, and load playlists, as well as access
 * the main list of playlists and the panel containing playlist information.
 */
public class PlaylistManager implements Serializable {

    private static final long serialVersionUID = 6L;

    private JList<String> mainList;//JList displaying the list containing the playlists
    private JPanel playlistPanel;//JPanel containing the selected playlist
    private Panel mainListPanel;//Panel containing the mainList of playlists
    private CardLayout cardlayout;//CardLayout for managing playlists
    private Map<String, Playlist> playlists;//Map to store playlist instances
    private PlaylistManager manager;//PlaylistManager instance to save state with serialization
    private JScrollPane scrollPlaylists;//JScrollPane for the mainList of playlists

    /**
     * Constructs a new PlaylistManager object.
     * Initializes data structures and UI components for managing playlists.
     */
    public PlaylistManager() {
        playlists = new HashMap<>();

        // Initialize card layout for playlists
        cardlayout = new CardLayout();
        playlistPanel = new JPanel(cardlayout);
        playlistPanel.setBackground(Color.BLACK);

        // Initialize main list of playlists
        mainList = new JList<>();
        mainList.setBackground(new Color(64, 64, 64));
        mainList.setCellRenderer(new ApoloListCellRenderer());
        scrollPlaylists = new JScrollPane(mainList);
        scrollPlaylists.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JLabel mainListLabel = new JLabel("Playlists");
        mainListLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainListLabel.setVerticalAlignment(SwingConstants.CENTER);
        mainListLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainListLabel.setForeground(Color.WHITE);

        mainListPanel = new Panel(new BorderLayout());
        mainListPanel.setBackground(Color.BLACK);
        mainListPanel.add(mainListLabel, BorderLayout.NORTH);
        mainListPanel.add(scrollPlaylists, BorderLayout.CENTER);
    }


    /**
     * Creates a new playlist with the specified name.
     * Adds the playlist to the manager and updates the UI accordingly.
     * @return The created playlist object.
     */
    public void createPlaylist(String playlistName) throws MusicException {
        if (playlistName == null) {
            return;
        }

        playlistName = playlistName.trim();

        if (playlistName.length() < 1 || playlistName.length() > 20) {
            throw new MusicException("Playlist name must be between 1 and 20 characters.", "Invalid Name");
        }

        if (playlists.containsKey(playlistName)) {
            throw new MusicException("Playlist with this name already exists.", "Duplicate Name");
        }

        Playlist playlist = new Playlist(playlistName);
        playlists.put(playlistName, playlist);

        //atualiza view
        mainList.setListData(playlists.keySet().toArray(new String[0]));
        playlistPanel.add(playlist.getPlaylist(), playlistName);
    }


    /**
     * Deletes the selected playlist from the manager.
     * Removes the playlist from the UI and updates the mainList of playlists.
     * @param pm The playlist manager instance.
     * @throws MusicException If no playlist is selected for deletion.
     */
    public void deletePlaylist(String playlistName) throws MusicException {
        if (!playlists.containsKey(playlistName)) {
            throw new MusicException("Select a playlist before deleting!", "Null Playlist");
        }
        playlists.remove(playlistName);
        //atualiza view
        mainList.setListData(playlists.keySet().toArray(new String[0]));
        if (!playlists.isEmpty()) {
            mainList.setSelectedIndex(0);
        } else {
            playlistPanel.removeAll();
            playlistPanel.revalidate();
            playlistPanel.repaint();
        }
    }


    /**
     * Retrieves the mainList of playlists.
     * @return The JList containing the mainList of playlists.
     */
    public JList getMainList() {
        return mainList;
    }

    /**
     * Retrieves the panel containing the mainList of playlists for management in the UI.
     * @return The panel containing the mainList of playlists.
     */
    public Panel getMainListPanel() {
        return mainListPanel;
    }

    /**
     * Retrieves the panel with CardLayout containing a possible selected playlist.
     * @return The panel containing the selected playlist.
     */
    public JPanel getPlaylistPanel() {
        return playlistPanel;
    }

    /**
     * Retrieves the map of playlists for management in the UI.
     * @return The map containing playlist names as keys and playlist objects as values.
     */
    public Map<String, Playlist> getMap() {
        return playlists;
    }

    /**
     * Retrieves the card layout for managing playlists.
     * @return The CardLayout object used for managing playlists.
     */
    public CardLayout getCardLayout() {
        return cardlayout;
    }


    /**
     * Saves the current state of the PlaylistManager to a file.
     * @param pm The PlaylistManager instance to be saved.
     */
    public void saveToFile(PlaylistManager pm) throws MusicException{
        manager = pm;
        //try-with-resources. cria OOS para escrever. FOS abre arquivo para para escrever bytes
        try(ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream("playlists.byte"))){
            writer.writeObject(manager);
        } catch(IOException e){
            throw new MusicException("Error saving file: " + e.getMessage(), "Saving Error");
        }
    }


    /**
     * Loads the saved state of the PlaylistManager from a file.
     */
    public void loadFile() throws MusicException{////////////////////////////////
        try(ObjectInputStream reader = new ObjectInputStream(new FileInputStream("playlists.byte"))){//cria OIS para ler objetos do arquivo
            manager = ( (PlaylistManager) reader.readObject() );//lê os objetos serializados do arquivo e guarda dentro da classe que representa ela mesma
        }
        catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "The serialized file containing playlists was not found!", "Playlist Not Found", JOptionPane.WARNING_MESSAGE);
        }
        catch (IOException | ClassNotFoundException e) {
            throw new MusicException(e.getMessage(), "Something went wrong loading the playlists!");
        }
    }


    /**
     * Retrieves the PlaylistManager instance with the current state to enable playlist management in the UI.
     * @return The PlaylistManager instance.
     */
    public PlaylistManager getManager(){
        return manager;
    }

}