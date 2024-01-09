package com.apolo;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class Play implements Runnable{

    private AdvancedPlayer player;
    private FileInputStream fileInputStream;
    private URL url;

    public Play(String filePath) {
        try {
            // URL do arquivo usando o class loader
            this.url = Play.class.getClassLoader().getResource(filePath);

            if (this.url == null) {
                throw new IOException("Arquivo não encontrado: " + filePath);
            }

            // FileInputStream a partir da URL
            this.fileInputStream = new FileInputStream(url.getFile());

            // Bitstream para decodificar o arquivo MP3
            Bitstream bitstream = new Bitstream(fileInputStream);

            // AdvancedPlayer para reproduzir o arquivo MP3
            this.player = new AdvancedPlayer(fileInputStream);

        }
        catch (JavaLayerException | IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        play();
    }


    public void play() {

        try {
            //progresso da reprodução
            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                    System.out.println("Reprodução concluída!");
                    player.close();
                }
            });

            player.play();

        }
        catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }


    public void pausePlayback() {
        if (player != null) {
            player.close();
            System.out.println("Reprodução pausada");
        }
    }


    public void resumePlayback() {

        System.out.println("Reprodução retomada (não implementado)");

    }


}
