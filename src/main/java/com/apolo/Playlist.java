package com.apolo;

import javax.swing.*;
import java.awt.*;
import java.awt.BorderLayout;
import java.io.File;
import java.io.Serializable;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.border.Border;

public class Playlist implements Serializable {

    private static final long serialVersionUID = 6L;
    JScrollPane scrollPane;//painel para scrollar a lista de musicas
    DefaultListModel<String> listModel;//representa o modelo/gerenciador da playlist
    JList<String> mp3List;//representa a lista de musicas
    JLabel name;
    JPanel playlist;//painel com as informações de uma playlist

    public Playlist(String name) {
        this.name = new JLabel(name);
        this.name.setHorizontalAlignment(SwingConstants.CENTER);
        this.name.setVerticalAlignment(SwingConstants.CENTER);
        this.name.setFont(new Font("Arial", Font.BOLD, 20));
        this.name.setForeground(Color.WHITE);

        listModel = new DefaultListModel<>();

        mp3List = new JList<>();
        mp3List.setModel(listModel);
        mp3List.setCellRenderer(new FileNameCellRenderer());
        mp3List.setBackground(new Color(64, 64, 64));
        scrollPane = new JScrollPane(mp3List);

        playlist = new JPanel(new BorderLayout());
        playlist.setBackground(Color.BLACK);//cor de fundo do cabeçalho 33, 41, 48 / 129, 13, 175
        playlist.add(this.name, BorderLayout.NORTH);
        playlist.add(scrollPane, BorderLayout.CENTER);
    }


    public JPanel getPlaylist(){
        return playlist;
    }


    public void addMusic(String path){
        listModel.addElement(path);
    }


    public JList<String> getMp3List(){
        return mp3List;
    }


    private class FileNameCellRenderer extends DefaultListCellRenderer implements Serializable{
        private static final long serialVersionUID = 6L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value instanceof String) {
                value = new File((String) value).getName().replaceFirst("[.][^.]+$", "");//obtém apenas o nome do arquivo e retira extensão
                
            }
            //método da superclasse para obter o componente de renderização padrão
            Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            //verifica se o item está selecionado
            if (isSelected) {
                //item selecionado
                renderer.setBackground(new Color(129, 13, 175));
                renderer.setForeground(Color.BLACK);
            } else {
                //item não selecionado
                renderer.setBackground(list.getBackground());
                renderer.setForeground(Color.BLACK);
            }

            return renderer;
        }
    }


    public void removeSelectedMusic() {
        int selectedIndex = mp3List.getSelectedIndex();
        if (selectedIndex >= 0) {
            listModel.remove(selectedIndex);
        }
    }


}
