package com.aaroncarsonart.quickgame2.menu;

import com.aaroncarsonart.quickgame2.Game;
import imbroglio.Position2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

public class StatusMenuView extends MenuView {
    Position2D origin;

    public StatusMenuView(Game game) {
        super(game);
        this.origin = new Position2D(1, 1);
    }

    public void render(Graphics2D g, Menu menu) {

        Position2D origin = new Position2D(1, 1);
        String statusString = "Status";
        Color bg = Color.BLACK;
        Color fg = Color.WHITE;
        game.drawString(g, statusString, origin.x(), origin.y(), bg, fg);
        drawMenuBorders(g, origin, statusString.length(), 1);
    }

    private void drawMenuContents(Graphics2D g, Menu menu) {
        List<MenuItem> items = menu.getMenuItems();
        int height = items.size();
        int width = 0;
        for (int i = 0; i < items.size(); i++) {
            width = Math.max(width, items.get(i).getLabel().length());
        }
//            for (int x = 0; x < width; x++) {
//                for (int y = 0; y < height; y++) {
//                    drawChar(g, " ", x, y, Color.BLACK, Color.BLACK);
//                }
//            }
        int selectedIndex = menu.getIndex();
        for (int i = 0; i < items.size(); i++) {
            MenuItem item = items.get(i);
            String label = item.getLabel();
            for (int j = 0; j < width; j++) {

                // get character to print
                char c;
                if (j < label.length()) {
                    c = label.charAt(j);
                } else {
                    c = ' ';
                }

                // get colors
                Color bg, fg;
                if (i == selectedIndex) {
                    bg = Color.WHITE;
                    fg = Color.BLACK;
                } else {
                    bg = Color.BLACK;
                    fg = Color.WHITE;
                }

                int x = origin.x() + j;
                int y = origin.y() + i;

                // draw character
                game.drawChar(g, c, x, y, bg, fg);
            }
        }
    }

}
