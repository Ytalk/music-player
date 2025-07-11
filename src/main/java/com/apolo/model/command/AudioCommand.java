package com.apolo.model.command;

public class AudioCommand {
    private final AudioCommandType type;
    private final Object data;//filePath, newFrame or null

    public AudioCommand(AudioCommandType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public AudioCommandType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}