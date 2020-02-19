package com.aaroncarsonart.quickgame2.menu;

import com.aaroncarsonart.quickgame2.Game;
import com.aaroncarsonart.quickgame2.hero.Hero;
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

        Position2D menuOrigin = new Position2D(1, 3);
        Color bg = Color.BLACK;
        Color fg = Color.WHITE;
        Color vbg = Color.BLACK;
        Color vfg = Color.YELLOW;

        // draw outer box
        int width = 30;
        int height = 20;
        drawMenuBorders(g, menuOrigin, width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                game.drawChar(g, ' ', menuOrigin.x() + x, menuOrigin.y() + y, bg, fg);
            }
        }

        // ------------------------------------------------
        // Draw hero stats
        // ------------------------------------------------
        Hero hero = game.getHero();

        int cx = menuOrigin.x();
        int cy = menuOrigin.y();

        String statLabel, statValue;

        statLabel = "Name: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = hero.getName();
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 2;

        statLabel = "Class: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = hero.getHeroClass().getName();
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 1;

//        String raceLabel = "Race: ";
//        game.drawString(g, raceLabel, cx, cy, bg, fg);
//        cx += raceLabel.length();
//        String raceValue = hero.getHeroRace().getName();
//        game.drawString(g, raceValue, cx, cy, bg, fg);
//        cx = menuOrigin.x();
//        cy ++;

        int stat2ndColumnOffset = 8;

        statLabel = "Lv: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getLevel());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x() + stat2ndColumnOffset;

        statLabel = "Exp: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getExp());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 2;

        // stats


        statLabel = "Str: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getStrength());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x() + stat2ndColumnOffset;

        statLabel = "Int: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getIntelligence());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 1;

        statLabel = "Sta: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getStamina());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x() + stat2ndColumnOffset;

        statLabel = "Wis: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getWisdom());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 1;


        statLabel = "Agi: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getAgility());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x() + stat2ndColumnOffset;

        statLabel = "Cha: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getCharisma());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 2;

        // equipment
        statLabel = "Weapon: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getWeapon());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 1;

        statLabel = "Shield: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getShield());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 1;

        statLabel = "Armor:  ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getArmor());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 1;

        statLabel = "Helmet: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getHelmet());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 2;

        statLabel = "Etc1: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getEtc1());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 1;

        statLabel = "Etc2: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getEtc2());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 1;



        // draw status menu label
        String statusString = "Status";
        Position2D menuLabelOrigin = new Position2D(1, 1);
        game.drawString(g, statusString, menuLabelOrigin.x(), menuLabelOrigin.y(), bg, fg);
        drawMenuBorders(g, menuLabelOrigin, statusString.length(), 1);

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
