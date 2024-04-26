package com.apolo.model;

import com.apolo.gui.MusicException;

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
import java.awt.Component;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.DefaultListCellRenderer;
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
    public void createPlaylist() {
        String playlistName;
        while (true) {
            playlistName = JOptionPane.showInputDialog(null, "Enter playlist name (1 to 20 characters):", "New Playlist", JOptionPane.PLAIN_MESSAGE);

            if (playlistName == null) {
                return;
            }

            playlistName = playlistName.trim();

            if (playlistName.length() < 1 || playlistName.length() > 20) {
                JOptionPane.showMessageDialog(null, "Playlist name must be between 1 and 20 characters.", "Invalid Name", JOptionPane.ERROR_MESSAGE);
            } else if (playlists.containsKey(playlistName)) {
                JOptionPane.showMessageDialog(null, "Playlist with this name already exists. Please choose a different name.", "Duplicate Name", JOptionPane.ERROR_MESSAGE);
            } else {
                Playlist playlist = new Playlist(playlistName);
                playlists.put(playlistName, playlist);

                mainList.setListData(playlists.keySet().toArray(new String[0]));
                playlistPanel.add(playlist.getPlaylist(), playlistName);
                //mainList.setSelectedIndex(0);
                return;
            }

        }
    }


    /**
     * Deletes the selected playlist from the manager.
     * Removes the playlist from the UI and updates the mainList of playlists.
     * @param pm The playlist manager instance.
     * @throws MusicException If no playlist is selected for deletion.
     */
    public void deletePlaylist(PlaylistManager pm) throws MusicException{
        String selectedPlaylistName = mainList.getSelectedValue();
        Playlist playlist = playlists.get(selectedPlaylistName);

        if (playlist == null) {
            throw new MusicException("Select a playlist before deleting!", "Null Playlist");
        }

        int confirmPlaylistDel = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the " + selectedPlaylistName + " playlist?", "Confirm Playlist Deletion", JOptionPane.YES_NO_OPTION);
        if (confirmPlaylistDel == JOptionPane.YES_OPTION) {
            playlists.remove(selectedPlaylistName);
            mainList.setListData(playlists.keySet().toArray(new String[0]));

            if (!playlists.isEmpty()) {
                mainList.setSelectedIndex(0);
            } else{
                playlistPanel.remove(playlist.getPlaylist());
            }

            pm.saveToFile(pm);
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
    public void saveToFile(PlaylistManager pm){
        manager = pm;

        try(ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream("playlists.byte"))){//cria OOS para escrever. FOS abre arquivo para para escrever bytes
            writer.writeObject(manager);
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
        try(ObjectInputStream reader = new ObjectInputStream(new FileInputStream("playlists.byte"))){//cria OIS para ler objetos do arquivo
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
     * Retrieves the PlaylistManager instance with the current state to enable playlist management in the UI.
     * @return The PlaylistManager instance.
     */
    public PlaylistManager getManager(){
        return manager;
    }


    /**
     * Custom list cell renderer for displaying elements in a JList (mainList) with alternating background colors.
     * Background color alternates between two colors for better visual distinction.
     */
    public class ApoloListCellRenderer extends DefaultListCellRenderer  implements Serializable{
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

}