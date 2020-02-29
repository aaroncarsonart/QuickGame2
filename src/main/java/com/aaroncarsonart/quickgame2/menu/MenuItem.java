package com.aaroncarsonart.quickgame2.menu;

import java.awt.Color;
import java.util.function.Supplier;

public class MenuItem {

    Supplier<String> label;
    Callback callback;
    Color color;

    public MenuItem(String label, Callback callback) {
        this.label = () -> label;
        this.callback = callback;
        this.color = Color.WHITE;
    }
    public MenuItem(String label, Color color, Callback callback) {
        this.label = () -> label;
        this.callback = callback;
        this.color = color;
    }

    public MenuItem(Supplier<String> label, Callback callback) {
        this.label = label;
        this.callback = callback;
        this.color = Color.WHITE;
    }


    public String getLabel() {
        return label.get();
    }

    public void setLabel(Supplier<String> label) {
        this.label = label;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
