package com.apolo.model.audio;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.Timer;

import com.apolo.model.exception.MusicException;
import com.apolo.model.util.AudioMetadataReader;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

/**
 * The `JLayerAudioPlayer` class represents a music player that can play audio files using the JavaLayer library.
 * It implements the `Runnable` interface to enable multithreading for playback.
 */
public class JLayerAudioPlayer {
    private static final int FRAME_RATE = 45;
    private boolean isPlaying = false;
    private AdvancedPlayer player;

    private ChangeListener changeListener;//listener for state change events
    private ProgressListener progressListener;//bar and time

    private int currentFrame = 0;
    protected File currentFile;
    private Timer progressTimer;

    private FileInputStream fileInputStream = null;
    private BufferedInputStream bufferedInputStream = null;

    private double duration;

    /**
     * Sets the audio file to be played by the music player.
     *
     * @param filePath The path to the audio file to be played.
     * @throws MusicException If the specified file path is null or if the file does not exist.
     */
    public void setMusic(String filePath) throws MusicException {
        if (filePath == null) throw new MusicException("Select a song first", "Null Path");
        currentFile = new File(filePath);
        if (!currentFile.exists()) throw new MusicException("Song not found: " + filePath, "Path does not exist");

        duration = AudioMetadataReader.getDuration(filePath);
        resetPlayback();//reset current frame when new music is set
    }


    /**
     * Starts playback of the audio file.
     */
    public void play() throws MusicException {
        openPlayerStreams();

        playbackListener();
        isPlaying = true;
        //System.out.println("Playback started!");
        fireStateChanged(); //notify listeners of state change

        startProgressWatcher();

        try {
            player.play(); //start playback
        } catch (JavaLayerException e) {
            throw new MusicException("Error during playback: " + e.getMessage(), "Playback Error");
        } finally {
            cleanupPlayer();
        }
    }

    /**
     * Resumes playback from the last paused position.
     */
    public void resume() throws MusicException {
        openPlayerStreams();

        playbackListener();
        isPlaying = true;
        //System.out.println("Resumed playback");
        fireStateChanged();

        startProgressWatcher();

        try {
            player.play(currentFrame, Integer.MAX_VALUE);
        } catch (JavaLayerException e) {
            throw new MusicException("Error resuming playback: " + e.getMessage(), "Playback Error");
        } finally {
            cleanupPlayer();
        }
    }


    /**
     * Stops playback of the audio file.
     */
    public void pause() {
        if (isPlaying) {
            System.out.println("Playback paused/stopped!");
            if (player != null) {
                player.close();
            }
            System.out.println("Playback is over!");
        }
    }

    private void cleanupPlayer() {
        if (progressTimer != null) progressTimer.stop();
        closeStreams();
        isPlaying = false;
        fireStateChanged();
        System.out.println("Playback state cleaned up.");
    }

    /**
     * Resets the frame counter, progress bar and progress label to zero.
     */
    private void resetPlayback() {
        currentFrame = 0;
        //notifica o Controller para atualizar a View
        if (progressListener != null) {
            progressListener.onProgressUpdate(0, "00:00");
        }
    }

    //set up a playback listener to handle playback events
    private void playbackListener() {
        player.setPlayBackListener(new PlaybackListener() {
            @Override
            public void playbackFinished(PlaybackEvent evt) {
                resetPlayback();
                System.out.println("Playback complete!");
            }
        });
    }

    public void seek(double timeInSeconds) throws MusicException {
        currentFrame = (int) (timeInSeconds * FRAME_RATE);//45 frames per second

        System.out.println("fecha");
        closeStreams();

        //if (isPlaying) {
        System.out.println("Seeking to " + timeInSeconds + "s and resuming playback.");
        resume();
        //} else {
        //  System.out.println("Seeking to " + timeInSeconds + "s. Player remains paused.");
        //}
    }


    private void openPlayerStreams() throws MusicException {
        try { //create a new FileInputStream and wrap it in a BufferedInputStream
            fileInputStream = new FileInputStream(currentFile);//create a FileInputStream and wrap it in a BufferedInputStream
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            player = new AdvancedPlayer(bufferedInputStream);//initialize the AdvancedPlayer with the BufferedInputStream
        } catch (IOException e) {
            throw new MusicException("Error opening file: " + e.getMessage(), "File Error");
        } catch (JavaLayerException e) {
            throw new MusicException("Error initializing player: " + e.getMessage(), "Player Error");
        }
    }

    private void closeStreams() {
        try {
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
        } catch (IOException e) { }
        try {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        } catch (IOException e) { }
        bufferedInputStream = null;
        fileInputStream = null;
    }


    /**
     * Adds a ChangeListener to the music player to listen for state change events.
     *
     * @param listener The ChangeListener to be added.
     */
    public void addChangeListener(ChangeListener listener) {
        changeListener = listener;
    }

    /**
     * Notifies the registered ChangeListener about a change in the player's state.
     */
    private void fireStateChanged() {
        if (changeListener != null) {
            changeListener.stateChanged(new ChangeEvent(this));
        }
    }

    /**
     * Checks if the music player is currently playing audio.
     *
     * @return True if the player is playing audio, false otherwise.
     */
    public boolean isPlaying() {
        return isPlaying;
    }


    /**
     * Starts a progress watcher to monitor the playback progress of the music and update the GUI accordingly.
     */
    private void startProgressWatcher() {
        progressTimer = new Timer(1000, e -> {
            currentFrame += FRAME_RATE;

            int secs = currentFrame / FRAME_RATE;
            String formattedProgressText = String.format("%02d:%02d", secs / 60, secs % 60);

            //calculate the progress as a percentage relative to the total duration
            int progressBarValue = (int) ((secs / duration) * 100);

            //notifica o Controller sobre a atualização
            if (progressListener != null) {
                progressListener.onProgressUpdate(progressBarValue, formattedProgressText);
            }

        });
        progressTimer.start();
    }

    public double getDuration(){
        return duration;
    }

    public void setProgressListener(ProgressListener listener) {
        this.progressListener = listener;
    }

}