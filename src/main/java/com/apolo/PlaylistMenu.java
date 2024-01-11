package com.apolo;

import javax.swing.*;
import java.awt.*;

public class PlaylistMenu {

    JList<String> mainList;//jlist com str para card
    JPanel create_playlist;//card
    private DefaultListModel<String> mainListModel;//para modificar

    public PlaylistMenu(){
        //card
        create_playlist = new JPanel();
        CardLayout cardLayout = new CardLayout();
        create_playlist.setLayout(cardLayout);


        mainListModel = new DefaultListModel<>();
        mainList = new JList<>(mainListModel);


        //adicione um ouvinte para alternar entre playlists
        /*mainList.addListSelectionListener(e -> {
            String selectedPlaylist = mainList.getSelectedValue();
            cardLayout.show(create_playlist, selectedPlaylist);
        });*/
    }

    public void newPlaylist(Playlist p1, Playlist p2){
        create_playlist.add( p1.getPlaylist(), "playlist test" );//adiciona panel playlist ao card de playlist
        create_playlist.add( p2.getPlaylist(), "playlist song" );//adiciona panel playlist ao card de playlist

        mainListModel.addElement("playlist test");
        mainListModel.addElement("playlist song");
    }


    public JList<String> getMenuList(){
        return mainList;
    }


    public JPanel getPlaylistCard(){
        return create_playlist;
    }

}
