package com.apolo;

import java.awt.*;

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
    private Map<String, Playlist> playlists;//armazena instâncias de Playlist
    private JList<String> mainList;
    private Playlist playlist;
    private CardLayout cardLayout;
    private Play music = new Play();
    private boolean pause = true;
    private String music_path;

    private enum RepeatState { INACTIVE, REPEAT, REPEAT_ONCE }
    private RepeatState current_repeat_state = RepeatState.INACTIVE;
    private JButton repeat_button;
    private int counter_repeat_once = 0;

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
        cardLayout = playlist_manager.getPlaylistsCardLayout();



        //abrir ou deletar musica
        JButton addMusicButton = new JButton(addMusicIcon);
        addMusicButton.setBackground(Color.BLACK);
        addMusicButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        addMusicButton.setBounds(760, 22, 20, 20);
        addMusicButton.setFocusPainted(false);
        add(addMusicButton);

        addMusicButton.addActionListener(e -> {

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

        });


        JButton delMusicButton = new JButton(deleteIcon);
        delMusicButton.setBounds(330, 27, 20, 10);// posição e tamanho
        delMusicButton.setBackground(Color.BLACK);
        delMusicButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        delMusicButton.setFocusPainted(false);
        add(delMusicButton);

        delMusicButton.addActionListener(e -> {
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



        //adiciona a lista principal e as playlists (card) ao JFrame
        playlist_manager.getPlaylistCard().setBounds(295, 20, 520, 310);//posição e tamanho do card de uma playlist
        add(playlist_manager.getPlaylistCard());



        //CRIAR OU DELETAR PLAYLIST (deve ser analisado o salvamento)
        JButton createPlaylistButton = new JButton(addMusicIcon);
        createPlaylistButton.setBackground(Color.BLACK);
        createPlaylistButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        createPlaylistButton.setBounds(225, 22, 20, 20);
        createPlaylistButton.setFocusPainted(false);
        add(createPlaylistButton);

        createPlaylistButton.addActionListener(e -> {
            playlist = playlist_manager.creatPlaylist();

            if(mainList.getModel().getSize() == 1) {
                playlist_manager.getPlaylistCard().revalidate();
                playlist_manager.getPlaylistCard().repaint();
                addMusicButton.repaint();
                delMusicButton.repaint();
            }

        });


        JButton delete_playlist_button = new JButton(deleteIcon);
        delete_playlist_button.setBounds(45, 27, 20, 10);
        delete_playlist_button.setBackground(Color.BLACK);
        delete_playlist_button.setBorder(new EmptyBorder(0, 0, 0, 0));
        delete_playlist_button.setFocusPainted(false);
        add(delete_playlist_button);

        delete_playlist_button.addActionListener(e -> {
            try {
                playlist_manager.deletePlaylist(playlist_manager);
            }
            catch (musicException ex){
                ex.showMessage();
            }
        });



        playlist_manager.getPlaylistManagerPanel().setBounds(20, 20, 250, 400);//posição e tamanho do panel com as playlists
        add(playlist_manager.getPlaylistManagerPanel());



        //adicione um ouvinte para alternar entre playlists
        mainList.addListSelectionListener(e -> {
            String selectedPlaylist = mainList.getSelectedValue();
            cardLayout.show(playlist_manager.getPlaylistCard(), selectedPlaylist);
            addMusicButton.repaint();
            delMusicButton.repaint();
        });



        //TOCAR MUSICA E CONTROL-PLAYBACK
        JButton play_button = new JButton(  getIcon("/icons/48_circle_play_icon.png", 43, 43)  );
        play_button.setBorder( BorderFactory.createEmptyBorder() );
        play_button.setBounds(525, 360, 50, 50);
        play_button.setBackground(new Color(64, 64, 64));
        play_button.setContentAreaFilled(false);
        play_button.setFocusPainted(false);
        add(play_button);

        play_button.addMouseListener(new MouseAdapter() {//inflate effect
            @Override
            public void mouseEntered(MouseEvent e) {
                updateIcon(48);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                updateIcon(43);
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
                        String file_path = selectedPlaylist.getMp3List().getSelectedValue();

                        if( file_path.equals(music_path) ) {//resume or repeat without printPath
                            music.setMusic(music_path);
                            musicThread = new Thread(music);
                            musicThread.start();
                        }
                        else{//new play
                            System.out.println(file_path);
                            music_path = file_path;
                            music.setFrame();
                            music.setMusic(music_path);
                            musicThread = new Thread(music);
                            musicThread.start();
                        }

                    }
                } catch(musicException ex){
                    ex.showMessage();
                }
            }
        });



        JButton previous_button = new JButton(  getIcon("/icons/48_music_next_player_icon.png", 38, 38)  );
        previous_button.setBounds(465, 365, 43, 43);
        previous_button.setBorder(new EmptyBorder(0, 0, 0, 0));
        previous_button.setContentAreaFilled(false);
        previous_button.setBackground(new Color(64, 64, 64));
        previous_button.setFocusPainted(false);
        add(previous_button);

        previous_button.addMouseListener( new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e){
                updateIcon(43);
            }

            public void mouseExited(java.awt.event.MouseEvent e){
                updateIcon(38);
            }

            public void mousePressed(java.awt.event.MouseEvent e){
                updateIcon(33);
            }

            public void mouseReleased(java.awt.event.MouseEvent e){
                updateIcon(43);
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

                        String file_path = selectedPlaylist.getMp3List().getModel().getElementAt(previousIndex);//música anterior
                        System.out.println(file_path);

                        selectedPlaylist.getMp3List().setSelectedIndex(previousIndex);//muda na JList para a música anterior

                        music_path = file_path;
                        music.setFrame();
                        pause = false;
                        music.setMusic(file_path);
                        musicThread = new Thread(music);
                        musicThread.start();
                    }
                    else {
                        System.out.println("Você já está na primeira música.");
                        String file_path = selectedPlaylist.getMp3List().getSelectedValue();

                        music.setFrame();
                        pause = false;
                        music.setMusic(file_path);
                        musicThread = new Thread(music);
                        musicThread.start();
                    }
                }
            } catch(musicException ex){
                ex.showMessage();
            }
        });



        JButton next_button = new JButton(  getIcon("/icons/o48_music_next_player_icon.png", 38, 38)  );
        next_button.setBounds(595, 365, 43, 43);
        next_button.setBorder(new EmptyBorder(0, 0, 0, 0));
        next_button.setContentAreaFilled(false);
        next_button.setBackground(new Color(64, 64, 64));
        next_button.setFocusPainted(false);
        add(next_button);

        next_button.addMouseListener( new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e){
                updateIcon(43);
            }

            public void mouseExited(java.awt.event.MouseEvent e){
                updateIcon(38);
            }

            public void mousePressed(java.awt.event.MouseEvent e){
                updateIcon(33);
            }

            public void mouseReleased(java.awt.event.MouseEvent e){
                updateIcon(43);
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

                        String file_path = selectedPlaylist.getMp3List().getModel().getElementAt(nextIndex);//próxima música
                        System.out.println(file_path);

                        selectedPlaylist.getMp3List().setSelectedIndex(nextIndex);//muda na JList para a próxima música

                        music_path = file_path;
                        music.setFrame();
                        pause = false;
                        music.setMusic(file_path);
                        musicThread = new Thread(music);
                        musicThread.start();
                    }
                    else {
                        System.out.println("Não há mais músicas na lista.");
                        String file_path = selectedPlaylist.getMp3List().getSelectedValue();

                        music.setFrame();
                        pause = false;
                        music.setMusic(file_path);
                        musicThread = new Thread(music);
                        musicThread.start();
                    }
                }
            } catch(musicException ex){
                ex.showMessage();
            }
        });



        repeat_button = new JButton( getIcon("/icons/repeat-song-512.png", 23, 23) );
        repeat_button.setBounds(660, 373, 25, 25);
        repeat_button.setBorder(new EmptyBorder(0, 0, 0, 0));
        repeat_button.setFocusPainted(false);
        repeat_button.setContentAreaFilled(false);
        repeat_button.setBackground(new Color(64, 64, 64));
        add(repeat_button);

        repeat_button.addActionListener(e -> {
            toggleRepeatState();
            updateButtonIcon();
        });



        music.addChangeListener(evt -> {//play, pause (icons) and play in sequence or with repeat
            if(music.isPlaying()){
                play_button.setIcon( getIcon("/icons/48_circle_pause_icon.png", 43, 43) );
            }
            else{
                play_button.setIcon( getIcon("/icons/48_circle_play_icon.png", 43, 43) );

                if(!pause ) {
                    String selectedPlaylistName = mainList.getSelectedValue();
                    Playlist selectedPlaylist = playlists.get(selectedPlaylistName);

                    if(selectedPlaylist != null){
                        if( (selectedPlaylist.getMp3List().getModel().getSize() - 1) > selectedPlaylist.getMp3List().getSelectedIndex() && current_repeat_state == RepeatState.INACTIVE )
                            next_button.doClick();

                        else if( current_repeat_state == RepeatState.REPEAT ) {
                            music.setFrame();
                            play_button.doClick();
                        }

                        else if( current_repeat_state == RepeatState.REPEAT_ONCE ){
                            music.setFrame();
                            if(counter_repeat_once < 1){//repeat once
                                play_button.doClick();
                                ++counter_repeat_once;
                            }
                            else{
                                if ( (selectedPlaylist.getMp3List().getModel().getSize() - 1) > selectedPlaylist.getMp3List().getSelectedIndex() ) {//!(last song)
                                    next_button.doClick();
                                }
                                counter_repeat_once = 0;
                            }
                        }

                    }
                }

            }
        });



        /*GridLayout gridLayout = new GridLayout(1, 4);
        gridLayout.setHgap(10);
        Panel playback_control = new Panel( gridLayout );
        playback_control.add(previous_button);
        playback_control.add(play_button);
        playback_control.add(next_button);
        playback_control.add(repeat_button);
        playback_control.setBounds(300, 370, 250, 55);
        add(playback_control);*/



        JLabel label = new JLabel();
        label.setIcon( getIcon("/icons/playback-control.png", 400, 70) );
        label.setBounds(350, 350, 400, 70);
        add(label);



        //DETALHES DO FRAME
        setLayout(null);
        setTitle("Harmonic Apolo");
        setPreferredSize(new Dimension(854, 480));
        getContentPane().setBackground( new Color( 40, 40, 40) );
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


    private void toggleRepeatState() {
        switch (current_repeat_state) {
            case INACTIVE:
                current_repeat_state = RepeatState.REPEAT;
                break;

            case REPEAT:
                current_repeat_state = RepeatState.REPEAT_ONCE;
                break;

            case REPEAT_ONCE:
                current_repeat_state = RepeatState.INACTIVE;
                break;
        }
    }

    private void updateButtonIcon() {
        ImageIcon icon;
        switch (current_repeat_state) {
            case INACTIVE:
                icon = getIcon("/icons/repeat-song-512.png", 23, 23);
                break;

            case REPEAT:
                icon = getIcon("/icons/repeat-song-1-512.png", 23, 23);
                break;

            case REPEAT_ONCE:
                icon = getIcon("/icons/repeat-song-once-1-512.png", 23, 23);
                break;

            default:
                icon = getIcon("/icons/repeat-song-512.png", 23, 23);
                break;
        }
        repeat_button.setIcon(icon);
    }


    public static void main(String[] args) {
        new Apolo();
    }


}