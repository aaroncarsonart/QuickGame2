package com.aaroncarsonart.quickgame2;

import com.sun.prism.image.ViewPort;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;

public class CharGrid {

    public int width;
    public int height;

    private JFrame jFrame;
    private Container container;
    private CharPanel[][] textGrid;

    public CharGrid(int height, int width) {
        jFrame = new JFrame("QuickGame");
        jFrame.setFocusTraversalKeysEnabled(false);
        container = jFrame.getContentPane();
        container.setBackground(Color.BLACK);
        container.setLayout(new GridLayout(height, width));

        textGrid = new CharPanel[height][width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                CharPanel textArea = new CharPanel();
                textArea.setChar(' ');
                textArea.setForeground(Color.BLACK);
                textArea.setForeground(Color.DARK_GRAY);
                textGrid[y][x] = textArea;
                container.add(textArea.getJPanel());
            }
        }
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setBackground(Color color, int y, int x) {
        CharPanel panel = textGrid[y][x];
        panel.setBackground(color);
    }

    public void setForeground(Color color, int y, int x) {
        CharPanel panel = textGrid[y][x];
        panel.setForeground(color);
    }

    public void setColors(Color bg, Color fg, int y, int x) {
        CharPanel panel = textGrid[y][x];
        panel.setBackground(bg);
        panel.setForeground(fg);
    }

    public void setChar(char c, int y, int x) {
        CharPanel panel = textGrid[y][x];
        panel.setChar(c);
    }

    public void setText(String s, int y, int x) {
        CharPanel panel = textGrid[y][x];
        panel.setText(s);
    }

    public CharPanel getTilePanel(int y, int x) {
        return textGrid[y][x];
    }


    public void drawText(String text, Color bg, Color fg, int y, int x) {
        for (int i = 0; i < text.length(); i++) {
            CharPanel playerTextArea = textGrid[y][x + i];
            playerTextArea.setText(String.valueOf(text.charAt(i)));
            playerTextArea.setBackground(bg);
            playerTextArea.setForeground(fg);
        }
    }


    public void drawTextLeft(String text, Color fgColor, int y, int x) {
        for (int i = 0; i < text.length(); i++) {
            CharPanel playerTextArea = textGrid[y][x + i];
            playerTextArea.setText(String.valueOf(text.charAt(i)));
            playerTextArea.setForeground(fgColor);
        }
    }

    public void drawText(String text, Color fgColor, int y, int x) {
        drawTextLeft(text, fgColor, y, x);
    }

    public void drawText(String text, int y, int x) {
        drawTextLeft(text, Color.WHITE, y, x);
    }

    public JFrame getJFrame() {
        return jFrame;
    }

    public void show() {
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(false);
        jFrame.setVisible(true);
        jFrame.requestFocusInWindow();
    }

    public void drawBoxCorners(int vy, int vx, int vh, int vw) {
        Color bg = Color.BLACK;
        Color fg = Color.DARK_GRAY;

        int x, y;
        char c;

        c = '╔';
        y = vy;
        x = vx;
        setChar(c, y, x);
        setBackground(bg, y, x);
        setForeground(fg, y, x);

        c = '╗';
        y = vy;
        x = vx + vw;
        setChar(c, y, x);
        setBackground(bg, y, x);
        setForeground(fg, y, x);

        c = '╚';
        y = vy + vh;
        x = vx;
        setChar(c, y, x);
        setBackground(bg, y, x);
        setForeground(fg, y, x);

        c = '╝';
        y = vy + vh;
        x = vx + vw;
        setChar(c, y, x);
        setBackground(bg, y, x);
        setForeground(fg, y, x);
    }

    public void drawBoxBorders(int vy, int vx, int vh, int vw) {
        Color bg = Color.BLACK;
        Color fg = Color.DARK_GRAY;

        String singleLines = "│─┌┐└┘";
        String doubleLines = "║═╔╗╚╝ ";

        int startOffset = 1;
        int endOffset = 0;

        // draw top horizontal line
        for (int x = vx + startOffset; x < vx + vw - endOffset; x++) {
            int y = vy;
            this.setChar('─', y, x);
            this.setBackground(bg, y, x);
            this.setForeground(fg, y, x);
        }

        for (int x = vx + startOffset; x < vx + vw - endOffset; x++) {
            int y = vy + vh;
            this.setChar('─', y, x);
            this.setBackground(bg, y, x);
            this.setForeground(fg, y, x);
        }

        // draw left vertical line
        for (int y = vy + startOffset; y < vy + vh - endOffset; y++) {
            int x = vx;
            this.setChar('│', y, x);
            this.setBackground(bg, y, x);
            this.setForeground(fg, y, x);
        }

        // draw left vertical line
        for (int y = vy + startOffset; y < vy + vh - endOffset; y++) {
            int x = vx + vw;
            this.setChar('│', y, x);
            this.setBackground(bg, y, x);
            this.setForeground(fg, y, x);
        }
    }

    public void highlight(int vy, int vx, int vh, int vw) {

    }

}
