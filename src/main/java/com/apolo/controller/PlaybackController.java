package com.apolo.controller;

import com.apolo.gui.ApoloPopUp;
import com.apolo.model.MusicException;
import com.apolo.model.PlaybackManager;
import com.apolo.model.Playlist;

import java.net.URL;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;


public class PlaybackController {
    private JProgressBar progressBar;
    private JLabel progressLabel;

    private PlaybackManager playbackManager = new PlaybackManager();
    //private Thread musicThread = new Thread(playbackManager);

    private boolean pause = true;
    private String musicPath;

    private enum RepeatState { INACTIVE, REPEAT, REPEAT_ONCE }
    private RepeatState currentRepeatState = RepeatState.INACTIVE;
    private int counterRepeatOnce = 0;


    public PlaybackController(JProgressBar progressBar, JLabel progressLabel){
        this.progressBar = progressBar;
        this.progressLabel = progressLabel;

        //registra um Listener
        playbackManager.setProgressListener((progressBarValue, formattedProgressText) -> {
            progressBar.setValue(progressBarValue);
            progressLabel.setText(formattedProgressText);
        });
    }


    public PlaybackManager getPlaybackManager() {
        return playbackManager;
    }




    public void setPlaybackTime(int mouseX) {///////////////////////////////////////
        int progressBarVal = (int) Math.round(((double) mouseX / progressBar.getWidth()) * progressBar.getMaximum());

        progressBar.setValue(progressBarVal);

        //creates a new progress time according to the bar percentage and music duration
        double newTime = (progressBarVal / 100.0) * playbackManager.getDuration();

        playbackManager.setPlaybackFrame(newTime); //update frame

        if (playbackManager.isPlaying()) {
            System.out.println("pausa, encerra thread e inicia uma nova...");
            pause = true;
            playbackManager.pausePlayback();
            playbackManager.stopPlayback();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            playbackManager.startPlayback();
            pause = false;
        }

    }


    public void playMusic(Playlist selectedPlaylist){
        if(playbackManager.isPlaying()){//pause
            pause = true;
            playbackManager.pausePlayback();
        }
        else {//play

            try {
                if (selectedPlaylist == null)
                    throw new MusicException("Select a playlist first!", "Null Playlist");

                pause = false;
                String filePath = selectedPlaylist.getMp3List().getSelectedValue();

                if (filePath == null)
                    throw new MusicException("Select a song first!", "Null Music");

                if( filePath.equals(musicPath) ) {//resume or repeat without printPath
                    try {
                        playbackManager.setMusic(musicPath);
                        playbackManager.startPlayback();
                    } catch(MusicException ex){
                        new ApoloPopUp().showError(ex.getMessage(), ex.getErrorName());
                    }
                }
                else{//new play
                    System.out.println(filePath);
                    musicPath = filePath;

                    playbackManager.resetPlayback();
                    try {
                        playbackManager.setMusic(musicPath);
                        playbackManager.startPlayback();
                    } catch(MusicException ex){
                        new ApoloPopUp().showError(ex.getMessage(), ex.getErrorName());
                    }
                }

            } catch(MusicException ex){
                new ApoloPopUp().showWarning(ex.getMessage(), ex.getErrorName());
            }

        }
    }


    public String getMusicDuration(){
        return playbackManager.getFormatDuration();
    }


    public void skipMusic(int nextOrPrevious, Playlist selectedPlaylist){
        pause = true;
        playbackManager.pausePlayback();

        try {
            if (selectedPlaylist == null)
                throw new MusicException("Select a playlist first!", "Null Playlist");

            int newIndex = selectedPlaylist.getMp3List().getSelectedIndex() + nextOrPrevious;
            if (newIndex < selectedPlaylist.getMp3List().getModel().getSize() && newIndex >= 0) {
                String filePath = selectedPlaylist.getMp3List().getModel().getElementAt(newIndex);//next or previous song
                System.out.println(filePath);
                selectedPlaylist.getMp3List().setSelectedIndex(newIndex);//changes the JList to the next or previous song

                musicPath = filePath;
                playbackManager.resetPlayback();
                pause = false;

                try {
                    playbackManager.setMusic(filePath);
                    playbackManager.startPlayback();
                } catch (MusicException ex) {
                    new ApoloPopUp().showError(ex.getMessage(), ex.getErrorName());
                }
            } else {
                System.out.println("There are no more songs in the playlist.");
                String file_path = selectedPlaylist.getMp3List().getSelectedValue();

                playbackManager.resetPlayback();
                pause = false;

                try {
                    playbackManager.setMusic(file_path);
                    playbackManager.startPlayback();
                } catch (MusicException ex) {
                    new ApoloPopUp().showError(ex.getMessage(), ex.getErrorName());
                }
            }

        } catch(MusicException ex){
            new ApoloPopUp().showWarning(ex.getMessage(), ex.getErrorName());
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


    public void handleMusicChange(JButton playButton, Playlist selectedPlaylist) {
        if (playbackManager.isPlaying()) {
            playButton.setIcon(getIcon("/icons/48_circle_pause_icon.png", 43, 43));
        }
        else {
            playButton.setIcon(getIcon("/icons/48_circle_play_icon.png", 43, 43));

            if (!pause) {//entra aqui quando termina e não quando pausa
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
                    skipMusic(1, selectedPlaylist);
                }
                break;

            case REPEAT:
                playMusic(selectedPlaylist);
                break;

            case REPEAT_ONCE:
                handleRepeatOnce(playlistSize, currentIndex, selectedPlaylist);
                break;
        }
    }


    private void handleRepeatOnce(int playlistSize, int currentIndex, Playlist selectedPlaylist) {
        if (counterRepeatOnce < 1) {
            playMusic(selectedPlaylist);
            counterRepeatOnce++;
        } else {
            if (currentIndex < playlistSize - 1) {
                skipMusic(1, selectedPlaylist);
            }
            counterRepeatOnce = 0;
        }
    }


}