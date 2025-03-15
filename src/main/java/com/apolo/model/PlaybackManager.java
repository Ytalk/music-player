package com.apolo.model;

import com.apolo.gui.MusicException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

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
 * The `PlaybackManager` class represents a music player that can play audio files using the JavaLayer library.
 * It implements the `Runnable` interface to enable multithreading for playback.
 */
public class PlaybackManager implements Runnable {

    private boolean playing = false; // Flag to indicate whether the player is currently playing
    private ChangeListener changeListener; // Listener for state change events
    private AdvancedPlayer player; // The player responsible for audio playback
    protected File file; // The audio file to be played
    private Timer frameTimer;

    private int currentFrame = 0; // Current frame position within the audio file
    private double duration;
    private String formatDuration;

    private Thread playbackThread;
    private ProgressListener progressListener;


    public void setProgressListener(ProgressListener listener) {
        this.progressListener = listener;
    }

    public synchronized void startPlayback() {
        playbackThread = new Thread(this);
        playbackThread.start();
    }

    public synchronized void stopPlayback() {
        if(playbackThread != null && playbackThread.isAlive()) {
            playbackThread.interrupt();
            try {
                playbackThread.join(); //aguarda a thread finalizar
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); //mantém o estado de interrupção
            }
        }
    }

    /*public PlaybackManager(JProgressBar progressBar, JLabel progressLabel) {
        this.progressBar = progressBar;
        this.progressLabel = progressLabel;

        //listener para permitir arrastar a barra de progresso
        progressBar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                int mouseX = evt.getX();
                int progressBarVal = (int) Math.round(((double) mouseX / progressBar.getWidth()) * progressBar.getMaximum());

                progressBar.setValue(progressBarVal);

                double newProgress = (progressBarVal / 100.0) * duration;
                currentFrame = (int) (newProgress * 45); //45 frames por segundo

                if (isPlaying()) {
                    System.out.println("pausa, encerra thread e inicia uma nova...");
                    pausePlayback();
                    stopPlayback();
                    //frameTimer.stop();/////////////////
                    startPlayback();
                }

                progressBar.setValue(progressBarVal);
            }
        });

    }*/


    /**
     * Sets the audio file to be played by the music player.
     *
     * @param filePath The path to the audio file to be played.
     * @throws MusicException If the specified file path is null or if the file does not exist.
     */
    public void setMusic(String filePath) throws MusicException {
        //check if the file path is null
        if (filePath == null) {
            throw new MusicException("Select a song first", "Null path");
        }

        //create a File object from the provided file path
        file = new File(filePath);

        //check if the file exists
        if (!file.exists()) {
            throw new MusicException("Song not found: " + filePath, "Path does not exist");
        }

        duration = getMP3Duration(filePath);

        try {
            //create a FileInputStream and wrap it in a BufferedInputStream
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            //initialize the AdvancedPlayer with the BufferedInputStream
            this.player = new AdvancedPlayer(bufferedInputStream);
        } catch (JavaLayerException | IOException e) {
            e.printStackTrace();
        }
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
        return playing;
    }

    /**
     * Implementation of the Runnable interface's run() method.
     */
    @Override
    public void run() {
        if (currentFrame == 0) {
            playPlayback();
        } else {
            resumePlayback();
        }
    }

    /**
     * Starts playback of the audio file.
     */
    private void playPlayback() {
        try {
            //set up a playback listener to handle playback events
            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                player.close();
                frameTimer.stop();
                resetPlayback();

                System.out.println("Playback complete!");

                playing = false;
                fireStateChanged();
                }
            });

            System.out.println("Playback started!");

            playing = true; //mark as playing
            fireStateChanged(); //notify listeners of state change

            startProgressWatcher();
            player.play(); //start playback
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops playback of the audio file.
     */
    public void pausePlayback() {
        if (playing) {
            player.close();
            frameTimer.stop();

            playing = false;
            fireStateChanged();

            System.out.println("Playback is over!");
        }
    }

    /**
     * Resumes playback from the last paused position.
     */
    private void resumePlayback() {
        try {
            //create a new FileInputStream and wrap it in a BufferedInputStream
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            this.player = new AdvancedPlayer(bufferedInputStream);

            //set up a playback listener to handle playback events
            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                    player.close();
                    frameTimer.stop();
                    resetPlayback();

                    System.out.println("Playback complete!");

                    playing = false;
                    fireStateChanged();
                }
            });

            System.out.println("Resumed playback");

            playing = true;
            fireStateChanged();

            startProgressWatcher();
            player.play(currentFrame, Integer.MAX_VALUE);

        } catch (JavaLayerException | IOException e) {
            e.printStackTrace();
        }
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
            //progressLabel.setText( String.format("%02d:%02d", minutes, seconds) );////////////////////
            String formattedProgressText = String.format("%02d:%02d", minutes, seconds);

            //calculate the progress as a percentage relative to the total duration
            double progressBarValue = (progressSeconds / duration) * 100;
            //progressBar.setValue((int) progressBarValue);////////////////////////////

            //notifica o Controller sobre a atualização
            if (progressListener != null) {
                progressListener.onProgressUpdate((int) progressBarValue, formattedProgressText);
            }
        });
        frameTimer.start();
    }


    /**
     * Resets the frame counter, progress bar and progress label to zero.
     */
    /*public void resetPlayback() {
        currentFrame = 0;
        progressBar.setValue(0);
        progressLabel.setText("00:00");
    }*/
    public void resetPlayback() {
        currentFrame = 0;

        //notifica o Controller para atualizar a View
        if (progressListener != null) {
            progressListener.onProgressUpdate(0, "00:00");
        }
    }


    /**
     * Retrieves the duration of an MP3 audio file in seconds and updates the progress label with the formatted duration.
     *
     * @param filePath The path to the MP3 audio file.
     * @return The duration of the audio file in seconds.
     */
    private double getMP3Duration(String filePath) {
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

}