package com.aaroncarsonart.quickgame2;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;

public class CharPanel {

    private JLabel label;
    private JPanel panel;
    private Color bgColor;
    private Color fgColor;

    public CharPanel() {
        label = new JLabel(" ");
        label.setFont(Constants.FONT);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(label);

        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
    }

    public void setBackground(Color color) {
        panel.setBackground(color);
        bgColor = color;
    }

    public void setForeground(Color color) {
        label.setForeground(color);
        fgColor = color;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public Color getFgColor() {
        return fgColor;
    }

    public void setChar(char c) {
        label.setText(String.valueOf(c));
    }

    public void setText(String s) {
        label.setText(s);
    }

    public JComponent getJPanel() {
        return panel;
    }
}
