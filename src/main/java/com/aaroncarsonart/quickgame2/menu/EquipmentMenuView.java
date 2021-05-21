package com.aaroncarsonart.quickgame2.menu;

import com.aaroncarsonart.quickgame2.Game;
import com.aaroncarsonart.quickgame2.hero.Hero;
import com.aaroncarsonart.quickgame2.hero.Stat;
import com.aaroncarsonart.quickgame2.hero.StatModifier;
import com.aaroncarsonart.quickgame2.inventory.Equipment;
import com.aaroncarsonart.quickgame2.inventory.Inventory;
import com.aaroncarsonart.quickgame2.inventory.Item;
import com.aaroncarsonart.quickgame2.inventory.RecoveryItem;
import com.aaroncarsonart.quickgame2.util.Pair;
import com.aaroncarsonart.quickgame2.util.StringUtils;
import imbroglio.Position2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class EquipmentMenuView extends MenuView {

    private Inventory inventory;
    private Position2D origin;
    private String header;

    public EquipmentMenuView(Game game, Position2D origin, String header, Inventory inventory) {
        super(game);
        this.origin = origin;
        this.header = header;
        this.inventory = inventory;
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
        int height = Math.min(items.size(), maxLength);
        int width = 0;
        for (int i = 0; i < items.size(); i++) {
            width = Math.max(width, items.get(i).getLabel().length());
        }

        int selectedIndex = menu.getIndex();
//        for (int i = 0; i < items.size(); i++) {
        for (int i = startOffset; i < startOffset + height; i++) {
            MenuItem item = items.get(i);
            String label = item.getLabel();
            Color color = item == null ? Color.DARK_GRAY : item.getColor();
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
                    bg = color;
                    fg = Color.BLACK;
                } else {
                    bg = Color.BLACK;
                    fg = color;
                }

                int x = bodyOrigin.x() + j;
                int y = bodyOrigin.y() + i - startOffset;

                // draw character
                game.drawChar(g, c, x, y, bg, fg);
            }

            // draw colorized quantity sign if applicable
            Inventory.Slot slot = inventory.getSlots()[i];
            if (slot.getItem() == null || slot.getQuantity() == 1) {
                continue;
            }
            String name = slot.getItem().getName();
            int quantity = slot.getQuantity();
            String quantityString = "x" + quantity;

            int x = bodyOrigin.x() + name.length() + 1;
//            int x = bodyOrigin.x();
            int y = bodyOrigin.y() + i - startOffset;
            Color bg, fg;
            if (i == selectedIndex) {
                bg = color == Color.WHITE ? Color.YELLOW : color;
                fg = Color.BLACK;
            } else {
                bg = Color.BLACK;
                fg = color == Color.WHITE ? Color.YELLOW : color;
            }
            game.drawString(g, quantityString, x, y, bg, fg);
        }

        Position2D statusOrigin = new Position2D(bodyOrigin.x() + width + 1, bodyOrigin.y() );
        int statusWidth = 30;
        int statusHeight = 20;
        // todo draw player status
        for (int x = 0; x < statusWidth; x++) {
            for (int y = 0; y < statusHeight; y++) {
                game.drawChar(g, ' ', statusOrigin.x() + x, statusOrigin.y() + y, Color.BLACK, Color.BLACK);
            }
        }

        int cx, cy;
        String field, valueStr;
        Color bg = Color.BLACK;
        Color fgField = Color.WHITE;
        Color fgValue = Color.YELLOW;

        cx = statusOrigin.x();
        cy = statusOrigin.y();

        Stat stat;
        int baseValue, itemModifier;
        Color statColor;
        int oldValue, newValue;
        int xColumn2 = 14;

        Hero hero = game.getHero();
        Inventory.Slot selectedSlot = inventory.getSlots()[selectedIndex];
        Map<Stat, StatModifier> selectedItemModifiers;
        Map<Stat, List<StatModifier>> equippedItemModifiers;
        if (selectedSlot.getItem() != null && selectedSlot.getItem() instanceof Equipment) {
            Equipment selectedItem = (Equipment) selectedSlot.getItem();
            selectedItemModifiers = selectedItem.getStatModifiers();

        } else {
            selectedItemModifiers = new HashMap<>();
        }

        field = "Name: ";
        game.drawString(g, field, cx, cy, bg, fgField);
        cx += field.length();
        valueStr = hero.getName();
        game.drawString(g, valueStr, cx, cy, bg, fgValue);
        cx = statusOrigin.x();
        cy += 2;

        field = "Str: ";
        game.drawString(g, field, cx, cy, bg, fgField);
        cx += field.length();

        oldValue = hero.getModifiedStrength();
        valueStr = String.valueOf(oldValue);
        game.drawString(g, valueStr, cx, cy, bg, fgValue);
        cx += valueStr.length();

        if (selectedSlot.getItem() != null && selectedSlot.getItem() instanceof Equipment) {
            Equipment selectedEquipment = (Equipment) selectedSlot.getItem();
            drawStatBonus(g, cx, cy, hero, hero::getModifiedStrength, selectedEquipment);
        }
        cx = statusOrigin.x() + xColumn2;

        field = "Int: ";
        game.drawString(g, field, cx, cy, bg, fgField);
        cx += field.length();

        oldValue = hero.getModifiedIntelligence();
        valueStr = String.valueOf(oldValue);
        game.drawString(g, valueStr, cx, cy, bg, fgValue);
        cx += valueStr.length();

        if (selectedSlot.getItem() != null && selectedSlot.getItem() instanceof Equipment) {
            Equipment selectedEquipment = (Equipment) selectedSlot.getItem();
            drawStatBonus(g, cx, cy, hero, hero::getModifiedIntelligence, selectedEquipment);
        }
        cx = statusOrigin.x();
        cy += 1;

        field = "Agi: ";
        game.drawString(g, field, cx, cy, bg, fgField);
        cx += field.length();

        oldValue = hero.getModifiedAgility();
        valueStr = String.valueOf(oldValue);
        game.drawString(g, valueStr, cx, cy, bg, fgValue);
        cx += valueStr.length();

        if (selectedSlot.getItem() != null && selectedSlot.getItem() instanceof Equipment) {
            Equipment selectedEquipment = (Equipment) selectedSlot.getItem();
            drawStatBonus(g, cx, cy, hero, hero::getModifiedAgility, selectedEquipment);
        }
        cx = statusOrigin.x() + xColumn2;

        field = "Wis: ";
        game.drawString(g, field, cx, cy, bg, fgField);
        cx += field.length();

        oldValue = hero.getModifiedWisdom();
        valueStr = String.valueOf(oldValue);
        game.drawString(g, valueStr, cx, cy, bg, fgValue);
        cx += valueStr.length();

        if (selectedSlot.getItem() != null && selectedSlot.getItem() instanceof Equipment) {
            Equipment selectedEquipment = (Equipment) selectedSlot.getItem();
            drawStatBonus(g, cx, cy, hero, hero::getModifiedWisdom, selectedEquipment);
        }
        cx = statusOrigin.x();
        cy += 1;

        field = "Sta: ";
        game.drawString(g, field, cx, cy, bg, fgField);
        cx += field.length();

        oldValue = hero.getModifiedStamina();
        valueStr = String.valueOf(oldValue);
        game.drawString(g, valueStr, cx, cy, bg, fgValue);
        cx += valueStr.length();

        if (selectedSlot.getItem() != null && selectedSlot.getItem() instanceof Equipment) {
            Equipment selectedEquipment = (Equipment) selectedSlot.getItem();
            drawStatBonus(g, cx, cy, hero, hero::getModifiedStamina, selectedEquipment);
        }
        cx = statusOrigin.x() + xColumn2;

        field = "Cha: ";
        game.drawString(g, field, cx, cy, bg, fgField);
        cx += field.length();

        oldValue = hero.getModifiedCharisma();
        valueStr = String.valueOf(oldValue);
        game.drawString(g, valueStr, cx, cy, bg, fgValue);
        cx += valueStr.length();

        if (selectedSlot.getItem() != null && selectedSlot.getItem() instanceof Equipment) {
            Equipment selectedEquipment = (Equipment) selectedSlot.getItem();
            drawStatBonus(g, cx, cy, hero, hero::getModifiedCharisma, selectedEquipment);
        }
        cx = statusOrigin.x();
        cy += 2;

        field = "Weapon: ";
        game.drawString(g, field, cx, cy, bg, fgField);
        cx += field.length();
        valueStr = Optional.ofNullable(hero.getWeapon())
                .map(Equipment::getName)
                .orElse("<none>");
        statColor = valueStr.equals("<none>") ? Color.DARK_GRAY : fgValue;
        game.drawString(g, valueStr, cx, cy, bg, statColor);
        cx = statusOrigin.x();
        cy += 1;

        field = "Shield: ";
        game.drawString(g, field, cx, cy, bg, fgField);
        cx += field.length();
        valueStr = Optional.ofNullable(hero.getShield())
                .map(Equipment::getName)
                .orElse("<none>");
        statColor = valueStr.equals("<none>") ? Color.DARK_GRAY : fgValue;
        game.drawString(g, valueStr, cx, cy, bg, statColor);
        cx = statusOrigin.x();
        cy += 1;

        field = "Armor:  ";
        game.drawString(g, field, cx, cy, bg, fgField);
        cx += field.length();
        valueStr = Optional.ofNullable(hero.getArmor())
                .map(Equipment::getName)
                .orElse("<none>");
        statColor = valueStr.equals("<none>") ? Color.DARK_GRAY : fgValue;
        game.drawString(g, valueStr, cx, cy, bg, statColor);
        cx = statusOrigin.x();
        cy += 1;

        field = "Helmet: ";
        game.drawString(g, field, cx, cy, bg, fgField);
        cx += field.length();
        valueStr = Optional.ofNullable(hero.getHelmet())
                .map(Equipment::getName)
                .orElse("<none>");
        statColor = valueStr.equals("<none>") ? Color.DARK_GRAY : fgValue;
        game.drawString(g, valueStr, cx, cy, bg, statColor);
        cx = statusOrigin.x();
        cy += 2;

        field = "Etc1: ";
        game.drawString(g, field, cx, cy, bg, fgField);
        cx += field.length();
        valueStr = Optional.ofNullable(hero.getEtc1())
                .map(Equipment::getName)
                .orElse("<none>");
        statColor = valueStr.equals("<none>") ? Color.DARK_GRAY : fgValue;
        game.drawString(g, valueStr, cx, cy, bg, statColor);
        cx = statusOrigin.x();
        cy += 1;

        field = "Etc2: ";
        game.drawString(g, field, cx, cy, bg, fgField);
        cx += field.length();
        valueStr = Optional.ofNullable(hero.getEtc2())
                .map(Equipment::getName)
                .orElse("<none>");
        statColor = valueStr.equals("<none>") ? Color.DARK_GRAY : fgValue;
        game.drawString(g, valueStr, cx, cy, bg, statColor);
        cx = statusOrigin.x();
        cy += 2;

        // --------------------------------------------------------------------
        field = "Atk: ";
        game.drawString(g, field, cx, cy, bg, fgField);
        cx += field.length();

        oldValue = hero.getAttack();
        valueStr = String.valueOf(oldValue);
        game.drawString(g, valueStr, cx, cy, bg, fgValue);
        cx += valueStr.length();

        if (selectedSlot.getItem() != null && selectedSlot.getItem() instanceof Equipment) {
            Equipment selectedEquipment = (Equipment) selectedSlot.getItem();
            drawStatBonus(g, cx, cy, hero, hero::getAttack, selectedEquipment);
        }
        cx = statusOrigin.x();
        cy += 1;

        // --------------------------------------------------------------------
        field = "Def: ";
        game.drawString(g, field, cx, cy, bg, fgField);
        cx += field.length();

        oldValue = hero.getDefense();
        valueStr = String.valueOf(oldValue);
        game.drawString(g, valueStr, cx, cy, bg, fgValue);
        cx += valueStr.length();

        if (selectedSlot.getItem() != null && selectedSlot.getItem() instanceof Equipment) {
            Equipment selectedEquipment = (Equipment) selectedSlot.getItem();
            drawStatBonus(g, cx, cy, hero, hero::getDefense, selectedEquipment);
        }
        cx = statusOrigin.x();
        cy += 1;

        // --------------------------------------------------------------------
        field = "Acc: ";
        game.drawString(g, field, cx, cy, bg, fgField);
        cx += field.length();

        oldValue = hero.getAccuracy();
        valueStr = String.valueOf(oldValue);
        game.drawString(g, valueStr, cx, cy, bg, fgValue);
        cx += valueStr.length();

        if (selectedSlot.getItem() != null && selectedSlot.getItem() instanceof Equipment) {
            Equipment selectedEquipment = (Equipment) selectedSlot.getItem();
            drawStatBonus(g, cx, cy, hero, hero::getAccuracy, selectedEquipment);
        }
        cx = statusOrigin.x();
        cy += 1;

        // --------------------------------------------------------------------
        field = "Eva: ";
        game.drawString(g, field, cx, cy, bg, fgField);
        cx += field.length();

        oldValue = hero.getEvade();
        valueStr = String.valueOf(oldValue);
        game.drawString(g, valueStr, cx, cy, bg, fgValue);
        cx += valueStr.length();

        if (selectedSlot.getItem() != null && selectedSlot.getItem() instanceof Equipment) {
            Equipment selectedEquipment = (Equipment) selectedSlot.getItem();
            drawStatBonus(g, cx, cy, hero, hero::getEvade, selectedEquipment);
        }
        cx = statusOrigin.x();
        cy += 1;

        // ------------------------------------------------
        // Draw item details view
        // ------------------------------------------------
        // TODO complete
        Position2D detailsOrigin = new Position2D(bodyOrigin.x() + width + 1, bodyOrigin.y() + statusHeight + 1);
        int detailsWidth = 24;
        int detailsHeight = 12;
        for (int x = 0; x < detailsWidth; x++) {
            for (int y = 0; y < detailsHeight; y++) {
                game.drawChar(g, ' ', detailsOrigin.x() + x, detailsOrigin.y() + y, Color.BLACK, Color.BLACK);
            }
        }

        Item item = inventory.getSlots()[selectedIndex].getItem();
        if (item != null && item instanceof Equipment) {
//            int cx, cy;
//            String field, value;
//            Color bg = Color.BLACK;
//            Color fgField = Color.WHITE;
//            Color fgValue = Color.YELLOW;

            cx = detailsOrigin.x();
            cy = detailsOrigin.y();

            field = "Name: ";
            game.drawString(g, field, cx, cy, bg, fgField);
            cx += field.length();
            valueStr = item.getName();
            game.drawString(g, valueStr, cx, cy, bg, fgValue);
            cx = detailsOrigin.x();
            cy += 2;

            field = "Type: ";
            game.drawString(g, field, cx, cy, bg, fgField);
            cx += field.length();
            valueStr = Item.getItemType(item);
            game.drawString(g, valueStr, cx, cy, bg, fgValue);
            cx = detailsOrigin.x();
            cy += 1;

            field = "Wt: ";
            game.drawString(g, field, cx, cy, bg, fgField);
            cx += field.length();
            valueStr = String.format("%.1f lbs", item.getWeight());
            game.drawString(g, valueStr, cx, cy, bg, fgValue);
            cx = detailsOrigin.x();
            cy += 1;

            field = "Sell: ";
            game.drawString(g, field, cx, cy, bg, fgField);
            cx += field.length();
            valueStr = String.format("$%d", item.getCost() / 2);
            game.drawString(g, valueStr, cx, cy, bg, fgValue);
            cx = detailsOrigin.x();
            cy += 2;

            field = "Description: ";
            game.drawString(g, field, cx, cy, bg, fgField);
            cx = detailsOrigin.x();
            cy += 1;

            // render equipment description
            if (item instanceof Equipment) {
                Equipment equipment = (Equipment) item;
                for (StatModifier statModifier : equipment.getStatModifiers().values()) {
                    valueStr = StringUtils.capitalize(statModifier.getStat().name()) + " ";
                    game.drawString(g, valueStr, cx, cy, bg, fgValue);
                    cx += valueStr.length();

                    int modifier = statModifier.getModifier();
                    String sign = modifier >= 0 ? "+" : "-";
                    Color color = modifier >= 0 ? Color.GREEN : Color.RED;
                    valueStr = sign + Math.abs(modifier);
                    game.drawString(g, valueStr, cx, cy, bg, color);
                    cx = detailsOrigin.x();
                    cy += 1;
                }
            }
            // render recovery description
            else if (item instanceof RecoveryItem) {
                RecoveryItem recoveryItem = (RecoveryItem) item;
                ArrayList<Pair<String, Integer>> pairs = new ArrayList<>();
                if (recoveryItem.getHealth() > 0) {
                    pairs.add(new Pair<>("HP ", recoveryItem.getHealth()));
                }
                if (recoveryItem.getMana() > 0) {
                    pairs.add(new Pair<>("MP ", recoveryItem.getMana()));
                }
                if (recoveryItem.getEnergy() > 0) {
                    pairs.add(new Pair<>("EP ", recoveryItem.getEnergy()));
                }
                for (Pair<String, Integer> pair : pairs) {
                    int modifier = pair.getValue();
                    String sign = modifier >= 0 ? "+" : "-";
                    Color color = modifier >= 0 ? Color.GREEN : Color.RED;
                    valueStr = sign + Math.abs(modifier);
                    game.drawString(g, valueStr, cx, cy, bg, color);
                    cx += valueStr.length();

                    valueStr = " " + pair.getKey();
                    game.drawString(g, valueStr, cx, cy, bg, fgValue);
                    cx = detailsOrigin.x();
                    cy += 1;

                }
            } else {
                valueStr = "<none>";
                game.drawString(g, valueStr, cx, cy, bg, fgValue);
                cx = detailsOrigin.x();
                cy += 1;
            }
            cy += 1;
        }




        // ------------------------------------------------
        // Draw menu borders
        // ------------------------------------------------
        drawMenuBorders(g, statusOrigin, statusWidth, statusHeight);
        drawMenuBorders(g, detailsOrigin, detailsWidth, detailsHeight);
        drawMenuBorders(g, bodyOrigin, width, height);

        // ------------------------------------------------
        // Draw header
        // ------------------------------------------------
        game.drawString(g, header, headerOrigin.x(), headerOrigin.y(), Color.BLACK, Color.WHITE);
        drawMenuBorders(g, headerOrigin, header.length(), 1);
    }

    private void drawStatBonus(Graphics2D g, int cx, int cy, Hero hero, Supplier<Integer> statFunction, Equipment selectedEquipment) {
        int oldValue = statFunction.get();
        Equipment currentEquipment = hero.equipItem(selectedEquipment, selectedEquipment.getType(), 1);
        int newValue = statFunction.get();
        String valueStr = String.valueOf(newValue);
        Color statColor = Color.WHITE;
        if (newValue > oldValue) {
            statColor = Color.GREEN;
        } else if (newValue < oldValue) {
            statColor = Color.RED;
        }
        if (newValue != oldValue) {
            String arrowStr = " -> ";
            game.drawString(g, arrowStr, cx, cy, Color.BLACK, Color.WHITE);
            cx += arrowStr.length();

            game.drawString(g, valueStr, cx, cy, Color.BLACK, statColor);
        }
        hero.equipItem(currentEquipment, selectedEquipment.getType(), 1);
    }
}
