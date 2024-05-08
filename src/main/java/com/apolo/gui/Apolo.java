package com.apolo.gui;

import com.apolo.model.Playlist;
import com.apolo.model.PlaylistManager;
import com.apolo.model.PlaybackManager;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.net.URL;

import java.awt.Color;
import java.awt.Image;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DnDConstants;
import java.awt.datatransfer.DataFlavor;

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
    private int counterRepeatOnce = 0;
    private JButton repeatButton = formatButton( getIcon("/icons/repeat-song-512.png", 23, 23), 64, false );

    private JButton playButton = formatButton( getIcon("/icons/48_circle_play_icon.png", 43, 43), 64, false );
    private JButton previousButton = formatButton( getIcon("/icons/48_music_next_player_icon.png", 38, 38), 64, false );
    private JButton nextButton = formatButton( getIcon("/icons/o48_music_next_player_icon.png", 38, 38), 64, false );

    private ImageIcon addIcon = getIcon("/icons/392_4-more-white.png", 17, 17);
    private ImageIcon delIcon = getIcon("/icons/rectangle-632-180.png", 17, 7);

    private JButton addMusicButton = formatButton(addIcon, 0, true);
    private JButton delMusicButton = formatButton(delIcon, 0, true);
    private JButton createPlaylistButton = formatButton(addIcon, 0, true);
    private JButton deletePlaylistButton = formatButton(delIcon, 0, true);


    private class userImports{
        private boolean noPlaylistsByte = false;

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

                if ( !( musicDirectory.exists() || musicDirectoryPT.exists() ) ) {
                    System.out.println("Default music folder not found.");
                    return;
                }

                File[] files;
                //all files in the music folder
                if (musicDirectory.exists()){
                    files = musicDirectory.listFiles();
                } else {
                    files = musicDirectoryPT.listFiles();
                }

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

                if (mp3Files.isEmpty()) {
                    System.out.println("No .mp3 files found in music folder.");
                    return;
                }

                Playlist playlist = new Playlist("Music");
                playlists.put("Music", playlist);
                mainList.setListData(playlists.keySet().toArray(new String[0]));
                mainList.setSelectedIndex(0);
                playlistPanel.add(playlist.getPlaylist(), "Music");

                for (File mp3File : mp3Files) {
                    playlist.getListModel().addElement(mp3File.getAbsolutePath());
                }
            }
        }

    }


    private JButton formatButton( ImageIcon icon, int RGB, boolean bool ){
        JButton bnt = new JButton( icon );
        bnt.setBackground(new Color(RGB, RGB, RGB));
        bnt.setBorder( BorderFactory.createEmptyBorder() );
        bnt.setFocusPainted(false);
        bnt.setContentAreaFilled(bool);
        add(bnt);
        return bnt;
    }


    public Apolo(){

        userImports userImports = new userImports();
        playlistManager = userImports.getPlaylists();

        mainList = playlistManager.getMainList();
        playlists = playlistManager.getMap();
        cardLayout = playlistManager.getCardLayout();
        playlistPanel = playlistManager.getPlaylistPanel();


        userImports.getMusicDirectory();


        //ADD OR DELETE MUSIC
        addMusicButton.setBounds(758, 22, 20, 20);

        addMusicButton.addActionListener(e -> {
            try {
                String selectedPlaylistName = mainList.getSelectedValue();

                if (selectedPlaylistName == null)
                    throw new MusicException("Select a playlist before adding a song!", "Null Playlist");

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(true);
                fileChooser.setDialogTitle( "Specify files to add (press Ctrl for multi-selection)" );
                fileChooser.setFileFilter( new FileNameExtensionFilter("MP3 Files", "mp3") );

                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File[] selectedFiles = fileChooser.getSelectedFiles();
                    playlist = playlists.get(selectedPlaylistName);

                    for (File file : selectedFiles) {
                        playlist.getListModel().addElement( file.getAbsolutePath() );
                    }

                    playlistManager.saveToFile(playlistManager);
                }

            } catch (MusicException ex) {
                ex.showMessage();
            }
        });


        delMusicButton.setBounds(324, 27, 20, 10);

        delMusicButton.addActionListener(e -> {
            String selectedPlaylistName = mainList.getSelectedValue();

            try {
                if (selectedPlaylistName == null)
                    throw new MusicException("Select a playlist before deleting a song!", "Null Playlist");

                playlist = playlists.get(selectedPlaylistName);
                int selectedMusicPathIndex = playlist.getMp3List().getSelectedIndex();
                if ( selectedMusicPathIndex == -1 )
                    throw new MusicException("Select a song before deleting it!", "Null Music");

                int confirmSongDel = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected music?", "Confirm Song Deletion", JOptionPane.YES_NO_OPTION);
                if (confirmSongDel == JOptionPane.YES_OPTION) {
                    playlist.getListModel().remove(selectedMusicPathIndex);
                    playlistManager.saveToFile(playlistManager);
                }
            }
            catch (MusicException ex){
                ex.showMessage();
            }
        });


        playlistPanel.setBounds(286, 20, 532, 310);//position and size of the panel with the songs
        add(playlistPanel);


        //create a DropTarget and set it to the panel
        DropTarget addMusicDropTarget = new DropTarget(playlistPanel, new DropTargetAdapter() {
            public void drop(DropTargetDropEvent dtde) {
                try {
                    String selectedPlaylistName = mainList.getSelectedValue();

                    if (selectedPlaylistName == null)
                        throw new MusicException("Select a playlist before adding a song!", "Null Playlist");

                    //check if the dropped data is a list of files
                    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);
                        List<File> files = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                        playlist = playlists.get(selectedPlaylistName);

                        for (File file : files) {
                            playlist.getListModel().addElement( file.getAbsolutePath() );
                        }
                        dtde.dropComplete(true);
                        playlistManager.saveToFile(playlistManager);
                    } else {
                        dtde.rejectDrop();
                    }
                }
                catch (MusicException e) {
                    e.showMessage();
                    dtde.dropComplete(false);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    dtde.dropComplete(false);
                }
            }
        });

        playlistPanel.setDropTarget(addMusicDropTarget);



        //CREATE OR DELETE PLAYLIST
        createPlaylistButton.setBounds(218, 22, 20, 20);

        createPlaylistButton.addActionListener(e -> {
            playlistManager.createPlaylist();

            if(mainList.getModel().getSize() == 1) {
                playlistPanel.revalidate();
                addMusicButton.repaint();
                delMusicButton.repaint();
            }

        });


        deletePlaylistButton.setBounds(47, 27, 20, 10);

        deletePlaylistButton.addActionListener(e -> {
            try {
                playlistManager.deletePlaylist(playlistManager);
            }
            catch (MusicException ex){
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
        playButton.addMouseListener(new MouseAdapter() {//inflate effect
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
                playButton.setIcon( getIcon(iconName, size, size) );
            }
        });

        playButton.addActionListener(e -> {
            if(music.isPlaying()){//pause
                pause = true;
                music.pausePlayblack();
            }
            else {//play
                String selectedPlaylistName = mainList.getSelectedValue();
                Playlist selectedPlaylist = playlists.get(selectedPlaylistName);

                try {
                    if(selectedPlaylist == null)
                        throw new MusicException("Select a playlist first!", "Null Playlist");

                    pause = false;
                    String file_path = selectedPlaylist.getMp3List().getSelectedValue();

                    if(file_path == null)
                        throw new MusicException("Select a song first!", "Null Music");

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

                } catch(MusicException ex){
                    ex.showMessage();
                }
            }
        });


        previousButton.addMouseListener( new java.awt.event.MouseAdapter() {
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
                previousButton.setIcon( getIcon("/icons/48_music_next_player_icon.png", size, size) );
            }
        });

        previousButton.addActionListener(e -> {
            skipMusic(-1);
        });


        nextButton.addMouseListener( new java.awt.event.MouseAdapter() {
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
                nextButton.setIcon( getIcon("/icons/o48_music_next_player_icon.png", size, size) );
            }
        });

        nextButton.addActionListener( e -> {
            skipMusic(1);
        });



        music.addChangeListener(evt -> {//play, pause (icons) and play in sequence or with repeat
            handleMusicChange();
        });



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
        playbackControl.add(previousButton);
        playbackControl.add(playButton);
        playbackControl.add(nextButton);
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


    private void handleMusicChange() {
        if (music.isPlaying()) {
            playButton.setIcon(getIcon("/icons/48_circle_pause_icon.png", 43, 43));
        }
        else {
            playButton.setIcon(getIcon("/icons/48_circle_play_icon.png", 43, 43));

            if (!pause) {
                String selectedPlaylistName = mainList.getSelectedValue();
                Playlist selectedPlaylist = playlists.get(selectedPlaylistName);

                if (selectedPlaylist != null) {
                    handlePlaybackState(selectedPlaylist);
                }
            }

        }
    }

    private void handlePlaybackState(Playlist selectedPlaylist) {
        int playlistSize = selectedPlaylist.getMp3List().getModel().getSize();
        int currentIndex = selectedPlaylist.getMp3List().getSelectedIndex();

        switch (currentRepeatState) {
            case INACTIVE:
                if (currentIndex < playlistSize - 1) {
                    nextButton.doClick();
                }
                break;

            case REPEAT:
                playButton.doClick();
                break;

            case REPEAT_ONCE:
                handleRepeatOnce(playlistSize, currentIndex);
                break;
        }
    }

    private void handleRepeatOnce(int playlistSize, int currentIndex) {
        if (counterRepeatOnce < 1) {
            playButton.doClick();
            counterRepeatOnce++;
        } else {
            if (currentIndex < playlistSize - 1) {
                nextButton.doClick();
            }
            counterRepeatOnce = 0;
        }
    }


    public void skipMusic(int nextOrPrevious){
        pause = true;
        music.pausePlayblack();

        String selectedPlaylistName = mainList.getSelectedValue();
        Playlist selectedPlaylist = playlists.get(selectedPlaylistName);

        try {
            if ( selectedPlaylist == null )
                throw new MusicException("Select a playlist first!", "Null Playlist");

            int newIndex = selectedPlaylist.getMp3List().getSelectedIndex() + nextOrPrevious;
            if (newIndex < selectedPlaylist.getMp3List().getModel().getSize() && newIndex >= 0) {
                String file_path = selectedPlaylist.getMp3List().getModel().getElementAt(newIndex);//next or previous song
                System.out.println(file_path);
                selectedPlaylist.getMp3List().setSelectedIndex(newIndex);//changes the JList to the next or previous song

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
        } catch(MusicException ex){
            ex.showMessage();
        }
    }


    public static void main(String[] args) {
        new Apolo();
    }

}