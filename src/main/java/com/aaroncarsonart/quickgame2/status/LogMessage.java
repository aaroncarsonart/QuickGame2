package com.aaroncarsonart.quickgame2.status;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class LogMessage {

    List<ColoredString> message;

    public LogMessage() {
        message = new ArrayList<>();
    }

    /**
     * Create a basic status message with default colors.
     * @param message The message to use.
     */
    public LogMessage(String message) {
        this();
        append(new ColoredString(message, Color.WHITE));
    }

    public LogMessage(String message, Color color) {
        this();
        append(new ColoredString(message, color));
    }

    public List<ColoredString> getMessage() {
        return message;
    }

    public void setMessage(List<ColoredString> message) {
        this.message = message;
    }

    public void append(String string) {
        message.add(new ColoredString(string, Color.WHITE));
    }

    public void append(String string, Color color) {
        message.add(new ColoredString(string, color));
    }

    public void append(ColoredString coloredString) {
        message.add(coloredString);
    }
}
