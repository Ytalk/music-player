package com.apolo;

import javax.swing.*;
import java.awt.Dimension;

public class Playlist {

    JScrollPane scrollPane;//representa a lista de musicas
    DefaultListModel<String> listModel;//representa o modelo/gerenciador da playlist
    JList<String> mp3List;//

    public Playlist() {
        mp3List = new JList<>();
        listModel = new DefaultListModel<>();
        mp3List.setModel(listModel);

        this.scrollPane = new JScrollPane(mp3List);
    }

    public JScrollPane getPlaylist(){
        return scrollPane;
    }

    public void addMusic(String path){
        this.listModel.addElement(path);
    }


    public JList<String> getMp3List(){
        return mp3List;
    }


}
