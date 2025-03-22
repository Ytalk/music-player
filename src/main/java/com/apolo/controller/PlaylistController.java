package com.apolo.controller;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import com.apolo.gui.ApoloPopUp;
import com.apolo.model.MusicException;
import com.apolo.model.Playlist;
import com.apolo.model.PlaylistManager;

import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DnDConstants;
import java.awt.datatransfer.DataFlavor;

public class PlaylistController {
    private PlaylistManager playlistManager;
    private Playlist playlist;

    public PlaylistManager getPlaylistManager() {
        return playlistManager;
    }


    private boolean noPlaylistsByte = false;

    public void getPlaylists() {
        playlistManager = new PlaylistManager();
        playlistManager.loadFile();////////////////////////////////////////////
        if (playlistManager.getManager() != null) {
            playlistManager = playlistManager.getManager();
        }
        else{
            noPlaylistsByte = true;
        }
    }

    public void getMusicDirectory() {
        if (noPlaylistsByte) {
            File musicDirectoryPT = new File(System.getProperty("user.home") + "/Músicas");
            File musicDirectory = new File(System.getProperty("user.home") + "/Music");

            if ( !( musicDirectory.exists() || musicDirectoryPT.exists() ) ) {
                System.out.println("Default music folder not found.");
                return;
            }

            File[] files;
            //all files in the music folder
            if (musicDirectory.exists()){
                files = musicDirectory.listFiles();
            } else {
                files = musicDirectoryPT.listFiles();
            }

            //store the .mp3 files
            List<File> mp3Files = new ArrayList<>();

            if (files != null) {
                for (File file : files) {
                    //checks if it is an .mp3 file
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".mp3")) {
                        mp3Files.add(file);
                    }
                }
            }

            if (mp3Files.isEmpty()) {
                System.out.println("No .mp3 files found in music folder.");
                return;
            }

            Playlist playlist = new Playlist("Music");
            playlistManager.getMap().put("Music", playlist);
            playlistManager.getMainList().setListData(playlistManager.getMap().keySet().toArray(new String[0]));
            playlistManager.getMainList().setSelectedIndex(0);
            playlistManager.getPlaylistPanel().add(playlist.getPlaylist(), "Music");

            for (File mp3File : mp3Files) {
                playlist.getListModel().addElement(mp3File.getAbsolutePath());
            }
        }
    }


    public Playlist getSelectedPlaylist() {
        String selectedPlaylistName = (String) playlistManager.getMainList().getSelectedValue();///////////////////
        Playlist selectedPlaylist = playlistManager.getMap().get(selectedPlaylistName);///////////////
        return selectedPlaylist;
    }


    public void addMisicByButton(){
        try {
            String selectedPlaylistName = (String) playlistManager.getMainList().getSelectedValue();//////////////

            if (selectedPlaylistName == null)
                throw new MusicException("Select a playlist before adding a song!", "Null Playlist");

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setDialogTitle( "Specify files to add (press Ctrl for multi-selection)" );
            fileChooser.setFileFilter( new FileNameExtensionFilter("MP3 Files", "mp3") );

            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File[] selectedFiles = fileChooser.getSelectedFiles();
                playlist = playlistManager.getMap().get(selectedPlaylistName);///////////////////

                for (File file : selectedFiles) {
                    playlist.getListModel().addElement( file.getAbsolutePath() );
                }

                playlistManager.saveToFile(playlistManager);//////////////////////////
            }

        } catch (MusicException ex) {
            new ApoloPopUp().showWarning(ex.getMessage(), ex.getErrorName());
        }
    }


    public void delMusic(){
        String selectedPlaylistName = (String) playlistManager.getMainList().getSelectedValue();////////////////

        try {
            if (selectedPlaylistName == null)
                throw new MusicException("Select a playlist before deleting a song!", "Null Playlist");

            playlist = playlistManager.getMap().get(selectedPlaylistName);////////////////////////////
            int selectedMusicPathIndex = playlist.getMp3List().getSelectedIndex();
            if ( selectedMusicPathIndex == -1 )
                throw new MusicException("Select a song before deleting it!", "Null Music");

            int confirmSongDel = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected music?", "Confirm Song Deletion", JOptionPane.YES_NO_OPTION);
            if (confirmSongDel == JOptionPane.YES_OPTION) {
                playlist.getListModel().remove(selectedMusicPathIndex);
                playlistManager.saveToFile(playlistManager);////////////////////////////////////////
            }
        }
        catch (MusicException ex){
            new ApoloPopUp().showWarning(ex.getMessage(), ex.getErrorName());
        }
    }


    public DropTarget addMusicByDropTarget(){
        DropTarget addMusicDropTarget = new DropTarget(playlistManager.getPlaylistPanel(), new DropTargetAdapter() {

            public void drop(DropTargetDropEvent dtde) {
                try {
                    String selectedPlaylistName = (String) playlistManager.getMainList().getSelectedValue();

                    if (selectedPlaylistName == null)
                        throw new MusicException("Select a playlist before adding a song!", "Null Playlist");

                    //check if the dropped data is a list of files
                    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);
                        List<File> files = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                        playlist = playlistManager.getMap().get(selectedPlaylistName);

                        for (File file : files) {
                            playlist.getListModel().addElement( file.getAbsolutePath() );
                        }
                        dtde.dropComplete(true);
                        playlistManager.saveToFile(playlistManager);/////////////////////
                    } else {
                        dtde.rejectDrop();
                    }
                }
                catch (MusicException ex) {
                    new ApoloPopUp().showWarning(ex.getMessage(), ex.getErrorName());
                    dtde.dropComplete(false);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    dtde.dropComplete(false);
                }
            }

        });

        return addMusicDropTarget;
    }

}
