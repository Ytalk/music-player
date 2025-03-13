package com.apolo.controller;

import com.apolo.gui.MusicException;
import com.apolo.model.PlaybackManager;
import com.apolo.model.Playlist;
import com.apolo.model.PlaylistManager;

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

public class ListenerController implements ListenerControllerInterface{
    private PlaylistManager playlistManager;
    //private Map<String, Playlist> playlists;//stores Playlist instances
    //private JPanel playlistPanel;
    //private JList<String> mainList;
    private Playlist playlist;
    //private CardLayout cardLayout;



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


    public PlaybackManager getMusic() {
        return music;
    }


    /*public JPanel getPlaylistPanel(){
        return playlistPanel;
    }*/

    public PlaylistManager getPlaylistManager() {
        return playlistManager;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JLabel getProgressLabel() {
        return progressLabel;
    }



    private boolean noPlaylistsByte = false;

    public void getPlaylists() {
        playlistManager = new PlaylistManager();
        playlistManager.loadFile();
        if (playlistManager.getManager() != null) {
            playlistManager = playlistManager.getManager();
        }
        else{
            noPlaylistsByte = true;
        }
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

            Playlist playlist = new Playlist("Music");//o erro está aqui pq Apolo acessa diretamanete o que está em PM, e não oq ta aq. mude tudo para aq p/ acesso direto a PM
            playlistManager.getMap().put("Music", playlist);
            playlistManager.getMainList().setListData(playlistManager.getMap().keySet().toArray(new String[0]));
            playlistManager.getMainList().setSelectedIndex(0);
            playlistManager.getPlaylistPanel().add(playlist.getPlaylist(), "Music");

            for (File mp3File : mp3Files) {
                playlist.getListModel().addElement(mp3File.getAbsolutePath());
            }
        }
    }



    public ImageIcon getIcon(String pathIcon, int width, int height){
        URL imageUrl = getClass().getResource(pathIcon);
        //internal uses URL to instantiate ImageIcon which is transformed into Image for resizing. External uses Image to instantiate ImageIcon.
        return new ImageIcon( new ImageIcon(imageUrl).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH) );
    }


    public JButton formatButton( ImageIcon icon, int RGB, boolean bool ){
        JButton bnt = new JButton( icon );
        bnt.setBackground(new Color(RGB, RGB, RGB));
        bnt.setBorder( BorderFactory.createEmptyBorder() );
        bnt.setFocusPainted(false);
        bnt.setContentAreaFilled(bool);
        return bnt;
    }







    public void addMisicByButton(){
        try {
            String selectedPlaylistName = (String) playlistManager.getMainList().getSelectedValue();//////////////

            if (selectedPlaylistName == null)
                throw new MusicException("Select a playlist before adding a song!", "Null Playlist");

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setDialogTitle( "Specify files to add (press Ctrl for multi-selection)" );
            fileChooser.setFileFilter( new FileNameExtensionFilter("MP3 Files", "mp3") );

            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File[] selectedFiles = fileChooser.getSelectedFiles();
                playlist = playlistManager.getMap().get(selectedPlaylistName);///////////////////

                for (File file : selectedFiles) {
                    playlist.getListModel().addElement( file.getAbsolutePath() );
                }

                playlistManager.saveToFile(playlistManager);
            }

        } catch (MusicException ex) {
            ex.showMessage();
        }
    }


    public void delMusic(){
        String selectedPlaylistName = (String) playlistManager.getMainList().getSelectedValue();////////////////

        try {
            if (selectedPlaylistName == null)
                throw new MusicException("Select a playlist before deleting a song!", "Null Playlist");

            playlist = playlistManager.getMap().get(selectedPlaylistName);////////////////////////////
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
    }

        public DropTarget addMusicByDropTarget(){
            DropTarget addMusicDropTarget = new DropTarget(playlistManager.getPlaylistPanel(), new DropTargetAdapter() {


            public void drop(DropTargetDropEvent dtde) {
                try {
                    String selectedPlaylistName = (String) playlistManager.getMainList().getSelectedValue();

                    if (selectedPlaylistName == null)
                        throw new MusicException("Select a playlist before adding a song!", "Null Playlist");

                    //check if the dropped data is a list of files
                    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);
                        List<File> files = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                        playlist = playlistManager.getMap().get(selectedPlaylistName);

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

        return addMusicDropTarget;
    }


    public void playButton(JLabel durationLabel){
        if(music.isPlaying()){//pause
            pause = true;
            music.pausePlayblack();
        }
        else {//play
            String selectedPlaylistName = (String) playlistManager.getMainList().getSelectedValue();///////////////////
            Playlist selectedPlaylist = playlistManager.getMap().get(selectedPlaylistName);///////////////

            try {
                if(selectedPlaylist == null)
                    throw new MusicException("Select a playlist first!", "Null Playlist");

                pause = false;
                String file_path = selectedPlaylist.getMp3List().getSelectedValue();

                if(file_path == null)
                    throw new MusicException("Select a song first!", "Null Music");

                if( file_path.equals(music_path) ) {//resume or repeat without printPath
                    music.setMusic(music_path);

                    music.startPlayback();
                }
                else{//new play
                    System.out.println(file_path);
                    music_path = file_path;

                    music.resetPlayback();
                    music.setMusic(music_path);
                    durationLabel.setText( music.getFormatDuration() );

                    music.startPlayback();
                }

            } catch(MusicException ex){
                ex.showMessage();
            }
        }
    }


    public void skipMusic(int nextOrPrevious, JLabel durationLabel){
        pause = true;
        music.pausePlayblack();

        String selectedPlaylistName = (String) playlistManager.getMainList().getSelectedValue();///////////////////////
        Playlist selectedPlaylist = playlistManager.getMap().get(selectedPlaylistName);///////////////////////

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




    public void toggleRepeatState(JButton repeatButton) {
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


    public void handleMusicChange(JButton playButton, JLabel durationLabel) {
        if (music.isPlaying()) {
            playButton.setIcon(getIcon("/icons/48_circle_pause_icon.png", 43, 43));
        }
        else {
            playButton.setIcon(getIcon("/icons/48_circle_play_icon.png", 43, 43));

            if (!pause) {//entra aqui quando termina e não quando pausa
                String selectedPlaylistName = (String) playlistManager.getMainList().getSelectedValue();////////////////
                Playlist selectedPlaylist = playlistManager.getMap().get(selectedPlaylistName);////////////////////////

                if (selectedPlaylist != null) {
                    handlePlaybackState(selectedPlaylist, durationLabel);
                }
            }

        }
    }

    private void handlePlaybackState(Playlist selectedPlaylist, JLabel durationLabel) {
        int playlistSize = selectedPlaylist.getMp3List().getModel().getSize();
        int currentIndex = selectedPlaylist.getMp3List().getSelectedIndex();

        switch (currentRepeatState) {
            case INACTIVE:
                if (currentIndex < playlistSize - 1) {
                    skipMusic(1, durationLabel);
                }
                break;

            case REPEAT:
                playButton(durationLabel);
                break;

            case REPEAT_ONCE:
                handleRepeatOnce(playlistSize, currentIndex, durationLabel);
                break;
        }
    }


    private void handleRepeatOnce(int playlistSize, int currentIndex, JLabel durationLabel) {
        if (counterRepeatOnce < 1) {
            playButton(durationLabel);
            counterRepeatOnce++;
        } else {
            if (currentIndex < playlistSize - 1) {
                skipMusic(1, durationLabel);
            }
            counterRepeatOnce = 0;
        }
    }


}
