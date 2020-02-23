package com.aaroncarsonart.quickgame2.status;

import java.awt.Color;

public class ColoredString {

    private String message;
    private Color color;

    public ColoredString(String message, Color color) {
        this.message = message;
        this.color = color;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
