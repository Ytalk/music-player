package com.apolo;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Play implements Runnable{

    private boolean playing = false;
    private ChangeListener changeListener;
    private AdvancedPlayer player;
    private int pausedOnFrame = 1500;
    private File file;
    private int currentFrame = 0;


    //NO CONSTRUCTOR

    public void setMusic(String filePath) throws musicException{
        if (filePath == null) {
            throw new musicException("select a song first", "null path");
        }

        file = new File(filePath);

        if (!file.exists()) {
            throw new musicException("Song not found: " + filePath, "Path does not exist");
        }

        try{
            FileInputStream inputStream = new FileInputStream(file);
            this.player = new AdvancedPlayer(inputStream);
        }
        catch (JavaLayerException | IOException e) {
            e.printStackTrace();
        }
    }


    public void addChangeListener(ChangeListener listener) {//primeiro aqui
        changeListener = listener;
    }

    private void fireStateChanged() {//depois aqui
        if (changeListener != null) {
            changeListener.stateChanged(new ChangeEvent(this));
        }
    }


    public boolean isPlaying(){
        return playing;
    }


    @Override
    public void run() {
        play();
    }


    public void play() {

        try {
            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                    System.out.println("Playback complete!");
                    player.close();

                    playing = false;
                    fireStateChanged();
                }
            });

            System.out.println("Playback started!");

            playing = true; //marca como reproduzindo
            fireStateChanged(); //notifica ouvintes sobre a mudança de estado

            Thread frameCounterThread = new Thread(() -> {
                while (playing) {
                    try {
                        Thread.sleep(10);
                        currentFrame++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            frameCounterThread.start();

            player.play();
        }
        catch (JavaLayerException e) {
            e.printStackTrace();
        }

    }


    public void stop() {
        if (playing) {
            player.close();
            System.out.println("Playback is over!" + currentFrame);

            playing = false;
            fireStateChanged();
        }
    }


    /*public void resumePlayback() {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            player = new AdvancedPlayer(fileInputStream);

            //listener para acompanhar o progresso da reprodução
            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                    System.out.println("Reprodução concluída");
                }
            });
            //inicia a reprodução posição pausada
            player.play( pausedOnFrame, Integer.MAX_VALUE);
            System.out.println("Reprodução retomada");
        } catch (JavaLayerException | IOException e) {
            e.printStackTrace();
        }
    }*/


}
