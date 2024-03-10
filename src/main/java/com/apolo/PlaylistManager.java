package com.apolo;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * The `PlaylistManager` class manages playlists in the music player application.
 * It provides functionality to create, delete, and load playlists, as well as access
 * the main list of playlists and the panel containing playlist information.
 */
public class PlaylistManager implements Serializable {

    private static final long serialVersionUID = 6L;

    private JList<String> mainList; // JList displaying the main list of playlists
    private JPanel playlistPanel; // Panel containing playlists
    private Panel mainListPanel; // Panel containing the main list of playlists
    private CardLayout playlistCardlayout; // Card layout for managing playlists
    private Map<String, Playlist> playlists; // Map to store playlist instances
    private PlaylistManager manager; // Instance of the playlist manager
    private JScrollPane scrollPlaylists; // Scroll pane for the main list of playlists

    /**
     * Constructs a new PlaylistManager object.
     * Initializes data structures and UI components for managing playlists.
     */
    public PlaylistManager() {
        playlists = new HashMap<>();

        // Initialize card layout for playlists
        playlistCardlayout = new CardLayout();
        playlistPanel = new JPanel(playlistCardlayout);
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
    public void createPlaylist() {
        String playlistName;

        while (true) {
            playlistName = JOptionPane.showInputDialog(null, "Enter playlist name (1 to 20 characters):", "New Playlist", JOptionPane.PLAIN_MESSAGE);

            if (playlistName == null) {
                break;
            }

            if (playlistName.length() < 1 || playlistName.length() > 20) {
                JOptionPane.showMessageDialog(null, "Playlist name must be between 1 and 20 characters.", "Invalid Name", JOptionPane.ERROR_MESSAGE);
            } else if (playlistName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Playlist name cannot be empty or consist only of whitespace.", "Invalid Name", JOptionPane.ERROR_MESSAGE);
            } else if (playlists.containsKey(playlistName)) {
                JOptionPane.showMessageDialog(null, "Playlist with this name already exists. Please choose a different name.", "Duplicate Name", JOptionPane.ERROR_MESSAGE);
            } else {
                Playlist playlist = new Playlist(playlistName);
                playlists.put(playlistName, playlist);

                mainList.setListData(playlists.keySet().toArray(new String[0]));

                playlistPanel.add(playlist.getPlaylist(), playlistName);
                return;
            }
        }

        return;
    }


    /**
     * Deletes the selected playlist from the manager.
     * Removes the playlist from the UI and updates the main list of playlists.
     * @param pm The playlist manager instance.
     * @throws musicException If no playlist is selected for deletion.
     */
    public void deletePlaylist(PlaylistManager pm) throws musicException{
        String playlist_name = mainList.getSelectedValue();
        Playlist playlist = playlists.get(playlist_name);

        if (playlist != null) {
            int confirm_playlist_del = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the " + playlist_name + " playlist?", "Confirm Playlist Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm_playlist_del == JOptionPane.YES_OPTION) {
                playlistPanel.remove(playlist.getPlaylist());
                playlists.remove(playlist_name);
                mainList.setListData(playlists.keySet().toArray(new String[0]));

                if (!playlists.isEmpty()) {//isso aqui está quase inutil, atualizar del e creat playlist
                    mainList.setSelectedIndex(0);
                    String firstPlaylist = mainList.getSelectedValue();
                    playlistCardlayout.show(playlistPanel, firstPlaylist);
                }

                pm.saveToFile(pm);
            }
        }
        else{
            throw new musicException("Select a playlist before deleting!", "Null Playlist");
        }
    }


    /**
     * Retrieves the main list of playlists.
     * @return The JList containing the main list of playlists.
     */
    public JList getMainList() {
        return mainList;
    }

    /**
     * Retrieves the panel containing the playlist manager UI components.
     * @return The panel containing the main list of playlists.
     */
    public Panel getMainListPanel() {
        return mainListPanel;
    }

    /**
     * Retrieves the panel containing the playlists.
     * @return The panel containing the playlists.
     */
    public JPanel getPlaylistPanel() {
        return playlistPanel;
    }

    /**
     * Retrieves the map of playlists.
     * @return The map containing playlist names as keys and playlist objects as values.
     */
    public Map<String, Playlist> getMap() {
        return playlists;
    }

    /**
     * Retrieves the card layout for managing playlists.
     * @return The CardLayout object used for managing playlists.
     */
    public CardLayout getPlaylistCardLayout() {
        return playlistCardlayout;
    }


    /**
     * Saves the current state of the PlaylistManager to a file.
     * @param pm The PlaylistManager instance to be saved.
     */
    public void saveToFile(PlaylistManager pm){
        manager = pm;

        try(ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream("src/main/java/com/apolo/playlists.byte"))){//cria OOS para escrever. FOS abre arquivo para para escrever bytes
            writer.writeObject(manager);//escreve no arquivo
        }
        catch(IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving file: " + e.getMessage(), "Saving Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads the saved state of the PlaylistManager from a file.
     */
    public void loadFile(){
        try(ObjectInputStream reader = new ObjectInputStream(new FileInputStream("src/main/java/com/apolo/playlists.byte"))){//cria OIS para ler objetos do arquivo
            manager = ( (PlaylistManager) reader.readObject() );//lê os objetos serializados do arquivo e guarda dentro da classe que representa ela mesma
        }
        catch (FileNotFoundException e){
            //JOptionPane.showMessageDialog(null, "The serialized file containing playlists was not found!", "Playlist Not Found", JOptionPane.WARNING_MESSAGE);
        }
        catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Something went wrong loading the playlists!", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Retrieves the PlaylistManager instance.
     * @return The PlaylistManager instance.
     */
    public PlaylistManager getManager(){
        return manager;
    }


    public class ApoloListCellRenderer extends DefaultListCellRenderer  implements Serializable{
        private static final long serialVersionUID = 6L;

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


}