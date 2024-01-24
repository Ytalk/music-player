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

import javazoom.jl.player.advanced.PlaybackListener;


public class Apolo extends JFrame{

    Thread musicThread = new Thread();
    private Map<String, Playlist> playlists;  //armazena instâncias de Playlist
    private JList<String> mainList;
    private Playlist playlist;
    Play music = new Play();

    public class test{

        public PlaylistManager teste() {
            PlaylistManager playlist_manager = new PlaylistManager();
            playlist_manager.loadFile();
            if (playlist_manager.getManager() != null) {
                playlist_manager = playlist_manager.getManager();
            }

            return playlist_manager;
        }

    }

    public Apolo(){

        PlaylistManager playlist_manager = new test().teste();

        mainList = playlist_manager.getMainList();
        playlists = playlist_manager.getMap();



        //CRIAR OU DELETAR PLAYLIST
        JButton createPlaylistButton = new JButton(  getIcon("/icons/392_4-more-white.png", 17)  );
        createPlaylistButton.setBackground(new Color(33, 41, 48));
        createPlaylistButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        createPlaylistButton.setBounds(170, 51, 22, 22);
        add(createPlaylistButton);

        createPlaylistButton.addActionListener(e -> {
            playlist = playlist_manager.creatPlaylist();
            playlist_manager.saveToFile(playlist_manager);
        });


        JButton delete_playlist_button = new JButton("Delete");
        delete_playlist_button.setBounds(100, 370, 80, 20);// posição e tamanho
        add(delete_playlist_button);

        delete_playlist_button.addActionListener(e -> {
            playlist_manager.deletePlaylist();
            playlist_manager.saveToFile(playlist_manager);
        });



        //abrir ou deletar musica
        JButton addMusicButton = new JButton(  getIcon("/icons/392_4-more-white.png", 17)  );//aclopar na playlist
        addMusicButton.setBackground(new Color(33, 41, 48));
        addMusicButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        addMusicButton.setBounds(700, 51, 22, 22);
        add(addMusicButton);

        addMusicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Specify a file to open");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivos MP3", "mp3");
                fileChooser.setFileFilter(filter);

                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String filePath = selectedFile.getAbsolutePath();

                    String selectedPlaylist = mainList.getSelectedValue();

                    addMusic(selectedPlaylist, filePath);

                    playlist_manager.saveToFile(playlist_manager);
                }
            }
        });


        JButton delete_button = new JButton("Remove");
        delete_button.setBounds(675, 370, 80, 20);//posição e tamanho
        add(delete_button);

        delete_button.addActionListener(e -> {
            String selectedPlaylist = mainList.getSelectedValue();
            removeSelectedMusic(selectedPlaylist);
            playlist_manager.saveToFile(playlist_manager);
        });



        //adicione um ouvinte para alternar entre playlists
        playlist_manager.switchPlaylist();



        //adicione a lista principal e as playlists (card) ao JFrame
        playlist_manager.getPlaylistCard().setBounds(250, 50, 500, 300);//posição e tamanho do card de uma playlist
        add(playlist_manager.getPlaylistCard());

        playlist_manager.getPlaylistManagerPanel().setBounds(10, 50, 200, 300);//posição e tamanho do panel das playlists
        add(playlist_manager.getPlaylistManagerPanel());



        //TOCAR MUSICA
        JButton play_button = new JButton(  getIcon("/icons/48_circle_play_icon.png", 48)  );
        play_button.setBounds(360, 360, 58, 58);
        play_button.setBorder( BorderFactory.createEmptyBorder() );
        play_button.setContentAreaFilled(false);
        play_button.setFocusPainted(false);
        add(play_button);

        music.addChangeListener(evt -> {
            if(music.isPlaying()){
                play_button.setIcon( getIcon("/icons/48_circle_pause_icon.png", 48) );
            }
            else{
                play_button.setIcon(getIcon("/icons/48_circle_play_icon.png", 48));
            }
        });

        play_button.addMouseListener(new MouseAdapter() {
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
                play_button.setIcon(getIcon(iconName, size));
            }
        });

        play_button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                if(music.isPlaying()){
                    music.stop();
                }
                else {
                    String selectedPlaylistName = mainList.getSelectedValue();

                    if (selectedPlaylistName != null) {
                        Playlist selectedPlaylist = playlists.get(selectedPlaylistName);

                        if (selectedPlaylist != null && !musicThread.isAlive()) {
                            String filePath = selectedPlaylist.getMp3List().getSelectedValue();
                            System.out.println(filePath);

                            music.setMusic(filePath);
                            musicThread = new Thread(music);
                            musicThread.start();
                        }
                    }

                }

            }
        });



        JButton previous_button = new JButton(  getIcon("/icons/48_music_next_player_icon.png", 48)  );
        previous_button.setBounds(300, 360, 48, 48);
        previous_button.setBorder(new EmptyBorder(0, 0, 0, 0));
        previous_button.setContentAreaFilled(false);
        add(previous_button);

        previous_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                music.stop();
                String selectedPlaylistName = mainList.getSelectedValue();

                if (selectedPlaylistName != null) {
                    Playlist selectedPlaylist = playlists.get(selectedPlaylistName);

                    if ( selectedPlaylist != null && !musicThread.isAlive() ) {
                        int previousIndex = selectedPlaylist.getMp3List().getSelectedIndex() - 1;

                        if (previousIndex >= 0) {
                            //obtém o arquivo da música anterior
                            String filePath = selectedPlaylist.getMp3List().getModel().getElementAt(previousIndex);
                            System.out.println(filePath);

                            //muda a seleção na JList para a música anterior
                            selectedPlaylist.getMp3List().setSelectedIndex(previousIndex);

                            music.setMusic(filePath);
                            musicThread = new Thread(music);
                            musicThread.start();
                        }
                        else {
                            System.out.println("Você já está na primeira música.");
                        }
                    }
                }
            }
        });


        JButton next_button = new JButton(  getIcon("/icons/o48_music_next_player_icon.png", 48)  );
        next_button.setBounds(420, 360, 48, 48);
        next_button.setBorder(new EmptyBorder(0, 0, 0, 0));
        next_button.setContentAreaFilled(false);
        add(next_button);

        next_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                music.stop();
                String selectedPlaylistName = mainList.getSelectedValue();

                if (selectedPlaylistName != null) {
                    Playlist selectedPlaylist = playlists.get(selectedPlaylistName);

                    if ( selectedPlaylist != null && !musicThread.isAlive() ) {
                        int nextIndex = selectedPlaylist.getMp3List().getSelectedIndex() + 1;

                        if ( nextIndex < selectedPlaylist.getMp3List().getModel().getSize() ) {
                            // Obtém o próximo arquivo de música
                            String filePath = selectedPlaylist.getMp3List().getModel().getElementAt(nextIndex);
                            System.out.println(filePath);

                            // Muda a seleção na JList para a próxima música
                            selectedPlaylist.getMp3List().setSelectedIndex(nextIndex);

                            music.setMusic(filePath);
                            musicThread = new Thread(music);
                            musicThread.start();
                        } 
                        else{
                            System.out.println("Não há mais músicas na lista.");
                        }
                    }
                }
            }
        });



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


    public void addMusic(String playlistName, String path) {
        Playlist playlist = playlists.get(playlistName);

        if (playlist != null) {
            playlist.addMusic(path);
        }
    }


    public void removeSelectedMusic(String playlistName) {
        Playlist playlist = playlists.get(playlistName);

        if (playlist != null) {
            playlist.removeSelectedMusic();
        }
    }


    public ImageIcon getIcon(String pathIcon, int size){
        URL imageUrl = getClass().getResource(pathIcon);
        ImageIcon icon = new ImageIcon(imageUrl);
        Image new_image = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);

        return new ImageIcon( new_image );
    }


    public static void main(String[] args) {
        new Apolo();
    }


}
