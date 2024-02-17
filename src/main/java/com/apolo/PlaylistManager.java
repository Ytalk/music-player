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

public class PlaylistManager implements Serializable{

    private static final long serialVersionUID = 6L;
    private JList<String> mainList;//jlist com str para card
    private JPanel playlists_panel;//card
    private Panel mainList_panel;//panel playlist manager
    private CardLayout playlists_cardlayout;
    private Map<String, Playlist> playlists;  //armazena instâncias de Playlist
    private PlaylistManager manager;
    JScrollPane scrollPlaylists;


    public PlaylistManager() {
        playlists = new HashMap<>();

        //CARD
<<<<<<< HEAD
        playlists_cardlayout = new CardLayout();
        playlists_panel = new JPanel( playlists_cardlayout );
        playlists_panel.setBackground(Color.BLACK);//cor do panel-card sem playlist
=======
        playlists_panel = new JPanel();
        cardLayout = new CardLayout();
        playlists_panel.setLayout(cardLayout);
        playlists_panel.setBackground(Color.BLACK);//cor inicial do card
>>>>>>> 2845dfc72293268ffb2dc0dcf45246d137aa3f30


        //JLIST DE PLAYLISTS
        mainList = new JList<>();
        mainList.setBackground(new Color(64, 64, 64));
        mainList.setCellRenderer(new PurpleListRenderer());
<<<<<<< HEAD
        scrollPlaylists = new JScrollPane(mainList);
        scrollPlaylists.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
=======
        JScrollPane scrollPlaylists = new JScrollPane(mainList);
>>>>>>> 2845dfc72293268ffb2dc0dcf45246d137aa3f30

        JLabel mainList_label = new JLabel("Playlists");//label de playlist manager
        mainList_label.setHorizontalAlignment(SwingConstants.CENTER);
        mainList_label.setVerticalAlignment(SwingConstants.CENTER);
        mainList_label.setFont(new Font("Arial", Font.BOLD, 20));
        mainList_label.setForeground(Color.WHITE);

        mainList_panel = new Panel(new BorderLayout());//panel de playlist manager
        mainList_panel.setBackground(Color.BLACK);//cor de fundo que pega a label
        mainList_panel.add(mainList_label, BorderLayout.NORTH);
        mainList_panel.add(scrollPlaylists, BorderLayout.CENTER);
<<<<<<< HEAD
=======

>>>>>>> 2845dfc72293268ffb2dc0dcf45246d137aa3f30
    }


    public Playlist creatPlaylist(){

        String playlistName = "";
        while( playlistName.length() < 1 || playlistName.length() > 20 ) {
            playlistName = JOptionPane.showInputDialog(null, "Enter playlist name (1 to 20 characters):", "New Playlist", JOptionPane.PLAIN_MESSAGE);

            if (playlistName == null) {
                break;
            }
        }

        if (playlistName != null) {
            Playlist playlist = new Playlist(playlistName);
            playlists.put(playlistName, playlist);

            //atualiza a mainList com os nomes das playlists existentes
            mainList.setListData(playlists.keySet().toArray(new String[0]));

            //adiciona a nova playlist ao card
            playlists_panel.add(playlist.getPlaylist(), playlistName);

            return playlist;
        }
        return null;
    }


    public void deletePlaylist(PlaylistManager pm) throws musicException{
        String playlist_name = mainList.getSelectedValue();
        Playlist playlist = playlists.get(playlist_name);

        if (playlist != null) {
            int confirm_playlist_del = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the " + playlist_name + " playlist?", "Confirm Playlist Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm_playlist_del == JOptionPane.YES_OPTION) {
                playlists_panel.remove(playlist.getPlaylist());
                playlists.remove(playlist_name);
                mainList.setListData(playlists.keySet().toArray(new String[0]));
                pm.saveToFile(pm);
            }
            else{//NO or X
                return;
            }
        }
        else{
            throw new musicException("Select a playlist before deleting!", "no playlist selected");
        }
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


    public CardLayout getPlaylistsCardLayout(){
        return playlists_cardlayout;
    }


    public void saveToFile(PlaylistManager pm){
        manager = pm;

        try(ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream("src\\main\\java\\com\\apolo\\playlist.byte"))){//cria OOS para escrever. FOS abre arquivo para para escrever bytes
            writer.writeObject(manager);//escreve no arquivo
        }
        catch(IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving file: " + e.getMessage(), "Saving Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void loadFile(){

        try(ObjectInputStream reader = new ObjectInputStream(new FileInputStream("src\\main\\java\\com\\apolo\\playlist.byte"))){//cria OIS para ler objetos do arquivo

            manager = ( (PlaylistManager) reader.readObject() );//lê os objetos serializados do arquivo e guarda dentro da classe que representa ela mesma

            //reader.registerValidation(this, 0);//registra o objeto para validação. validação imediata (prioridade 0 / alta).
        }

        catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "The serialized file containing playlists was not found!", "Playlist Not Found", JOptionPane.WARNING_MESSAGE);
        }

        catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Something went wrong loading the playlists!", JOptionPane.ERROR_MESSAGE);
        }
    }


    public PlaylistManager getManager(){
        return manager;
    }


    public class PurpleListRenderer extends DefaultListCellRenderer  implements Serializable{
<<<<<<< HEAD
        private static final long serialVersionUID = 6L;

=======
>>>>>>> 2845dfc72293268ffb2dc0dcf45246d137aa3f30
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

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


}