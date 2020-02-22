package com.aaroncarsonart.quickgame2.map;

import java.awt.Color;

public enum ColorSet {
    BROWN(Colors.DARK_BROWN, Colors.BROWN),
    RED(Colors.DARK_RED, Color.RED),
    PURPLE(Colors.DARK_PURPLE, Colors.PURPLE),
    BLUE(Colors.DARK_BLUE, Color.BLUE),
    GREEN(Colors.DARK_GREEN, Color.GREEN),
    YELLOW(Colors.DARK_YELLOW, Color.YELLOW);

    public final Color bg;
    public final Color fg;

    private ColorSet(Color bg, Color fg) {
        this.bg = bg;
        this.fg = fg;
    }

    static class Colors {
        public static final Color BROWN = new Color(165, 82, 0);
        public static final Color DARK_BROWN = new Color(50, 15, 0);

        public static final Color DARK_RED = new Color(40, 0, 0);
        public static final Color PURPLE = Color.MAGENTA; // new Color(128, 0, 128);
        public static final Color DARK_PURPLE = new Color(40, 0, 40);
        public static final Color DARK_BLUE = new Color(0, 0, 50);
        public static final Color DARK_GREEN = new Color(0, 40, 0);
        public static final Color DARK_YELLOW = new Color(40, 40, 0);

    }

}
