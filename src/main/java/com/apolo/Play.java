package com.apolo;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.io.File;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Play implements Runnable{

    private boolean playing = false;
    private ChangeListener changeListener;
    private AdvancedPlayer player;
    private int pausedOnFrame = 0;



    //NO CONSTRUCTOR

    public void setMusic(String filePath) throws musicException{
        if (filePath == null) {
            throw new musicException("select a song first", "null path");
        }

        // Cria um objeto File com o caminho fornecido
        File file = new File(filePath);

        if (!file.exists()) {
            throw new musicException("Song not found: " + filePath, "Path does not exist");
        }

        try{
            // Cria um FileInputStream a partir do caminho absoluto
            FileInputStream inputStream = new FileInputStream(file);

            // AdvancedPlayer para reproduzir o arquivo MP3
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
                    System.out.println("Reprodução concluída!");
                    player.close();

                    playing = false;
                    fireStateChanged();
                }
            });

            System.out.println("Reprodução iniciada!");

            playing = true; // Marca como reproduzindo
            fireStateChanged(); // Notifica ouvintes sobre a mudança de estado

            player.play();
        }
        catch (JavaLayerException e) {
            e.printStackTrace();
        }

    }


    public void stop() {
        if (playing) {
            player.close();
            System.out.println("Reprodução encerrada!");

            playing = false;
            fireStateChanged();
        }
    }


}
