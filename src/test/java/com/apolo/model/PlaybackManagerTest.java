package com.apolo.model;

import static org.junit.jupiter.api.Assertions.*;

import com.apolo.model.audio.JLayerAudioPlayer;
import com.apolo.model.exception.MusicException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.apolo.view.MusicException;

public class PlaybackManagerTest {

    @InjectMocks
    private JLayerAudioPlayer playbackManager;

    @BeforeEach
    public void setUp() {
        // Inicializa os mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSetMusic() throws Exception {
        String filePath = "C:\\Users\\Dérick\\Music\\VMZ - Tô Indo Embora _ Lyric Vídeo(MP3_70K).mp3";

        playbackManager.setMusic(filePath);
        assertNotNull(playbackManager.file);

        assertThrows(MusicException.class, () -> playbackManager.setMusic(null));
    }

    @Test
    public void testRun() {
        playbackManager.setMusic("C:/Users/Dérick/Music/VMZ - Tô Indo Embora _ Lyric Vídeo(MP3_70K).mp3");
        Thread thread = new Thread(playbackManager);

        assertFalse(playbackManager.isPlaying());
        thread.start();

        //assertTrue(playbackManager.isPlaying());

        assertFalse(playbackManager.isPlaying());
    }

}