package com.apolo.model.audio;

import com.apolo.model.command.AudioCommand;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioCommandExecutor {
    private final Thread commandLoopThread;
    private final BlockingQueue<AudioCommand> commandQueue;
    private volatile boolean running;
    private final JLayerAudioPlayer audioPlayer;

    public AudioCommandExecutor(JLayerAudioPlayer audioPlayer){
        this.audioPlayer = audioPlayer;
        commandQueue = new LinkedBlockingQueue<>();
        running = true;

        commandLoopThread = new Thread(this::commandLoop, "AudioCommandLoopThread");//only 'this' for runnable
        commandLoopThread.setDaemon(true);
        commandLoopThread.start();
    }

    public void enqueueCommand(AudioCommand cmd) {
        commandQueue.offer(cmd);
    }


    private void commandLoop() {
        while (running) {
            try {
                AudioCommand cmd = commandQueue.take();
                switch (cmd.getType()) {
                    case LOAD_FILE:
                        System.out.println("load");
                        //extrai caminho, faz setMusic internamente, reseta estado
                        audioPlayer.setMusic((String) cmd.getData());
                        break;

                    case PLAY://set file and play
                        System.out.println("play");
                        audioPlayer.play();
                        break;

                    case RESUME:
                        System.out.println("resume");
                        audioPlayer.resume();
                        break;

                    case PAUSE:
                        System.out.println("pause");
                        audioPlayer.pause();
                        break;

                    case SEEK://resume
                        System.out.println("seek");
                        audioPlayer.seek((double) cmd.getData());
                        break;

                    /*case STOP:
                        player.handleStop();
                        break;

                    case EXIT:
                        // limpa recursos e interrompe loop
                        player.cleanupPlayer();
                        running = false;
                        break;*/
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}