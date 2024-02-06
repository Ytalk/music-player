package com.apolo;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Map;
import java.io.File;


public class Apolo extends JFrame{

    Thread musicThread = new Thread();
    private Map<String, Playlist> playlists;  //armazena instâncias de Playlist
    private JList<String> mainList;
    private Playlist playlist;
    private CardLayout cardLayout;
    private Play music = new Play();
    private boolean pause = true;

    private ImageIcon addMusicIcon = getIcon("/icons/392_4-more-white.png", 17, 17);
    private ImageIcon deleteIcon = getIcon("/icons/rectangle-632-180.png", 17, 7);

    public class importUserPlaylists{

        public PlaylistManager getPlaylists() {
            PlaylistManager playlist_manager = new PlaylistManager();
            playlist_manager.loadFile();
            if (playlist_manager.getManager() != null) {
                playlist_manager = playlist_manager.getManager();
            }

            return playlist_manager;
        }

    }

    public Apolo(){

        PlaylistManager playlist_manager = new importUserPlaylists().getPlaylists();

        mainList = playlist_manager.getMainList();
        playlists = playlist_manager.getMap();
        cardLayout = playlist_manager.getCardLayout();



        //CRIAR OU DELETAR PLAYLIST
        JButton createPlaylistButton = new JButton(addMusicIcon);
        createPlaylistButton.setBackground(new Color(33, 41, 48));
        createPlaylistButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        createPlaylistButton.setBounds(170, 52, 20, 20);
        createPlaylistButton.setFocusPainted(false);
        add(createPlaylistButton);

        createPlaylistButton.addActionListener(e -> {
            playlist = playlist_manager.creatPlaylist();
            playlist_manager.saveToFile(playlist_manager);
        });


        JButton delete_playlist_button = new JButton(deleteIcon);
        delete_playlist_button.setBounds(30, 57, 20, 10);// posição e tamanho
        delete_playlist_button.setBackground(new Color(33, 41, 48));
        delete_playlist_button.setBorder(new EmptyBorder(0, 0, 0, 0));
        delete_playlist_button.setFocusPainted(false);
        add(delete_playlist_button);

        delete_playlist_button.addActionListener(e -> {
            try {
                playlist_manager.deletePlaylist();
                playlist_manager.saveToFile(playlist_manager);
            }
            catch (musicException ex){
                ex.showMessage();
            }
        });



        //abrir ou deletar musica
        JButton addMusicButton = new JButton(addMusicIcon);//aclopar na playlist
        addMusicButton.setBackground(new Color(33, 41, 48));
        addMusicButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        addMusicButton.setBounds(700, 52, 20, 20);
        addMusicButton.setFocusPainted(false);
        add(addMusicButton);

        addMusicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    String selectedPlaylist = mainList.getSelectedValue();

                    if (selectedPlaylist == null) {
                        throw new musicException("Select a playlist before adding a song!", "Null Playlist");
                    }
                    else {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Specify a file to open");
                        FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivos MP3", "mp3");
                        fileChooser.setFileFilter(filter);

                        int result = fileChooser.showOpenDialog(null);

                        if (result == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = fileChooser.getSelectedFile();
                            String filePath = selectedFile.getAbsolutePath();

                            playlist = playlists.get(selectedPlaylist);
                            playlist.addMusic(filePath);
                            playlist_manager.saveToFile(playlist_manager);
                        }
                    }

                } catch (musicException ex){
                   ex.showMessage();
                }

            }
        });



        JButton delete_button = new JButton(deleteIcon);
        delete_button.setBounds(270, 57, 20, 10);// posição e tamanho
        delete_button.setBackground(new Color(33, 41, 48));
        delete_button.setBorder(new EmptyBorder(0, 0, 0, 0));
        delete_button.setFocusPainted(false);
        add(delete_button);

        delete_button.addActionListener(e -> {
            String selectedPlaylist = mainList.getSelectedValue();

            try {
                if (selectedPlaylist == null) {
                    throw new musicException("Select a playlist before deleting a song!", "no playlist selected");
                }

                int confirm_playlist_del = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected music?", "Confirm Playlist Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm_playlist_del == JOptionPane.YES_OPTION) {
                    playlist = playlists.get(selectedPlaylist);
                    playlist.removeSelectedMusic();
                    playlist_manager.saveToFile(playlist_manager);
                }
            }
            catch (musicException ex){
                ex.showMessage();
            }
        });



        //adicione um ouvinte para alternar entre playlists
        mainList.addListSelectionListener(e -> {
            String selectedPlaylist = mainList.getSelectedValue();
            cardLayout.show(playlist_manager.getPlaylistCard(), selectedPlaylist);
            addMusicButton.repaint();
            delete_button.repaint();
        });



        //adicione a lista principal e as playlists (card) ao JFrame
        playlist_manager.getPlaylistCard().setBounds(250, 50, 500, 300);//posição e tamanho do card de uma playlist
        add(playlist_manager.getPlaylistCard());

        playlist_manager.getPlaylistManagerPanel().setBounds(10, 50, 200, 300);//posição e tamanho do panel das playlists
        add(playlist_manager.getPlaylistManagerPanel());



        //TOCAR MUSICA E GRIDLAYOUT
        JButton play_button = new JButton(  getIcon("/icons/48_circle_play_icon.png", 48, 48)  );
        play_button.setBorder( BorderFactory.createEmptyBorder() );
        play_button.setContentAreaFilled(false);
        play_button.setFocusPainted(false);



        play_button.addMouseListener(new MouseAdapter() {//inflate effect
            @Override
            public void mouseEntered(MouseEvent e) {
                updateIcon(53);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                updateIcon(48);
            }

            private void updateIcon(int size) {
                String iconName = music.isPlaying() ? "/icons/48_circle_pause_icon.png" : "/icons/48_circle_play_icon.png";
                play_button.setIcon( getIcon(iconName, size, size) );
            }
        });

        play_button.addActionListener(e -> {
            if(music.isPlaying()){//pause
                pause = true;
                music.stop();
            }
            else {//play
                String selectedPlaylistName = mainList.getSelectedValue();
                Playlist selectedPlaylist = playlists.get(selectedPlaylistName);

                try {
                    if(selectedPlaylist == null)
                        throw new musicException("Select a playlist first!", "Null Playlist");
                    else {
                        pause = false;
                        String filePath = selectedPlaylist.getMp3List().getSelectedValue();
                        System.out.println(filePath);

                        music.setMusic(filePath);
                        musicThread = new Thread(music);
                        musicThread.start();
                    }
                } catch(musicException ex){
                    ex.showMessage();
                }
            }
        });



        JButton previous_button = new JButton(  getIcon("/icons/48_music_next_player_icon.png", 48, 48)  );
        previous_button.setBorder(new EmptyBorder(0, 0, 0, 0));
        previous_button.setContentAreaFilled(false);
        previous_button.setFocusPainted(false);

        previous_button.addMouseListener( new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e){
                updateIcon(52);
            }

            public void mouseExited(java.awt.event.MouseEvent e){
                updateIcon(48);
            }

            public void mousePressed(java.awt.event.MouseEvent e){
                updateIcon(43);
            }

            public void mouseReleased(java.awt.event.MouseEvent e){
                updateIcon(52);
            }

            private void updateIcon(int size) {
                previous_button.setIcon( getIcon("/icons/48_music_next_player_icon.png", size, size) );
            }
        });

        previous_button.addActionListener(e -> {
            pause = true;
            music.stop();

            String selectedPlaylistName = mainList.getSelectedValue();
            Playlist selectedPlaylist = playlists.get(selectedPlaylistName);

            try {
                if (selectedPlaylist == null)
                    throw new musicException("Select a playlist first!", "Null Playlist");
                else {
                    int previousIndex = selectedPlaylist.getMp3List().getSelectedIndex() - 1;

                    if (previousIndex >= 0) {

                        String filePath = selectedPlaylist.getMp3List().getModel().getElementAt(previousIndex);//música anterior
                        System.out.println(filePath);

                        selectedPlaylist.getMp3List().setSelectedIndex(previousIndex);//muda na JList para a música anterior

                        pause = false;
                        music.setMusic(filePath);
                        musicThread = new Thread(music);
                        musicThread.start();
                    }
                    else {
                        System.out.println("Você já está na primeira música.");
                        String filePath = selectedPlaylist.getMp3List().getSelectedValue();

                        pause = false;
                        music.setMusic(filePath);
                        musicThread = new Thread(music);
                        musicThread.start();
                    }
                }
            } catch(musicException ex){
                ex.showMessage();
            }
        });



        JButton next_button = new JButton(  getIcon("/icons/o48_music_next_player_icon.png", 48, 48)  );
        next_button.setBorder(new EmptyBorder(0, 0, 0, 0));
        next_button.setContentAreaFilled(false);
        next_button.setFocusPainted(false);

        next_button.addMouseListener( new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e){
                updateIcon(52);
            }

            public void mouseExited(java.awt.event.MouseEvent e){
                updateIcon(48);
            }

            public void mousePressed(java.awt.event.MouseEvent e){
                updateIcon(43);
            }

            public void mouseReleased(java.awt.event.MouseEvent e){
                updateIcon(52);
            }

            private void updateIcon(int size) {
                next_button.setIcon( getIcon("/icons/o48_music_next_player_icon.png", size, size) );
            }
        });

        next_button.addActionListener( e -> {
            pause = true;
            music.stop();

            String selectedPlaylistName = mainList.getSelectedValue();
            Playlist selectedPlaylist = playlists.get(selectedPlaylistName);

            try {
                if ( selectedPlaylist == null )
                    throw new musicException("Select a playlist first!", "Null Playlist");
                else {
                    int nextIndex = selectedPlaylist.getMp3List().getSelectedIndex() + 1;

                    if (nextIndex < selectedPlaylist.getMp3List().getModel().getSize()) {

                        String filePath = selectedPlaylist.getMp3List().getModel().getElementAt(nextIndex);//próxima música
                        System.out.println(filePath);

                        selectedPlaylist.getMp3List().setSelectedIndex(nextIndex);//muda na JList para a próxima música

                        pause = false;
                        music.setMusic(filePath);
                        musicThread = new Thread(music);
                        musicThread.start();
                    }
                    else {
                        System.out.println("Não há mais músicas na lista.");
                        String filePath = selectedPlaylist.getMp3List().getSelectedValue();

                        music.setMusic(filePath);
                        musicThread = new Thread(music);
                        musicThread.start();
                    }
                }
            } catch(musicException ex){
                ex.showMessage();
            }
        });



        music.addChangeListener(evt -> {//play, pause and next
            if(music.isPlaying()){
                play_button.setIcon( getIcon("/icons/48_circle_pause_icon.png", 48, 48) );
            }
            else{
                play_button.setIcon( getIcon("/icons/48_circle_play_icon.png", 48, 48) );

                if(!pause ) {
                    String selectedPlaylistName = mainList.getSelectedValue();
                    Playlist selectedPlaylist = playlists.get(selectedPlaylistName);

                    if(selectedPlaylist != null){
                        if( (selectedPlaylist.getMp3List().getModel().getSize() - 1) > selectedPlaylist.getMp3List().getSelectedIndex() )
                            next_button.doClick();
                    }
                }

            }
        });



        GridLayout gridLayout = new GridLayout(1, 3);
        gridLayout.setHgap(10);
        Panel control_panel = new Panel( gridLayout );
        control_panel.add(previous_button);
        control_panel.add(play_button);
        control_panel.add(next_button);
        control_panel.setBounds(300, 360, 200, 90);
        add(control_panel);



        /*JLabel label = new JLabel();
        label.setIcon( getIcon("/icons/Design sem nome.png", 400, 90) );
        label.setBounds(50, 300, 400, 90);
        add(label);*/



        //DETALHES DO FRAME
        setLayout(null);
        setTitle("Harmonic Apolo");
        setPreferredSize(new Dimension(854, 480));
        getContentPane().setBackground(new Color( 129, 13, 175));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();//empacota/organiza
        setLocationRelativeTo(null);//centraliza
        setVisible(true);
    }


    public ImageIcon getIcon(String pathIcon, int width, int height){
        URL imageUrl = getClass().getResource(pathIcon);
        ImageIcon icon = new ImageIcon(imageUrl);
        Image new_image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return new ImageIcon( new_image );
    }


    public static void main(String[] args) {
        new Apolo();
    }


}