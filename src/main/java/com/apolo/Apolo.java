package com.apolo;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.net.URL;
import java.util.Map;
import java.io.File;


public class Apolo extends JFrame{

    Thread musicThread;
    private Map<String, Playlist> playlists;  //armazena instâncias de Playlist
    private JList<String> mainList;
    private Playlist playlist;


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



        //MENUBAR
        JMenuBar mb = new JMenuBar();
        mb.setBackground(new Color (33, 41, 48));
        setJMenuBar(mb);
        mb.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        //menu para adicionar path mp3 à JList
        JMenu file_menu = new JMenu("File");
        file_menu.setForeground(Color.WHITE);
        file_menu.setFont(new Font("Arial", Font.PLAIN, 14));

        JMenuItem open = new JMenuItem("Open");
        open.setForeground(Color.WHITE);
        open.setFont(new Font("Arial", Font.PLAIN, 14));
        open.setBackground(new Color(33, 41, 48));//cor do item suspenso
        file_menu.add(open);

        //cor da letra file quando fica em cima
        file_menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                file_menu.setForeground(new Color(129, 13, 175));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                file_menu.setForeground(Color.WHITE);
            }
        });

        //configurar a largura
        Dimension menuSize = new Dimension(50, 25);
        file_menu.setPreferredSize(menuSize);
        open.setPreferredSize(menuSize);

        mb.add(file_menu);



        //CRIAR OU DELETAR PLAYLIST
        URL imageUrl = getClass().getResource("/icons/plus_circled_icon.png");
        ImageIcon icon = new ImageIcon(imageUrl);
        Image new_image = icon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);

        JButton createPlaylistButton = new JButton(new ImageIcon(new_image));
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



        //adicione um ouvinte para alternar entre playlists
        playlist_manager.switchPlaylist();



        //adicione a lista principal e as playlists (card) ao JFrame
        playlist_manager.getPlaylistCard().setBounds(250, 50, 500, 300);//posição e tamanho do card de uma playlist
        add(playlist_manager.getPlaylistCard());

        playlist_manager.getPlaylistManagerPanel().setBounds(10, 50, 200, 300);//posição e tamanho do panel das playlists
        add(playlist_manager.getPlaylistManagerPanel());



        //abrir ou deletar musica
        open.addActionListener(new ActionListener() {
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




        //TOCAR MUSICA
        JButton play_button = new JButton("Play");
        play_button.setBounds(250, 370, 80, 20);//posição e tamanho
        add(play_button);

        play_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedPlaylistName = mainList.getSelectedValue();

                if (selectedPlaylistName != null) {
                    Playlist selectedPlaylist = playlists.get(selectedPlaylistName);

                    if (selectedPlaylist != null && (musicThread == null || !musicThread.isAlive())) {
                        String filePath = selectedPlaylist.getMp3List().getSelectedValue();
                        System.out.println(filePath);

                        Play music = new Play(filePath);
                        musicThread = new Thread(music);
                        musicThread.start();
                    }
                }
            }
        });


        /*JButton pause_button = new JButton("parar musica");
        pause_button.setBounds(400, 200, 100, 30);//posição e tamanho
        add(pause_button);

        pause_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                music.pausePlayback();
            }

        });


        JButton resume_button = new JButton("prosseguir musica");
        resume_button.setBounds(285, 100, 100, 30);//posição e tamanho
        add(resume_button);

        resume_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                music.resumePlayback(1000);
            }

        });*/




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


    public static void main(String[] args) {
        new Apolo();
    }


}