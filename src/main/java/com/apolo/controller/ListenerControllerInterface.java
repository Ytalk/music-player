package com.apolo.controller;

import com.apolo.model.PlaybackManager;
import com.apolo.model.Playlist;
import com.apolo.model.PlaylistManager;

import javax.swing.*;

public interface ListenerControllerInterface {

    //public void getPlaylists();
    //public void getMusicDirectory();
    public PlaybackManager getMusic();
    public JPanel getPlaylistPanel();
    public PlaylistManager getPlaylistManager();

    public JProgressBar getProgressBar();
    public JLabel getProgressLabel();


    public ImageIcon getIcon(String pathIcon, int width, int height);
    public JButton formatButton( ImageIcon icon, int RGB, boolean bool );

    public void addMisicByButton();
    public void delMusic();
    //public void addMusicByDropTarget();
    public void playButton(JLabel durationLabel);
    public void skipMusic(int nextOrPrevious, JLabel durationLabel);
    public void toggleRepeatState(JButton repeatButton);
    public void handleMusicChange(JButton playButton, JLabel durationLabel);
    //private void handlePlaybackState(Playlist selectedPlaylist);



}
