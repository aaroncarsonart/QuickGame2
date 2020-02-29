package com.aaroncarsonart.quickgame2.menu;

import com.aaroncarsonart.quickgame2.Game;
import com.aaroncarsonart.quickgame2.hero.Hero;
import com.aaroncarsonart.quickgame2.hero.Stat;
import com.aaroncarsonart.quickgame2.inventory.Inventory;
import com.aaroncarsonart.quickgame2.util.StringUtils;
import imbroglio.Position2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;

public class LevelUpMenuView extends MenuView {

    private Position2D origin;
    private String header;

    public LevelUpMenuView(Game game, Position2D origin, String header) {
        super(game);
        this.origin = origin;
        this.header = header;
    }

    @Override
    public void render(Graphics2D g, Menu menu) {
        int width = 25;
        int height = 12;
        Position2D bodyOrigin = new Position2D(origin.x(), origin.y() + 2);

        for (int x = bodyOrigin.x(); x < bodyOrigin.x() + width; x++) {
            for (int y = bodyOrigin.y(); y < bodyOrigin.y() + height; y++) {
                game.drawChar(g, ' ', x, y, Color.BLACK, Color.WHITE);
            }
        }

        int cx = bodyOrigin.x();
        int cy = bodyOrigin.y();

        Hero hero = game.getHero();
        String levelUpDesc = "Reached level ";
        game.drawString(g, levelUpDesc, cx, cy, Color.BLACK, Color.WHITE);
        cx += levelUpDesc.length();

        levelUpDesc = String.valueOf(hero.getLevel());
        game.drawString(g, levelUpDesc, cx, cy, Color.BLACK, Color.YELLOW);
        cx += levelUpDesc.length();

        levelUpDesc = "!";
        game.drawString(g, levelUpDesc, cx, cy, Color.BLACK, Color.WHITE);
        cx = origin.x();
        cy += 2;

        levelUpDesc = "Select stats to increase:";
        game.drawString(g, levelUpDesc, cx, cy, Color.BLACK, Color.WHITE);
//        cy += 1;
//
//        levelUpDesc = "increase:";
//        game.drawString(g, levelUpDesc, cx, cy, Color.BLACK, Color.WHITE);
        cy += 2;

        List<Stat> stats = Arrays.asList(
                Stat.STRENGTH,
                Stat.AGILITY,
                Stat.STAMINA,
                Stat.INTELLIGENCE,
                Stat.WISDOM,
                Stat.CHARISMA);

        int selectedIndex = menu.getIndex();
//        System.out.println("selectedIndex: " + selectedIndex);

        if (!(menu instanceof LevelUpMenu)) {
            throw new IllegalStateException("LevelUpMenuView should only be paired with a LevelUpMenu");
        }
        LevelUpMenu levelUpMenu = (LevelUpMenu) menu;
        int remainingStatPoints = levelUpMenu.getStatPoints();

        int i = 0;
        for (MenuItem menuItem : menu.getMenuItems()) {
            Color fg, bg;
            if (i == selectedIndex) {
                bg = Color.WHITE;
                fg = Color.BLACK;
            } else {
                bg = Color.BLACK;
                fg = Color.WHITE;
            }
            Stat stat = Stat.valueOf(menuItem.getLabel());
            String statLabel = StringUtils.capitalize(stat.name()).substring(0, 3) + ":";
            game.drawString(g, statLabel, cx, cy, bg, fg);
            cx += statLabel.length();

            bg = Color.BLACK;
            fg = Color.WHITE;
            statLabel = " ";
            game.drawString(g, statLabel, cx, cy, bg, fg);
            cx += statLabel.length();

//            if (i == selectedIndex) {
//                bg = Color.YELLOW;
//                fg = Color.BLACK;
//            } else {
                bg = Color.BLACK;
                fg = Color.YELLOW;
//            }

            int value = hero.getStatValue(stat);
            int statBonus = levelUpMenu.getStatBonus(stat);
            String statValue = String.valueOf(value + statBonus);
            if (statBonus > 0) {
                fg = Color.GREEN;
            }
            game.drawString(g, statValue, cx, cy, bg, fg);
            cx += statValue.length();

            if (i == selectedIndex && remainingStatPoints > 0) {
                statLabel = " -> ";
                game.drawString(g, statLabel, cx, cy, bg, Color.WHITE);
                cx += statLabel.length();

                statValue = String.valueOf(value + statBonus + 1);
                game.drawString(g, statValue, cx, cy, bg, Color.GREEN);
                cx += statValue.length();

            }
            cx = bodyOrigin.x();
            cy += 1;
            i++;
        }
        cy += 1;

        String statPointsLabel = String.valueOf(remainingStatPoints);
        game.drawString(g, statPointsLabel, cx, cy, Color.BLACK, Color.YELLOW);
        cx += statPointsLabel.length();
        statPointsLabel = " stat points remaining.";
        game.drawString(g, statPointsLabel, cx, cy, Color.BLACK, Color.WHITE);

        drawMenuBorders(g, bodyOrigin, width, height);
        drawMenuBorders(g, origin, header.length(), 1);
        game.drawString(g, header, origin.x(), origin.y(), Color.BLACK, Color.WHITE);
    }
}
