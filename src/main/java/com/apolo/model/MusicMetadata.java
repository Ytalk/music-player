package com.apolo.model;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.images.Artwork;

import javax.swing.ImageIcon;

import java.awt.Image;

import java.io.IOException;
import java.io.File;

public class MusicMetadata {
    private String filePath;

    public MusicMetadata(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Retrieves the formatted duration of an audio file for display in ApoloTaggerListCellRenderer.
     *
     * @param filePath The path to the audio file.
     * @return The formatted duration of the audio file in "mm:ss" format.
     */
    public String getMP3Duration() {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(filePath));
            int trackLength = audioFile.getAudioHeader().getTrackLength();//get the track length in seconds
            int minutes = trackLength / 60;
            int seconds = trackLength % 60;
            return String.format("%02d:%02d", minutes, seconds);
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            e.printStackTrace();
        }
        return "00:00";
        //return null;
    }


    /**
     * Retrieves the title of an audio file.
     *
     * @param filePath The path to the audio file.
     * @return The title of the audio file.
     */
    public String getMP3Title() {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(filePath));
            Tag tag = audioFile.getTag();
            if (tag != null) {
                return tag.getFirst(FieldKey.TITLE);
            }
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            e.printStackTrace();
        }
        return new File(filePath).getName().replaceFirst("[.][^.]+$", "");
        //return null;
    }


    /**
     * Retrieves the album artwork of an MP3 audio file.
     *
     * @param filePath The path to the MP3 audio file.
     * @return The album artwork as an ImageIcon, scaled to 30x30 pixels.
     */
    public ImageIcon getMP3AlbumArtwork() {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(filePath));
            Tag tag = audioFile.getTag();
            if (tag != null) {
                Artwork artwork = tag.getFirstArtwork();
                if (artwork != null) {
                    byte[] imageData = artwork.getBinaryData();//retrieve the binary data of the artwork
                    if (imageData != null) {
                        //create an ImageIcon from the image data and scale it to 30x30 pixels
                        return new ImageIcon(new ImageIcon(imageData).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
