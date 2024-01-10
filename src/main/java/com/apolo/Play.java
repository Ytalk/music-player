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
    private Bitstream bitstream;
    private int pausedOnFrame = 0;

    public Play(String filePath) {
        try {
            // URL do arquivo usando o class loader
            this.url = Play.class.getClassLoader().getResource(filePath);

            if (this.url == null) {
                throw new IOException("Arquivo não encontrado: " + filePath);
            }

            // FileInputStream a partir da URL
            this.fileInputStream = new FileInputStream(url.getFile());

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

            System.out.println("Reprodução iniciada!");

            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent event) {
                    pausedOnFrame = event.getFrame();
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


    public void resumePlayback( int currentFrame ) {
        try {
            // Cria um novo FileInputStream a partir da URL
            fileInputStream = new FileInputStream(url.getFile());

            // Cria um novo AdvancedPlayer
            player = new AdvancedPlayer(fileInputStream);

            // Configura o listener para acompanhar o progresso da reprodução
            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                    System.out.println("Reprodução concluída");
                }
            });

            // Move para a posição pausada e inicia a reprodução
            player.play( pausedOnFrame, Integer.MAX_VALUE);

            System.out.println("Reprodução retomada");

        } catch (JavaLayerException | IOException e) {
            e.printStackTrace();
        }
    }


}
