package com.apolo;

import java.awt.Color;
import java.awt.Image;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Apolo extends JFrame{

    private PlaylistManager playlistManager;
    private Map<String, Playlist> playlists;//stores Playlist instances
    private JPanel playlistPanel;
    private JList<String> mainList;
    private Playlist playlist;
    private CardLayout cardLayout;

    private JProgressBar progressBar = new JProgressBar(0, 100);
    private JLabel durationLabel = new JLabel( "00:00" );
    private JLabel progressLabel = new JLabel( "00:00" );

    private PlaybackManager music = new PlaybackManager( progressBar, progressLabel );
    private Thread musicThread = new Thread(music);

    private boolean pause = true;
    private String music_path;

    private enum RepeatState { INACTIVE, REPEAT, REPEAT_ONCE }
    private RepeatState currentRepeatState = RepeatState.INACTIVE;
    private JButton repeatButton;
    private int counterRepeatOnce = 0;

    private ImageIcon addIcon = getIcon("/icons/392_4-more-white.png", 17, 17);
    private ImageIcon delIcon = getIcon("/icons/rectangle-632-180.png", 17, 7);


    private class userImports{
        private boolean noPlaylistsByte;

        public PlaylistManager getPlaylists() {
            PlaylistManager playlist_manager = new PlaylistManager();
            playlist_manager.loadFile();
            if (playlist_manager.getManager() != null) {
                playlist_manager = playlist_manager.getManager();
            }
            else{
                noPlaylistsByte = true;
            }
            return playlist_manager;
        }


        public void getMusicDirectory() {
            if (noPlaylistsByte) {
                File musicDirectoryPT = new File(System.getProperty("user.home") + "/Músicas");
                File musicDirectory = new File(System.getProperty("user.home") + "/Music");

                if (musicDirectory.exists() || musicDirectoryPT.exists()) {
                    //all files in the music folder
                    File[] files = musicDirectory.listFiles();

                    //store the .mp3 files
                    List<File> mp3Files = new ArrayList<>();

                    if (files != null) {
                        for (File file : files) {
                            //checks if it is an .mp3 file
                            if (file.isFile() && file.getName().toLowerCase().endsWith(".mp3")) {
                                mp3Files.add(file);
                            }
                        }
                    }

                    if (!mp3Files.isEmpty()) {
                        Playlist playlist = new Playlist("Music");
                        playlists.put("Music", playlist);
                        mainList.setListData(playlists.keySet().toArray(new String[0]));
                        playlistPanel.add(playlist.getPlaylist(), "Music");

                        for (File mp3File : mp3Files) {
                            playlist.getListModel().addElement(mp3File.getAbsolutePath());
                        }
                    } else {
                        System.out.println("No .mp3 files found in music folder.");
                    }

                } else {
                    System.out.println("Default music folder not found.");
                }
            }

        }
    }

    public Apolo(){

        userImports userImports = new userImports();
        playlistManager = userImports.getPlaylists();

        mainList = playlistManager.getMainList();
        playlists = playlistManager.getMap();
        cardLayout = playlistManager.getCardLayout();
        playlistPanel = playlistManager.getPlaylistPanel();

        userImports.getMusicDirectory();

        String firstPlaylistName = mainList.getModel().getElementAt(0);
        playlist = playlists.get(firstPlaylistName);
        if(playlist != null){
            mainList.setSelectedIndex(0);//if the first playlist is different from null, it is selected
            if( playlist.getListModel().getSize() > 0 )
                playlist.getMp3List().setSelectedIndex(0);//if the first playlist has at least one song, the first song is selected
        }



        //ADD OR DELETE MUSIC
        JButton addMusicButton = new JButton(addIcon);
        addMusicButton.setBackground(Color.BLACK);
        addMusicButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        addMusicButton.setBounds(758, 22, 20, 20);
        addMusicButton.setFocusPainted(false);
        add(addMusicButton);

        addMusicButton.addActionListener(e -> {
            try {
                String selectedPlaylistName = mainList.getSelectedValue();

                if (selectedPlaylistName == null) {
                    throw new musicException("Select a playlist before adding a song!", "Null Playlist");
                } else {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setMultiSelectionEnabled(true);
                    fileChooser.setDialogTitle("Specify files to add (press Ctrl for multi-selection)");
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 Files", "mp3");
                    fileChooser.setFileFilter(filter);

                    int result = fileChooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File[] selectedFiles = fileChooser.getSelectedFiles();
                        playlist = playlists.get(selectedPlaylistName);

                        for (File selectedFile : selectedFiles) {
                            playlist.getListModel().addElement( selectedFile.getAbsolutePath() );
                        }

                        playlistManager.saveToFile(playlistManager);
                    }
                }

            } catch (musicException ex) {
                ex.showMessage();
            }
        });


        JButton delMusicButton = new JButton(delIcon);
        delMusicButton.setBounds(324, 27, 20, 10);
        delMusicButton.setBackground(Color.BLACK);
        delMusicButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        delMusicButton.setFocusPainted(false);
        add(delMusicButton);

        delMusicButton.addActionListener(e -> {
            String selectedPlaylistName = mainList.getSelectedValue();

            try {
                if (selectedPlaylistName == null) {
                    throw new musicException("Select a playlist before deleting a song!", "Null Playlist");
                }

                playlist = playlists.get(selectedPlaylistName);
                int selectedMusicPathIndex = playlist.getMp3List().getSelectedIndex();
                if( selectedMusicPathIndex == -1 ){
                    throw new musicException("Select a song before deleting it!", "Null Music");
                }

                int confirmSongDel = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected music?", "Confirm Song Deletion", JOptionPane.YES_NO_OPTION);
                if (confirmSongDel == JOptionPane.YES_OPTION) {
                    playlist.getListModel().remove(selectedMusicPathIndex);
                    playlistManager.saveToFile(playlistManager);
                }
            }
            catch (musicException ex){
                ex.showMessage();
            }
        });



        playlistPanel.setBounds(286, 20, 532, 310);//position and size of the panel with the songs
        add(playlistPanel);



        //CREATE OR DELETE PLAYLIST
        JButton createPlaylistButton = new JButton(addIcon);
        createPlaylistButton.setBackground(Color.BLACK);
        createPlaylistButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        createPlaylistButton.setBounds(218, 22, 20, 20);
        createPlaylistButton.setFocusPainted(false);
        add(createPlaylistButton);

        createPlaylistButton.addActionListener(e -> {
            playlistManager.createPlaylist();

            if(mainList.getModel().getSize() == 1) {
                playlistPanel.revalidate();
                addMusicButton.repaint();
                delMusicButton.repaint();
            }

        });


        JButton deletePlaylistButton = new JButton(delIcon);
        deletePlaylistButton.setBounds(47, 27, 20, 10);
        deletePlaylistButton.setBackground(Color.BLACK);
        deletePlaylistButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        deletePlaylistButton.setFocusPainted(false);
        add(deletePlaylistButton);

        deletePlaylistButton.addActionListener(e -> {
            try {
                playlistManager.deletePlaylist(playlistManager);
            }
            catch (musicException ex){
                ex.showMessage();
            }
        });


        playlistManager.getMainListPanel().setBounds(20, 20, 245, 400);//Position and size of the panel with playlists
        add(playlistManager.getMainListPanel());



        //listener to switch between playlists
        mainList.addListSelectionListener(e -> {
            String selectedPlaylist = mainList.getSelectedValue();
            cardLayout.show(playlistPanel, selectedPlaylist);
            addMusicButton.repaint();
            delMusicButton.repaint();
        });



        //PLAY MUSIC AND PLAYBACK-CONTROL
        JButton play_button = new JButton(  getIcon("/icons/48_circle_play_icon.png", 43, 43)  );
        play_button.setBorder( BorderFactory.createEmptyBorder() );
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
                music.pausePlayblack();
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

                        if(file_path == null)
                            throw new musicException("Select a song first!", "Null Music");

                        if( file_path.equals(music_path) ) {//resume or repeat without printPath
                            music.setMusic(music_path);

                            musicThread = new Thread(music);
                            musicThread.start();
                        }
                        else{//new play
                            System.out.println(file_path);
                            music_path = file_path;

                            music.resetPlayback();
                            music.setMusic(music_path);
                            durationLabel.setText( music.getFormatDuration() );

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
            music.pausePlayblack();

            String selectedPlaylistName = mainList.getSelectedValue();
            Playlist selectedPlaylist = playlists.get(selectedPlaylistName);

            try {
                if (selectedPlaylist == null)
                    throw new musicException("Select a playlist first!", "Null Playlist");
                else {
                    int previousIndex = selectedPlaylist.getMp3List().getSelectedIndex() - 1;

                    if (previousIndex >= 0) {

                        String file_path = selectedPlaylist.getMp3List().getModel().getElementAt(previousIndex);//previous song
                        System.out.println(file_path);

                            selectedPlaylist.getMp3List().setSelectedIndex(previousIndex);//changes the JList to the previous song

                        music_path = file_path;
                        music.resetPlayback();
                        pause = false;

                        music.setMusic(file_path);
                        durationLabel.setText( music.getFormatDuration() );

                        musicThread = new Thread(music);
                        musicThread.start();
                    }
                    else {
                        System.out.println("You're already on the first song.");
                        String file_path = selectedPlaylist.getMp3List().getSelectedValue();

                        music.resetPlayback();
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
            music.pausePlayblack();

            String selectedPlaylistName = mainList.getSelectedValue();
            Playlist selectedPlaylist = playlists.get(selectedPlaylistName);

            try {
                if ( selectedPlaylist == null )
                    throw new musicException("Select a playlist first!", "Null Playlist");
                else {
                    int nextIndex = selectedPlaylist.getMp3List().getSelectedIndex() + 1;

                    if (nextIndex < selectedPlaylist.getMp3List().getModel().getSize()) {

                        String file_path = selectedPlaylist.getMp3List().getModel().getElementAt(nextIndex);//next song
                        System.out.println(file_path);

                        selectedPlaylist.getMp3List().setSelectedIndex(nextIndex);//changes the JList to the next song

                        music_path = file_path;
                        music.resetPlayback();
                        pause = false;

                        music.setMusic(file_path);
                        durationLabel.setText( music.getFormatDuration() );

                        musicThread = new Thread(music);
                        musicThread.start();
                    }
                    else {
                        System.out.println("There are no more songs in the playlist.");
                        String file_path = selectedPlaylist.getMp3List().getSelectedValue();

                        music.resetPlayback();
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



        music.addChangeListener(evt -> {//play, pause (icons) and play in sequence or with repeat
            if(music.isPlaying()){
                play_button.setIcon( getIcon("/icons/48_circle_pause_icon.png", 43, 43) );
            }
            else{
                play_button.setIcon( getIcon("/icons/48_circle_play_icon.png", 43, 43) );

                if(!pause) {
                    String selectedPlaylistName = mainList.getSelectedValue();
                    Playlist selectedPlaylist = playlists.get(selectedPlaylistName);

                    if(selectedPlaylist != null){

                        //checks if the index of the selected one is less than that of the last one.
                        if( (selectedPlaylist.getMp3List().getModel().getSize() - 1) > selectedPlaylist.getMp3List().getSelectedIndex() && currentRepeatState == RepeatState.INACTIVE )
                            next_button.doClick();

                        else if( currentRepeatState == RepeatState.REPEAT ) {
                            play_button.doClick();
                        }

                        else if( currentRepeatState == RepeatState.REPEAT_ONCE ){
                            if(counterRepeatOnce < 1){
                                play_button.doClick();
                                ++counterRepeatOnce;
                            }
                            else{
                                if ( (selectedPlaylist.getMp3List().getModel().getSize() - 1) > selectedPlaylist.getMp3List().getSelectedIndex() ) {
                                    next_button.doClick();
                                }
                                counterRepeatOnce = 0;
                            }
                        }

                    }
                }

            }
        });



        repeatButton = new JButton( getIcon("/icons/repeat-song-512.png", 23, 23) );
        repeatButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        repeatButton.setFocusPainted(false);
        repeatButton.setContentAreaFilled(false);
        repeatButton.setBackground(new Color(64, 64, 64));
        add(repeatButton);

        repeatButton.addActionListener(e -> {
            toggleRepeatState();
        });


        JPanel playbackPanel = new JPanel(null);
        playbackPanel.setBackground( new Color( 40, 40, 40) );
        playbackPanel.setBounds(286, 335, 532, 85);
        add(playbackPanel);


        GridLayout gridLayout = new GridLayout(1, 4);
        gridLayout.setHgap(10);
        Panel playbackControl = new Panel( gridLayout );
        playbackControl.setBackground(new Color(64, 64, 64));
        playbackControl.add(previous_button);
        playbackControl.add(play_button);
        playbackControl.add(next_button);
        playbackControl.add(repeatButton);
        playbackControl.setBounds(182, 27, 230, 48);
        playbackPanel.add(playbackControl);

        JLabel playbackControlBackground = new JLabel( getIcon("/icons/playback-control.png", 400, 70) );
        playbackControlBackground.setBounds(67, 16, 400, 70);
        playbackPanel.add(playbackControlBackground);


        progressBar.setBounds(40, 4, 450, 5);
        progressBar.setForeground( new Color(129, 13, 175) );
        progressBar.setBackground(Color.WHITE);
        progressBar.setStringPainted(false);
        playbackPanel.add( progressBar );

        durationLabel.setBounds(500, 1, 40, 10);
        durationLabel.setForeground(Color.WHITE);
        playbackPanel.add( durationLabel );

        progressLabel.setBounds( 0, 1, 40, 10 );
        progressLabel.setForeground(Color.WHITE);
        playbackPanel.add( progressLabel );


        //FRAME DETAILS
        setLayout(null);
        setTitle("Harmonic Apolo");
        setPreferredSize(new Dimension(854, 480));
        getContentPane().setBackground( new Color( 40, 40, 40) );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();//organize/scale
        setLocationRelativeTo(null);//centralize
        setVisible(true);
    }


    public ImageIcon getIcon(String pathIcon, int width, int height){
        URL imageUrl = getClass().getResource(pathIcon);
        //internal uses URL to instantiate ImageIcon which is transformed into Image for resizing. External uses Image to instantiate ImageIcon.
        return new ImageIcon( new ImageIcon(imageUrl).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH) );
    }


    private void toggleRepeatState() {
        switch (currentRepeatState) {
            case INACTIVE:
                currentRepeatState = RepeatState.REPEAT;
                repeatButton.setIcon( getIcon("/icons/repeat-song-1-512.png", 23, 23) );
                break;

            case REPEAT:
                currentRepeatState = RepeatState.REPEAT_ONCE;
                repeatButton.setIcon( getIcon("/icons/repeat-song-once-1-512.png", 23, 23) );
                break;

            case REPEAT_ONCE:
                currentRepeatState = RepeatState.INACTIVE;
                repeatButton.setIcon( getIcon("/icons/repeat-song-512.png", 23, 23) );
                break;
        }
    }


    public static void main(String[] args) {
        new Apolo();
    }

}