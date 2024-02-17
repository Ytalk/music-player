package com.apolo;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Play implements Runnable{

    private boolean playing = false;
    private ChangeListener changeListener;
    private AdvancedPlayer player;
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
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            this.player = new AdvancedPlayer(bufferedInputStream);
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
        if(currentFrame == 0){
            play();
        }
        else{
            resume();
        }
    }


    private void play() {

        try {
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

            playing = true; //marca como reproduzindo
            fireStateChanged(); //notifica ouvintes sobre a mudança de estado

            startFrameCounter();
            player.play();
        }
        catch (JavaLayerException e) {
            e.printStackTrace();
        }

    }


    public void stop() {
        if (playing) {
            player.close();
            System.out.println("Playback is over!");

            playing = false;
            fireStateChanged();
        }
    }


    private void resume() {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            this.player = new AdvancedPlayer(bufferedInputStream);

            //listener para acompanhar o progresso da reprodução
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

            System.out.println("Reprodução retomada");

            playing = true;
            fireStateChanged();

            startFrameCounter();
            player.play( currentFrame, Integer.MAX_VALUE);

        } catch (JavaLayerException | IOException e) {
            e.printStackTrace();
        }
    }


    private void startFrameCounter() {
        Thread frameCounterThread = new Thread(() -> {
            while (playing) {
                try {
                    Thread.sleep(25);
                    currentFrame++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        frameCounterThread.start();
    }


    public void setFrame(){
        currentFrame = 0;
    }
}
