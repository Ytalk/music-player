package com.apolo;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class Main {

    public static void main(String[] args) {

        String filePath = "mp3/Purple.mp3";

        try {
            //URL do arquivo usando o class loader
            URL url = Main.class.getClassLoader().getResource(filePath);

            if (url == null) {
                throw new IOException("Arquivo não encontrado: " + filePath);
            }

            //FileInputStream a partir da URL
            FileInputStream fileInputStream = new FileInputStream(url.getFile());

            //Bitstream para decodificar o arquivo MP3
            Bitstream bitstream = new Bitstream(fileInputStream);

            //AdvancedPlayer para reproduzir o arquivo MP3
            AdvancedPlayer player = new AdvancedPlayer(fileInputStream);

            //PlaybackListener para imprimir o progresso da reprodução
            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                    System.out.println("Reprodução concluída!");
                    player.close();
                    System.exit(0);
                }
            });


            player.play();


        }
        catch (JavaLayerException | IOException e) {
            e.printStackTrace();
        }

    }
}
