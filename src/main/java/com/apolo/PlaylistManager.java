package com.apolo;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PlaylistManager {

    private JList<String> mainList;//jlist com str para card
    private JPanel playlists_panel;//card
    private Panel mainList_panel;//panel playlist manager
    private CardLayout cardLayout;
    private Map<String, Playlist> playlists;  //armazena instâncias de Playlist

    public PlaylistManager() {
        playlists = new HashMap<>();


        //CARD
        playlists_panel = new JPanel();
        cardLayout = new CardLayout();
        playlists_panel.setLayout(cardLayout);
        playlists_panel.setBackground(new Color( 129, 13, 175));


        //JLIST DE PLAYLISTS
        mainList = new JList<>(new String[0]);
        mainList.setBackground(new Color(64, 64, 64));

        JLabel mainList_label = new JLabel("Playlists");//label de playlist manager
        mainList_label.setHorizontalAlignment(SwingConstants.CENTER);
        mainList_label.setVerticalAlignment(SwingConstants.CENTER);
        mainList_label.setFont(new Font("Arial", Font.BOLD, 20));
        mainList_label.setForeground(Color.WHITE);

        mainList_panel = new Panel(new BorderLayout());//panel de playlist manager
        mainList_panel.setBackground(new Color(33, 41, 48));//cor de fundo que pega a label
        mainList_panel.add(mainList_label, BorderLayout.NORTH);
        mainList_panel.add(mainList, BorderLayout.CENTER);

    }


    public Playlist creatPlaylist(){
        String playlistName = JOptionPane.showInputDialog("Enter the name of the new playlist:");
        if (playlistName != null && !playlistName.isEmpty()) {
            Playlist playlist = new Playlist(playlistName);
            playlists.put(playlistName, playlist);

            // Atualiza a mainList com os nomes das playlists existentes
            mainList.setListData(playlists.keySet().toArray(new String[0]));

            // Adiciona a nova playlist ao cardLayout
            playlists_panel.add(playlist.getPlaylist(), playlistName);

            return playlist;
        }
    return null;
    }


    public void deletePlaylist(){
        String playlist_name = mainList.getSelectedValue();
        Playlist playlist = playlists.get(playlist_name);

        if (playlist != null) {
            playlists_panel.remove(playlist.getPlaylist());
            playlists.remove(playlist_name);
            mainList.setListData(playlists.keySet().toArray(new String[0]));
        }
    }


    public void switchPlaylist(){
        mainList.addListSelectionListener(e -> {
            String selectedPlaylist = mainList.getSelectedValue();
            cardLayout.show(playlists_panel, selectedPlaylist);
        });
    }


    public JList getMainList(){
        return mainList;
    }


    public Panel getPlaylistManagerPanel(){
        return mainList_panel;
    }


    public JPanel getPlaylistCard() {
        return playlists_panel;
    }


    public Map<String, Playlist> getMap(){
        return playlists;
    }
}