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
    private JScrollPane mp3pathlist_scroll;//painel para scrollar a lista de musicas
    private DefaultListModel<String> listModel;//representa o modelo/gerenciador da playlist
    private JList<String> mp3pathlist;//representa a lista de musicas
    private JLabel playlist_label;
    private JPanel playlist;//painel com as informações de uma playlist

    public Playlist(String name) {
        playlist_label = new JLabel(name);
        playlist_label.setHorizontalAlignment(SwingConstants.CENTER);
        playlist_label.setVerticalAlignment(SwingConstants.CENTER);
        playlist_label.setFont(new Font("Arial", Font.BOLD, 20));
        playlist_label.setForeground(Color.WHITE);

        listModel = new DefaultListModel<>();
<<<<<<< HEAD
        mp3pathlist = new JList<>();
        mp3pathlist.setModel(listModel);
        mp3pathlist.setCellRenderer(new FileNameCellRenderer());
        mp3pathlist.setBackground(new Color(64, 64, 64));
        mp3pathlist_scroll = new JScrollPane(mp3pathlist);
        mp3pathlist_scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        playlist = new JPanel(new BorderLayout());
        playlist.setBackground(Color.BLACK);//cor de fundo do cabeçalho 33, 41, 48 / 129, 13, 175
        playlist.add(playlist_label, BorderLayout.NORTH);
        playlist.add(mp3pathlist_scroll, BorderLayout.CENTER);
=======

        mp3List = new JList<>();
        mp3List.setModel(listModel);
        mp3List.setCellRenderer(new FileNameCellRenderer());
        mp3List.setBackground(new Color(64, 64, 64));
        scrollPane = new JScrollPane(mp3List);

        playlist = new JPanel(new BorderLayout());
        playlist.setBackground(Color.BLACK);//cor de fundo do cabeçalho 33, 41, 48 / 129, 13, 175
        playlist.add(this.name, BorderLayout.NORTH);
        playlist.add(scrollPane, BorderLayout.CENTER);
>>>>>>> 2845dfc72293268ffb2dc0dcf45246d137aa3f30
    }


    public JPanel getPlaylist(){
        return playlist;
    }


    public void addMusic(String path){
        listModel.addElement(path);
    }


    public JList<String> getMp3List(){
        return mp3pathlist;
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

<<<<<<< HEAD
=======
            //verifica se o item está selecionado
>>>>>>> 2845dfc72293268ffb2dc0dcf45246d137aa3f30
            if (isSelected) {
                //item selecionado
                renderer.setBackground(new Color(129, 13, 175));
                renderer.setForeground(Color.BLACK);
<<<<<<< HEAD
            }
            else {
                //não selecionado
                renderer.setForeground(Color.BLACK);
                if (index % 2 == 0) {
                    renderer.setBackground( new Color(64, 64, 64) );
                }
                else {
                    renderer.setBackground( new Color( 40, 40, 40) );
                }
=======
            } else {
                //item não selecionado
                renderer.setBackground(list.getBackground());
                renderer.setForeground(Color.BLACK);
>>>>>>> 2845dfc72293268ffb2dc0dcf45246d137aa3f30
            }

            return renderer;
        }
    }


    public void removeSelectedMusic() {
        int selectedIndex = mp3pathlist.getSelectedIndex();
        if (selectedIndex >= 0) {
            listModel.remove(selectedIndex);
        }
    }


}
