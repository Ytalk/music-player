package com.apolo;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;

import java.awt.*;
import javax.swing.*;
import java.awt.Color;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.File;


public class Apolo extends JFrame{

    Thread musicThread;
    private Map<String, Playlist> playlists;  //armazena instâncias de Playlist
    private JList<String> mainList;
    private Playlist playlist;


    public Apolo(){

        this.playlists = new HashMap<>();




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




        //CARD
        JPanel playlists_panel = new JPanel();
        CardLayout cardLayout = new CardLayout();
        playlists_panel.setLayout(cardLayout);
        playlists_panel.setBackground(new Color( 129, 13, 175));




        //CRIAR OU DELETAR PLAYLIST
        mainList = new JList<>(new String[0]);
        mainList.setBackground(new Color(64, 64, 64));

        JButton createPlaylistButton = new JButton("Criar playlist");
        createPlaylistButton.setBounds(10, 370, 80, 20);// posição e tamanho
        add(createPlaylistButton);

        createPlaylistButton.addActionListener(e -> {
            String playlistName = JOptionPane.showInputDialog("Digite o nome da nova playlist:");
            if (playlistName != null && !playlistName.isEmpty()) {
                playlist = new Playlist(playlistName);
                playlists.put(playlistName, playlist);

                // Atualiza a mainList com os nomes das playlists existentes
                mainList.setListData(playlists.keySet().toArray(new String[0]));

                // Adiciona a nova playlist ao cardLayout
                playlists_panel.add(playlist.getPlaylist(), playlistName);
            }
        });

        JButton delete_playlist_button = new JButton("Deletar playlist");
        delete_playlist_button.setBounds(100, 370, 80, 20);// posição e tamanho
        add(delete_playlist_button);

        delete_playlist_button.addActionListener(e -> {
                String playlist_name = mainList.getSelectedValue();
                Playlist playlist = playlists.get(playlist_name);

            if (playlist != null) {
                playlists_panel.remove(playlist.getPlaylist());
                playlists.remove(playlist_name);
                mainList.setListData(playlists.keySet().toArray(new String[0]));
            }
        });


        //adicione um ouvinte para alternar entre playlists
        mainList.addListSelectionListener(e -> {
            String selectedPlaylist = mainList.getSelectedValue();
            cardLayout.show(playlists_panel, selectedPlaylist);
        });

        // Adicione a lista principal e as playlists (card) ao JFrame
        playlists_panel.setBounds(250, 50, 500, 300);//posição e tamanho
        add(playlists_panel);

        JLabel mainList_label = new JLabel("Playlists");
        mainList_label.setBounds(0, 0, 500, 30);
        mainList_label.setBackground(new Color(33, 41, 48));
        mainList_label.setHorizontalAlignment(SwingConstants.CENTER);
        mainList_label.setVerticalAlignment(SwingConstants.CENTER);
        mainList_label.setFont(new Font("Arial", Font.BOLD, 20));
        mainList_label.setForeground(Color.WHITE);

        Panel mainList_panel = new Panel(new BorderLayout());
        mainList_panel.setBounds(10, 50, 200, 300);//posição e tamanho
        mainList_panel.add(mainList_label, BorderLayout.NORTH);
        mainList_panel.add(mainList, BorderLayout.CENTER);
        add(mainList_panel);



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
                }
            }
        });

        JButton delete_button = new JButton("del msc");
        delete_button.setBounds(675, 370, 80, 20);//posição e tamanho
        add(delete_button);
        delete_button.addActionListener(e -> {
            String selectedPlaylist = mainList.getSelectedValue();
            removeSelectedMusic(selectedPlaylist);
        });




        //TOCAR MUSICA
        JButton play_button = new JButton("play");
        play_button.setBounds(250, 370, 80, 20);//posição e tamanho
        add(play_button);
        play_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ( (musicThread == null || !musicThread.isAlive()) && (playlist != null)) {
                    String filePath = playlist.getMp3List().getSelectedValue();
                    System.out.println(filePath);
                    Play music = new Play(filePath);
                    musicThread = new Thread(music);
                    musicThread.start();
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
