package com.aaroncarsonart.quickgame2.menu;

import com.aaroncarsonart.quickgame2.Game;
import imbroglio.Position2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

/**
 * Draws a borderless, vertical list menu in the upper left hand corner.
 */
public class VerticalMenuView extends MenuView {
    Position2D origin;
    String header;

    public VerticalMenuView(Game game, Position2D origin, String header) {
        super(game);
        this.origin = origin;
        this.header = header;
    }

    public void render(Graphics2D g, Menu menu) {
        Position2D headerOrigin = origin;
        Position2D bodyOrigin = new Position2D(origin.x(), origin.y() + 2);

        // ------------------------------------------------
        // Draw menu contents
        // ------------------------------------------------
        int startOffset = menu.getStartOffset();
        int maxLength = menu.getMaxLength();

        List<MenuItem> items = menu.getMenuItems();
        int height;
        if (maxLength > 0 ) {
            height = Math.min(items.size(), maxLength);
        } else {
            height = items.size();
        }
        int width = 0;
        for (int i = 0; i < items.size(); i++) {
            width = Math.max(width, items.get(i).getLabel().length());
        }

        int selectedIndex = menu.getIndex();
        for (int i = startOffset; i < startOffset + height; i++) {
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

                int x = bodyOrigin.x() + j;
                int y = bodyOrigin.y() + i - startOffset;

                // draw character
                game.drawChar(g, c, x, y, bg, fg);
            }
        }

        // ------------------------------------------------
        // Draw menu border
        // ------------------------------------------------
        drawMenuBorders(g, bodyOrigin, width, height);

        // ------------------------------------------------
        // Draw header
        // ------------------------------------------------
        game.drawString(g, header, headerOrigin.x(), headerOrigin.y(), Color.BLACK, Color.WHITE);
        drawMenuBorders(g, headerOrigin, header.length(), 1);

    }
}
