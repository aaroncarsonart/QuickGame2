package com.aaroncarsonart.quickgame2.menu;

import com.aaroncarsonart.quickgame2.Game;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Draws a centered Menu with a border.
 */
public class CenterMenuView extends MenuView {

    public CenterMenuView(Game game) {
        super(game);
    }

    public void render(Graphics2D g, Menu menu) {
        int menuWidth = 0;
        for (MenuItem menuItem : menu.getMenuItems()) {
            menuWidth = Math.max(menuWidth, menuItem.getLabel().length());
        }
        int menuHeight = menu.getMenuItems().size();

        int mx = (game.getGridWidth() - menuWidth) / 2;
        int my = (game.getGridHeight() - menuHeight) / 2;

        int menuCursor = menu.getIndex();

        // ------------------------------------------------
        // draw oldMenuList contents
        // ------------------------------------------------
        for (int y = 0; y < menuHeight; y++) {
            for (int x = 0; x < menuWidth; x++) {
                Color bg = Color.BLACK;
                Color fg = Color.WHITE;
                if (y == menuCursor) {
                    bg = Color.WHITE;
                    fg = Color.BLACK;
                }

                MenuItem menuItem = menu.getMenuItems().get(y);
                String menuLabel = menuItem.getLabel();
                char c;
                if (x < menuLabel.length()) {
                    c = menuLabel.charAt(x);
                } else {
                    c = ' ';
                }
                game.drawChar(g, c, mx + x, my + y, bg, fg);
            }
        }
        // ------------------------------------------------
        // draw borders around menu
        // ------------------------------------------------
        Color bg = Color.BLACK;
        Color fg = Color.DARK_GRAY;
        // ─│┌┐└┘

        for (int y = 0; y < menuHeight; y++) {
            int x = mx - 1;
            game.drawChar(g, '│', x, my + y, bg, fg);
            x = mx + menuWidth;
            game.drawChar(g, '│', x, my + y, bg, fg);
        }

        for (int x = 0; x < menuWidth; x++) {
            int y = my - 1;
            game.drawChar(g, '─', mx + x, y, bg, fg);
            y = my + menuHeight;
            game.drawChar(g, '─', mx + x, y, bg, fg);
        }
        game.drawChar(g, '┌', -1 + mx, -1 + my, bg, fg);
        game.drawChar(g, '┐', mx + menuWidth, -1 + my, bg, fg);
        game.drawChar(g, '└', -1 + mx, my + menuHeight, bg, fg);
        game.drawChar(g, '┘', mx + menuWidth, my + menuHeight, bg, fg);

    }
}
