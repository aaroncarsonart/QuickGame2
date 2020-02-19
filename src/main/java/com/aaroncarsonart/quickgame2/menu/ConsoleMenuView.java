package com.aaroncarsonart.quickgame2.menu;

import com.aaroncarsonart.quickgame2.Game;
import imbroglio.Position2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

public class ConsoleMenuView extends MenuView {

    String prompt;

    public ConsoleMenuView(Game game, String prompt) {
        super(game);
        this.prompt = prompt;
    }

    public void render(Graphics2D g, Menu menu) {
        ConsoleMenu consoleMenu = (ConsoleMenu) menu;

        Color bg = Color.BLACK;
        Color fg = Color.WHITE;

        Position2D consoleOrigin = new Position2D(1, 3);
        List<Character> characterBuffer = consoleMenu.getCharacterBuffer();
        int maxLength = consoleMenu.getMaxLength();
        for (int i = 0; i < maxLength; i++) {
            game.drawChar(g, ' ', consoleOrigin.x() + i, consoleOrigin.y(), bg, fg);
        }
        for (int i = 0; i < characterBuffer.size() + 1; i++) {
            char c = ' ';
            if (i < characterBuffer.size()) {
                c = characterBuffer.get(i);
            }
            Color cbg = bg;
            Color cfg = fg;
            if (i == consoleMenu.getIndex()) {
                cbg = fg;
                cfg = bg;
            }
            game.drawChar(g, c, consoleOrigin.x() + i, consoleOrigin.y(), cbg, cfg);
        }
        drawMenuBorders(g, consoleOrigin, maxLength, 1);

        Position2D labelOrigin = new Position2D(1, 1);
        game.drawString(g, prompt, labelOrigin.x(), labelOrigin.y(), bg, fg);
        drawMenuBorders(g, labelOrigin, prompt.length(), 1);


    }
}
