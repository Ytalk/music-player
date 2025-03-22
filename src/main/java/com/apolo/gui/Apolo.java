package com.apolo.gui;

import com.apolo.controller.PlaybackController;
import com.apolo.controller.PlaylistController;
import com.apolo.model.MusicException;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JFrame;


public class Apolo extends JFrame {

    private JProgressBar progressBar = new JProgressBar(0, 100);
    private JLabel durationLabel = new JLabel( "00:00" );
    private JLabel progressLabel = new JLabel( "00:00" );

    private PlaybackController playbackController = new PlaybackController(progressBar, progressLabel);
    private PlaylistController playlistController = new PlaylistController();

    private JButton repeatButton = playbackController.formatButton( playbackController.getIcon("/icons/repeat-song-512.png", 23, 23), 64, false );

    private JButton playButton = playbackController.formatButton( playbackController.getIcon("/icons/48_circle_play_icon.png", 43, 43), 64, false );
    private JButton previousButton = playbackController.formatButton( playbackController.getIcon("/icons/48_music_next_player_icon.png", 38, 38), 64, false );
    private JButton nextButton = playbackController.formatButton( playbackController.getIcon("/icons/o48_music_next_player_icon.png", 38, 38), 64, false );

    private ImageIcon addIcon = playbackController.getIcon("/icons/392_4-more-white.png", 17, 17);
    private ImageIcon delIcon = playbackController.getIcon("/icons/rectangle-632-180.png", 17, 7);

    private JButton addMusicButton = playbackController.formatButton(addIcon, 0, true);
    private JButton delMusicButton = playbackController.formatButton(delIcon, 0, true);
    private JButton createPlaylistButton = playbackController.formatButton(addIcon, 0, true);
    private JButton deletePlaylistButton = playbackController.formatButton(delIcon, 0, true);


    public Apolo(){

        playlistController.getPlaylists();
        playlistController.getMusicDirectory();


        //ADD OR DELETE MUSIC
        add(addMusicButton);
        addMusicButton.setBounds(758, 22, 20, 20);

        addMusicButton.addActionListener(e -> {
            playlistController.addMisicByButton();
        });


        add(delMusicButton);
        delMusicButton.setBounds(324, 27, 20, 10);

        delMusicButton.addActionListener(e -> {
            playlistController.delMusic();
        });

        ///////////////////////////////////////////
        playlistController.getPlaylistManager().getPlaylistPanel().setBounds(286, 20, 532, 310);//position and size of the panel with the songs
        add( playlistController.getPlaylistManager().getPlaylistPanel() );/////////////////////////


        //create a DropTarget and set it to the panel
        playlistController.getPlaylistManager().getPlaylistPanel().setDropTarget( playlistController.addMusicByDropTarget() );



        //CREATE OR DELETE PLAYLIST
        add(createPlaylistButton);
        createPlaylistButton.setBounds(218, 22, 20, 20);

        createPlaylistButton.addActionListener(e -> {
            try {
                playlistController.getPlaylistManager().createPlaylist();/////////////////////////////////
            }
            catch (MusicException ex){
                new ApoloPopUp().showWarning(ex.getMessage(), ex.getErrorName());
            }

            if(playlistController.getPlaylistManager().getMainList().getModel().getSize() == 1) {////////////////////
                playlistController.getPlaylistManager().getPlaylistPanel().revalidate();///////////////////
                addMusicButton.repaint();
                delMusicButton.repaint();
            }

        });


        add(deletePlaylistButton);
        deletePlaylistButton.setBounds(47, 27, 20, 10);

        deletePlaylistButton.addActionListener(e -> {
            try {
                playlistController.getPlaylistManager().deletePlaylist( playlistController.getPlaylistManager() );/////////////////////////
                playlistController.getPlaylistManager().getManager().saveToFile( playlistController.getPlaylistManager() );
            }
            catch (MusicException ex){
                new ApoloPopUp().showWarning(ex.getMessage(), ex.getErrorName());
            }
        });

        ////////////////
        playlistController.getPlaylistManager().getMainListPanel().setBounds(20, 20, 245, 400);//Position and size of the panel with playlists
        add(playlistController.getPlaylistManager().getMainListPanel());


        //listener to switch between playlists
        playlistController.getPlaylistManager().getMainList().addListSelectionListener(e -> {///////////////////////
            String selectedPlaylist = (String) playlistController.getPlaylistManager().getMainList().getSelectedValue();///////////
            playlistController.getPlaylistManager().getCardLayout().show(playlistController.getPlaylistManager().getPlaylistPanel(), selectedPlaylist);////////////////
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
                String iconName = playbackController.getPlaybackManager().isPlaying() ? "/icons/48_circle_pause_icon.png" : "/icons/48_circle_play_icon.png";
                playButton.setIcon( playbackController.getIcon(iconName, size, size) );
            }
        });

        playButton.addActionListener(e -> {
            playbackController.playMusic( playlistController.getSelectedPlaylist() );
            durationLabel.setText( playbackController.getMusicDuration() );
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
                previousButton.setIcon( playbackController.getIcon("/icons/48_music_next_player_icon.png", size, size) );
            }
        });

        previousButton.addActionListener(e -> {
            playbackController.skipMusic(-1, playlistController.getSelectedPlaylist());
            durationLabel.setText( playbackController.getMusicDuration() );
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
                nextButton.setIcon( playbackController.getIcon("/icons/o48_music_next_player_icon.png", size, size) );
            }
        });

        nextButton.addActionListener( e -> {
            playbackController.skipMusic(1, playlistController.getSelectedPlaylist());
            durationLabel.setText( playbackController.getMusicDuration() );
        });


        playbackController.getPlaybackManager().addChangeListener(evt -> {//play, pause (icons) and play in sequence or with repeat
            playbackController.handleMusicChange(playButton, playlistController.getSelectedPlaylist());
            durationLabel.setText( playbackController.getMusicDuration() );
        });


        repeatButton.addActionListener(e -> {
            playbackController.toggleRepeatState(repeatButton);
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

        JLabel playbackControlBackground = new JLabel( playbackController.getIcon("/icons/playback-control.png", 400, 70) );
        playbackControlBackground.setBounds(67, 16, 400, 70);
        playbackPanel.add(playbackControlBackground);


        progressBar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                playbackController.setPlaybackTime(evt.getX());
            }
        });

        progressBar.setBounds(40, 4, 450, 5);
        progressBar.setForeground( new Color(129, 13, 175) );
        progressBar.setBackground(Color.WHITE);
        progressBar.setUI( new CustomProgressBarUI() );
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


    public static void main(String[] args) {
        new Apolo();
    }

}