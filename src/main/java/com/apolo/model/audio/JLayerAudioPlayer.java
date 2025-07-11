package com.apolo.model.audio;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.Timer;

import com.apolo.model.exception.MusicException;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

/**
 * The `JLayerAudioPlayer` class represents a music player that can play audio files using the JavaLayer library.
 * It implements the `Runnable` interface to enable multithreading for playback.
 */
public class JLayerAudioPlayer {

    private boolean isPlaying = false; // Flag to indicate whether the player is currently playing
    private ChangeListener changeListener; // Listener for state change events
    private AdvancedPlayer player; // The player responsible for audio playback
    protected File file; // The audio file to be played
    private Timer frameTimer;

    private int currentFrame = 0; // Current frame position within the audio file
    private double duration;
    private String formatDuration;

    private ProgressListener progressListener;//bar and time

    private FileInputStream fileInputStream = null;
    private BufferedInputStream bufferedInputStream = null;


    public void seek(double timeInSeconds) throws MusicException {
        setPlaybackFrame(timeInSeconds);////update currentFrame

        System.out.println("fecha");
        closeStreams();

        //if (isPlaying) {
            System.out.println("Seeking to " + timeInSeconds + "s and resuming playback.");
            resume();
        //} else {
          //  System.out.println("Seeking to " + timeInSeconds + "s. Player remains paused.");
        //}
    }


    /**
     * Starts playback of the audio file.
     */
    public void play() throws MusicException {
        closeStreams();////
        openPlayerStreams();

        resetPlayback();
        playbackListener();

        System.out.println("Playback started!");
        isPlaying = true; //mark as playing
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

        System.out.println("Resumed playback");
        isPlaying = true;
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

            if (frameTimer != null) {
                frameTimer.stop();
            }

            isPlaying = false;

            fireStateChanged();
            closeStreams();////
            System.out.println("Playback is over!");
        }
    }


    /**
     * Stops playback of the audio file and resets the playback position to the beginning.
     */
    /*public void stop() {
        if (isPlaying || player != null) { //para se estiver tocando ou houver um player ativo
            System.out.println("Stopping playback...");
            if (player != null) {
                player.close();
            }
            //cleanupPlayer();
            //resetPlayback();
            System.out.println("Playback stopped");
        }
    }*/


    /**
     * Resets the frame counter, progress bar and progress label to zero.
     */
    public void resetPlayback() {
        currentFrame = 0;
        //notifica o Controller para atualizar a View
        if (progressListener != null) {
            progressListener.onProgressUpdate(0, "00:00");
        }
    }

    private void cleanupPlayer() {
        isPlaying = false;
        frameTimer.stop();
        closeStreams();
        fireStateChanged();
        System.out.println("Playback state cleaned up.");
    }

    //set up a playback listener to handle playback events
    private void playbackListener() {
        player.setPlayBackListener(new PlaybackListener() {
            @Override
            public void playbackFinished(PlaybackEvent evt) {
                //player.close();
                //closeStreams();
                //frameTimer.stop();
                resetPlayback();
                System.out.println("Playback complete!");
                //isPlaying = false;
                //fireStateChanged();
            }
        });
    }


    /**
     * Sets the audio file to be played by the music player.
     *
     * @param filePath The path to the audio file to be played.
     * @throws MusicException If the specified file path is null or if the file does not exist.
     */
    public void setMusic(String filePath) throws MusicException {
        //validate and set currentFile
        if (filePath == null) throw new MusicException("Select a song first", "Null Path");
        file = new File(filePath);
        if (!file.exists()) throw new MusicException("Song not found: " + filePath, "Path does not exist");

        duration = getMP3Duration(filePath);
        ////resetPlayback(); //reset current frame when new music is set
        ////closeStreamsAndPlayer(); //close any existing player/streams before setting new music
    }

    private void openPlayerStreams() throws MusicException {
        try { //create a new FileInputStream and wrap it in a BufferedInputStream
            fileInputStream = new FileInputStream(file);//create a FileInputStream and wrap it in a BufferedInputStream
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
        frameTimer = new Timer(1000, e -> {
            currentFrame += 45;

            double progressSeconds = (double) currentFrame / 45;

            int minutes = (int) (progressSeconds / 60);
            int seconds = (int) (progressSeconds % 60);
            String formattedProgressText = String.format("%02d:%02d", minutes, seconds);

            //calculate the progress as a percentage relative to the total duration
            double progressBarValue = (progressSeconds / duration) * 100;

            //notifica o Controller sobre a atualização
            if (progressListener != null) {
                progressListener.onProgressUpdate((int) progressBarValue, formattedProgressText);
            }
        });
        frameTimer.start();
    }


    /**
     * Retrieves the duration of an MP3 audio file in seconds and updates the progress label with the formatted duration.
     *
     * @param filePath The path to the MP3 audio file.
     * @return The duration of the audio file in seconds.
     */
    private double getMP3Duration(String filePath) {////////////////////////////////////////////
        try {
            AudioFile audioFile = AudioFileIO.read(new File(filePath));
            int trackLength = audioFile.getAudioHeader().getTrackLength();//track duration in seconds

            int minutes = trackLength / 60;
            int seconds = trackLength % 60;
            formatDuration = String.format("%02d:%02d", minutes, seconds);

            return trackLength;
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            e.printStackTrace();
        }
        return 0;
    }

    
    /**
     * Retrieves the formatted duration of the MP3 audio file.
     *
     * @return The formatted duration of the MP3 audio file as "mm:ss".
     */
    public String getFormatDuration() {
        return formatDuration;
    }

    public double getDuration(){
        return duration;
    }

    public void setProgressListener(ProgressListener listener) {
        this.progressListener = listener;
    }

    public void setPlaybackFrame(double newTime) {
        currentFrame = (int) (newTime * 45); //45 frames per second
    }

}