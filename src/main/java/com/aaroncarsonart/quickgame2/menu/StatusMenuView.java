package com.aaroncarsonart.quickgame2.menu;

import com.aaroncarsonart.quickgame2.Game;
import com.aaroncarsonart.quickgame2.hero.Hero;
import com.aaroncarsonart.quickgame2.inventory.Equipment;
import imbroglio.Position2D;

import java.awt.Color;
import java.awt.Graphics2D;

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

        int column2Off = 8;
        int column3Off = 17;

        statLabel = "Lv: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getLevel());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x() + column2Off;

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
        statValue = String.valueOf(hero.getModifiedStrength());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x() + column2Off;

        statLabel = "Int: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getModifiedIntelligence());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 1;

        statLabel = "Sta: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getModifiedStamina());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x() + column2Off;

        statLabel = "Wis: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getModifiedWisdom());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 1;


        statLabel = "Agi: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getModifiedAgility());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x() + column2Off;

        statLabel = "Cha: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getModifiedCharisma());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 2;

        // equipment
        statLabel = "Weapon: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = getEquipmentName(hero.getWeapon());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 1;

        statLabel = "Shield: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = getEquipmentName(hero.getShield());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 1;

        statLabel = "Armor:  ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = getEquipmentName(hero.getArmor());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 1;

        statLabel = "Helmet: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = getEquipmentName(hero.getHelmet());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 2;

        statLabel = "Etc1: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = getEquipmentName(hero.getEtc1());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 1;

        statLabel = "Etc2: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = getEquipmentName(hero.getEtc2());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 2;

        // combat stats

        statLabel = "Atk: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getAttack());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x() + column2Off;

        statLabel = "Def: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getDefense());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x();
        cy += 1;

        statLabel = "Acc: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getAccuracy());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x() + column2Off;

        statLabel = "Eva: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getEvade());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x() + column3Off;
        cy = menuOrigin.y() + 2;

        // vital stats

        statLabel = "HP: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getHealth());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx += statValue.length();

        statLabel = "/";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getModifiedMaxHealth());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x() + column3Off;
        cy += 1;

        statLabel = "MP: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getMana());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx += statValue.length();

        statLabel = "/";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getModifiedMaxMana());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x() + column3Off;
        cy += 2;

        statLabel = "EP: ";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(Double.valueOf(hero.getEnergy()).intValue());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx += statValue.length();

        statLabel = "/";
        game.drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getModifiedMaxEnergy());
        game.drawString(g, statValue, cx, cy, vbg, vfg);
        cx = menuOrigin.x() + column3Off;
        cy += 1;


        // ----------------------------
        // draw status menu label
        // ----------------------------
        String statusString = "Status";
        Position2D menuLabelOrigin = new Position2D(1, 1);
        game.drawString(g, statusString, menuLabelOrigin.x(), menuLabelOrigin.y(), bg, fg);
        drawMenuBorders(g, menuLabelOrigin, statusString.length(), 1);
    }

    private String getEquipmentName(Equipment equipment) {
        if (equipment == null) {
            return "<none>";
        }
        return equipment.getName();
    }
}

