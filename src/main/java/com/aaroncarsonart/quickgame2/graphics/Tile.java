package com.aaroncarsonart.quickgame2.graphics;

import java.awt.Color;

public class Tile {
    private char c;
    private Color bg;
    private Color fg;

    public Tile(char c, Color bg, Color fg) {
        this.c = c;
        this.bg = bg;
        this.fg = fg;
    }

    public char getC() {
        return c;
    }

    public void setC(char c) {
        this.c = c;
    }

    public Color getBg() {
        return bg;
    }

    public void setBg(Color bg) {
        this.bg = bg;
    }

    public Color getFg() {
        return fg;
    }

    public void setFg(Color fg) {
        this.fg = fg;
    }
}
