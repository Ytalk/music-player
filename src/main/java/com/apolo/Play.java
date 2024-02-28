package com.apolo;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.images.Artwork;
import java.io.IOException;


/**
 * The `Play` class represents a music player that can play audio files using the JavaLayer library.
 * It implements the `Runnable` interface to enable multithreading for playback.
 */
public class Play implements Runnable {

    private boolean playing = false; // Flag to indicate whether the player is currently playing
    private ChangeListener changeListener; // Listener for state change events
    private AdvancedPlayer player; // The player responsible for audio playback
    private File file; // The audio file to be played
    private int currentFrame = 0; // Current frame position within the audio file
    private JProgressBar progressBar; // Adiciona uma barra de progresso
    private double duration;
    private String formatDuration;
    private JLabel progressLabel;

    public Play(JProgressBar progressBar, JLabel progressLabel) {//mudar nome da classe
        this.progressBar = progressBar;
        this.progressLabel = progressLabel;
    }


    /**
     * Sets the audio file to be played by the music player.
     *
     * @param filePath The path to the audio file to be played.
     * @throws musicException If the specified file path is null or if the file does not exist.
     */
    public void setMusic(String filePath) throws musicException {
        // Check if the file path is null
        if (filePath == null) {
            throw new musicException("Select a song first", "Null path");
        }

        // Create a File object from the provided file path
        file = new File(filePath);

        // Check if the file exists
        if (!file.exists()) {
            throw new musicException("Song not found: " + filePath, "Path does not exist");
        }

        duration = getMP3Duration(filePath);

        try {
            // Create a FileInputStream and wrap it in a BufferedInputStream
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            // Initialize the AdvancedPlayer with the BufferedInputStream
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
            play();
        } else {
            resume();
        }
    }

    /**
     * Starts playback of the audio file.
     */
    private void play() {
        try {
            // Set up a playback listener to handle playback events
            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                System.out.println("Playback complete!");
                player.close();
                setFrame();

                playing = false;
                fireStateChanged();
                }
            });

            System.out.println("Playback started!");

            playing = true; // Mark as playing
            fireStateChanged(); // Notify listeners of state change

            startFrameCounter(); // Start counting frames
            player.play(); // Start playback
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops playback of the audio file.
     */
    public void stop() {
        if (playing) {
            player.close();
            System.out.println("Playback is over!");

            playing = false;
            fireStateChanged();
        }
    }

    /**
     * Resumes playback from the last paused position.
     */
    private void resume() {
        try {
            // Create a new FileInputStream and wrap it in a BufferedInputStream
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            this.player = new AdvancedPlayer(bufferedInputStream);

            // Set up a playback listener to handle playback events
            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                    System.out.println("Playback complete!");
                    player.close();
                    setFrame();

                    playing = false;
                    fireStateChanged();
                }
            });

            System.out.println("Resumed playback");

            playing = true;
            fireStateChanged();

            startFrameCounter();
            player.play(currentFrame, Integer.MAX_VALUE);

        } catch (JavaLayerException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts a thread to count frames during playback.
     */
    private void startFrameCounter() {
        Thread frameCounterThread = new Thread(() -> {
            while (playing) {
                try {
                    Thread.sleep(25);
                    currentFrame++;
                    updateProgressBar();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        frameCounterThread.start();
    }

    /**
     * Resets the frame counter to zero.
     */
    public void setFrame() {
        currentFrame = 0;
        progressBar.setValue(0);
    }

    private void updateProgressBar() {
        if (player != null) {
            // Calcula o progresso atual da reprodução em segundos
            double progressSeconds = (double) currentFrame * 0.025;

            int minutes = (int) (progressSeconds / 60); // Calcula os minutos
            int seconds = (int) (progressSeconds % 60); // Calcula os segundos restantes
            progressLabel.setText( String.format("%02d:%02d", minutes, seconds) ); // Formata os minutos e segundos

            // Calcula o progresso em relação à duração total
            double progress = (progressSeconds / duration) * 1.51;

            // Atualiza o valor da barra de progresso
            progressBar.setValue((int) progress);
        }
    }

    private double getMP3Duration(String filePath) {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(filePath));
            int trackLength = audioFile.getAudioHeader().getTrackLength(); // Duração da faixa em segundos

            long milliseconds = trackLength * 1000; // Convertendo segundos para milissegundos
            long minutes = milliseconds / (1000 * 60);
            long seconds = (milliseconds / 1000) % 60;

            formatDuration = String.format("%02d:%02d", minutes, seconds);

            return minutes;
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            e.printStackTrace();
        }
        return -1; // Retorna -1 se houver algum erro
    }

    public String getFormatDuration() {
        return formatDuration;
    }

}