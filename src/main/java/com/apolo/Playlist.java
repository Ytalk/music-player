package com.apolo;

import javax.swing.*;
import java.awt.*;
import java.awt.BorderLayout;
import java.io.File;

public class Playlist {

    JScrollPane scrollPane;//representa a lista de musicas
    DefaultListModel<String> listModel;//representa o modelo/gerenciador da playlist
    JList<String> mp3List;//
    Label name;
    JPanel playlist;

    public Playlist(String name) {
        this.name = new Label(name);
        mp3List = new JList<>();
        listModel = new DefaultListModel<>();
        mp3List.setModel(listModel);
        mp3List.setCellRenderer(new FileNameCellRenderer());

        scrollPane = new JScrollPane(mp3List);
        playlist = new JPanel(new BorderLayout());

        playlist.add(this.name, BorderLayout.NORTH);
        playlist.add(scrollPane, BorderLayout.CENTER);
    }


    public JPanel getPlaylist(){
        return playlist;
    }


    public void addMusic(String path){
        this.listModel.addElement(path);
    }


    public JList<String> getMp3List(){
        return mp3List;
    }


    private class FileNameCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value instanceof String) {
                value = new File((String) value).getName(); // Obtém apenas o nome do arquivo
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }



}
