package com.aaroncarsonart.quickgame2.menu;

import com.aaroncarsonart.quickgame2.Game;
import imbroglio.Position2D;

import java.awt.Color;
import java.awt.Graphics2D;

public abstract class MenuView {

    protected Game game;

    public MenuView(Game game) {
        this.game = game;
    }

    public abstract void render(Graphics2D g, Menu menu);

    /**
     * Draw a border around a given set of dimensions.
     * @param g The Graphics2D object to draw onto.
     * @param origin The top-left position of the box to draw.
     * @param menuWidth The width of the box to draw.
     * @param menuHeight The height of the box to draw.
     */
    public void drawMenuBorders(Graphics2D g, Position2D origin, int menuWidth, int menuHeight) {
        // ------------------------------------------------
        // draw borders around menu
        // ------------------------------------------------
        Color bg = Color.BLACK;
        Color fg = Color.DARK_GRAY;
        // ─│┌┐└┘
        int mx = origin.x();
        int my = origin.y();

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
