package com.aaroncarsonart.quickgame2.menu;

import com.aaroncarsonart.quickgame2.Game;
import imbroglio.Position2D;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

/**
 * Draws a borderless, vertical list menu in the upper left hand corner.
 */
public class BasicVerticalMenuView extends MenuView {
    Position2D origin;
    boolean drawBorder;

    public BasicVerticalMenuView(Game game, Position2D origin, boolean drawBorder) {
        super(game);
        this.origin = origin;
        this.drawBorder = drawBorder;
    }

    public void render(Graphics2D g, Menu menu) {
        // ------------------------------------------------
        // Draw menu contents
        // ------------------------------------------------

        List<MenuItem> items = menu.getMenuItems();
        int height = items.size();
        int width = 0;
        for (int i = 0; i < items.size(); i++) {
            width = Math.max(width, items.get(i).getLabel().length());
        }

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

        // ------------------------------------------------
        // Draw menu border
        // ------------------------------------------------
        if(drawBorder) {
            drawMenuBorders(g, origin, width, height);
        }
    }
}
