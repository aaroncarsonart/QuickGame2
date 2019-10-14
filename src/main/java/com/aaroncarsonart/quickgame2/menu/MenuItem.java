package com.aaroncarsonart.quickgame2.menu;

public class MenuItem {

    String label;
    Callback callback;

    public MenuItem(String label, Callback callback) {
        this.label = label;
        this.callback = callback;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
}
