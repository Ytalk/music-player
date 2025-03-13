package com.apolo.gui;

import com.apolo.controller.ListenerController;
import com.apolo.controller.ListenerControllerInterface;
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


public class Apolo extends JFrame {

    private ListenerControllerInterface listenerController = new ListenerController();
    /*private JPanel playlistPanel;
    private PlaylistManager playlistManager;
    private JList mainList;
    private CardLayout cardLayout;
    private Map<String, Playlist> playlists;//stores Playlist instances*/


    private JButton repeatButton = listenerController.formatButton( listenerController.getIcon("/icons/repeat-song-512.png", 23, 23), 64, false );

    private JButton playButton = listenerController.formatButton( listenerController.getIcon("/icons/48_circle_play_icon.png", 43, 43), 64, false );
    private JButton previousButton = listenerController.formatButton( listenerController.getIcon("/icons/48_music_next_player_icon.png", 38, 38), 64, false );
    private JButton nextButton = listenerController.formatButton( listenerController.getIcon("/icons/o48_music_next_player_icon.png", 38, 38), 64, false );

    private ImageIcon addIcon = listenerController.getIcon("/icons/392_4-more-white.png", 17, 17);
    private ImageIcon delIcon = listenerController.getIcon("/icons/rectangle-632-180.png", 17, 7);

    private JButton addMusicButton = listenerController.formatButton(addIcon, 0, true);
    private JButton delMusicButton = listenerController.formatButton(delIcon, 0, true);
    private JButton createPlaylistButton = listenerController.formatButton(addIcon, 0, true);
    private JButton deletePlaylistButton = listenerController.formatButton(delIcon, 0, true);


    //private JProgressBar progressBar = new JProgressBar(0, 100);
    private JProgressBar progressBar = listenerController.getProgressBar();
    private JLabel durationLabel = new JLabel( "00:00" );
    //private JLabel progressLabel = new JLabel( "00:00" );
    private JLabel progressLabel = listenerController.getProgressLabel();


    public Apolo(){

        listenerController.getPlaylists();
        listenerController.getMusicDirectory();


        //ADD OR DELETE MUSIC
        add(addMusicButton);
        addMusicButton.setBounds(758, 22, 20, 20);

        addMusicButton.addActionListener(e -> {
            listenerController.addMisicByButton();
        });


        add(delMusicButton);
        delMusicButton.setBounds(324, 27, 20, 10);

        delMusicButton.addActionListener(e -> {
            listenerController.delMusic();
        });

        ///////////////////////////////////////////
        listenerController.getPlaylistManager().getPlaylistPanel().setBounds(286, 20, 532, 310);//position and size of the panel with the songs
        add( listenerController.getPlaylistManager().getPlaylistPanel() );/////////////////////////


        //create a DropTarget and set it to the panel
        listenerController.getPlaylistManager().getPlaylistPanel().setDropTarget( listenerController.addMusicByDropTarget() );



        //CREATE OR DELETE PLAYLIST
        add(createPlaylistButton);
        createPlaylistButton.setBounds(218, 22, 20, 20);

        createPlaylistButton.addActionListener(e -> {
            listenerController.getPlaylistManager().createPlaylist();/////////////////////////////////

            if(listenerController.getPlaylistManager().getMainList().getModel().getSize() == 1) {////////////////////
                listenerController.getPlaylistManager().getPlaylistPanel().revalidate();///////////////////
                addMusicButton.repaint();
                delMusicButton.repaint();
            }

        });


        add(deletePlaylistButton);
        deletePlaylistButton.setBounds(47, 27, 20, 10);

        deletePlaylistButton.addActionListener(e -> {
            try {
                listenerController.getPlaylistManager().deletePlaylist( listenerController.getPlaylistManager() );/////////////////////////
            }
            catch (MusicException ex){
                ex.showMessage();
            }
        });

        ////////////////////////////////////
        listenerController.getPlaylistManager().getMainListPanel().setBounds(20, 20, 245, 400);//Position and size of the panel with playlists
        add(listenerController.getPlaylistManager().getMainListPanel());//////////////////////////////////


        //listener to switch between playlists
        listenerController.getPlaylistManager().getMainList().addListSelectionListener(e -> {///////////////////////
            String selectedPlaylist = (String) listenerController.getPlaylistManager().getMainList().getSelectedValue();///////////
            listenerController.getPlaylistManager().getCardLayout().show(listenerController.getPlaylistManager().getPlaylistPanel(), selectedPlaylist);////////////////
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
                String iconName = listenerController.getMusic().isPlaying() ? "/icons/48_circle_pause_icon.png" : "/icons/48_circle_play_icon.png";
                playButton.setIcon( listenerController.getIcon(iconName, size, size) );
            }
        });

        playButton.addActionListener(e -> {
            listenerController.playButton(durationLabel);
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
                previousButton.setIcon( listenerController.getIcon("/icons/48_music_next_player_icon.png", size, size) );
            }
        });

        previousButton.addActionListener(e -> {
            listenerController.skipMusic(-1, durationLabel);
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
                nextButton.setIcon( listenerController.getIcon("/icons/o48_music_next_player_icon.png", size, size) );
            }
        });

        nextButton.addActionListener( e -> {
            listenerController.skipMusic(1, durationLabel);
        });



        listenerController.getMusic().addChangeListener(evt -> {//play, pause (icons) and play in sequence or with repeat
            listenerController.handleMusicChange(playButton, durationLabel);
        });



        repeatButton.addActionListener(e -> {
            listenerController.toggleRepeatState(repeatButton);
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

        JLabel playbackControlBackground = new JLabel( listenerController.getIcon("/icons/playback-control.png", 400, 70) );
        playbackControlBackground.setBounds(67, 16, 400, 70);
        playbackPanel.add(playbackControlBackground);


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