package com.apolo.model.util;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;

import javax.swing.ImageIcon;

import java.awt.Image;

import java.io.IOException;
import java.io.File;

/**
 * A utility class for reading metadata from audio files using the JAudioTagger library.
 * This class provides static methods to extract information such as duration, title,
 * and album cover art from various audio file formats.
 * It is marked as final because it is a utility class not intended for subclassing.
 */
public final class AudioMetadataReader {
    private static final int COVER_SIZE = 25;

    /**
     * Retrieves the formatted duration of an audio file for display.
     *
     * @param filePath The path to the audio file.
     * @return The formatted duration of the audio file in "mm:ss" format, or {@code null} if unreadable.
     */
    public static String getFormattedDuration(String filePath) {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(filePath));
            int trackLength = audioFile.getAudioHeader().getTrackLength();//track length in seconds
            int minutes = trackLength / 60;
            int seconds = trackLength % 60;
            return String.format("%02d:%02d", minutes, seconds);
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            return null;
        }
    }

    /**
     * Retrieves the duration of an audio file in seconds.
     *
     * @param filePath The path to the audio file.
     * @return The duration of the audio file in seconds, or 0 if an error occurs..
     */
    public static double getDuration(String filePath) {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(filePath));
            return audioFile.getAudioHeader().getTrackLength();
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * Retrieves the title of an audio file.
     * It first attempts to read the title from the audio file's tags. If no title
     * is found in the tags, it falls back to using the file name (without extension).
     *
     * @param filePath The path to the audio file.
     * @return The title of the audio file (from tags or file name).
     */
    public static String getTitle(String filePath) {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(filePath));
            Tag tag = audioFile.getTag();
            if (tag != null) {
                String title = tag.getFirst(FieldKey.TITLE);
                if (!title.isEmpty()) return title;
            }
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            e.printStackTrace();
        }
        return new File(filePath).getName().replaceFirst("\\.[^.]+$", "");
    }


    /**
     * Retrieves the album artwork of an audio file {@link ImageIcon}.
     * If album art is found, it is scaled to the {@link #COVER_SIZE}.
     * Returns {@code null} if no artwork is found or an error occurs during retrieval.
     *
     * @param filePath The path to the audio file.
     * @return The album artwork as an {@link ImageIcon} scaled to {@link #COVER_SIZE}x{@link #COVER_SIZE} pixels,
     * or {@code null} if no artwork is present or an error occurs.
     */
    public static ImageIcon getCoverArt(String filePath) {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(filePath));
            Tag tag = audioFile.getTag();
            if (tag != null && tag.getFirstArtwork() != null) {
                byte[] imageData = tag.getFirstArtwork().getBinaryData();//retrieve the binary data of the artwork
                //create an ImageIcon from the image data and scale it
                return new ImageIcon( new ImageIcon(imageData).getImage().getScaledInstance(COVER_SIZE, COVER_SIZE, Image.SCALE_SMOOTH) );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}