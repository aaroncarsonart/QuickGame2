package com.aaroncarsonart.quickgame2.menu;

import com.aaroncarsonart.quickgame2.Game;
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
import java.util.List;

public class InventoryMenuView extends MenuView {

    protected Position2D origin;
    protected String header;
    protected Inventory inventory;

    public InventoryMenuView(Game game, Position2D origin, String header, Inventory inventory) {
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
                bg = Color.YELLOW;
                fg = Color.BLACK;
            } else {
                bg = Color.BLACK;
                fg = Color.YELLOW;
            }
            game.drawString(g, quantityString, x, y, bg, fg);
        }

        // ------------------------------------------------
        // Draw item details view
        // ------------------------------------------------
        Position2D dOrigin = new Position2D(bodyOrigin.x() + width + 1, bodyOrigin.y());
        int dWidth = 24;
        int dHeight = 12;
        for (int x = 0; x < dWidth; x++) {
            for (int y = 0; y < dHeight; y++) {
                game.drawChar(g, ' ', dOrigin.x() + x, dOrigin.y() + y, Color.BLACK, Color.BLACK);
            }
        }

        Item item = inventory.getSlots()[selectedIndex].getItem();
        if (item != null) {
            int cx, cy;
            String field, value;
            Color bg = Color.BLACK;
            Color fgField = Color.WHITE;
            Color fgValue = Color.YELLOW;

            cx = dOrigin.x();
            cy = dOrigin.y();

            field = "Name: ";
            game.drawString(g, field, cx, cy, bg, fgField);
            cx += field.length();
            value = item.getName();
            game.drawString(g, value, cx, cy, bg, fgValue);
            cx = dOrigin.x();
            cy += 2;

            field = "Type: ";
            game.drawString(g, field, cx, cy, bg, fgField);
            cx += field.length();
            value = Item.getItemType(item);
            game.drawString(g, value, cx, cy, bg, fgValue);
            cx = dOrigin.x();
            cy += 1;

            field = "Wt: ";
            game.drawString(g, field, cx, cy, bg, fgField);
            cx += field.length();
            value = String.format("%.1f lbs", item.getWeight());
            game.drawString(g, value, cx, cy, bg, fgValue);
            cx = dOrigin.x();
            cy += 1;

            field = "Sell: ";
            game.drawString(g, field, cx, cy, bg, fgField);
            cx += field.length();
            value = String.format("$%d", item.getCost() / 2);
            game.drawString(g, value, cx, cy, bg, fgValue);
            cx = dOrigin.x();
            cy += 2;

            field = "Description: ";
            game.drawString(g, field, cx, cy, bg, fgField);
            cx = dOrigin.x();
            cy += 1;

            // render equipment description
            if (item instanceof Equipment) {
                Equipment equipment = (Equipment) item;
                for (StatModifier statModifier : equipment.getStatModifiers().values()) {
                    value = StringUtils.capitalize(statModifier.getStat().name()) + " ";
                    game.drawString(g, value, cx, cy, bg, fgValue);
                    cx += value.length();

                    int modifier = statModifier.getModifier();
                    String sign = modifier >= 0 ? "+" : "-";
                    Color color = modifier >= 0 ? Color.GREEN : Color.RED;
                    value = sign + Math.abs(modifier);
                    game.drawString(g, value, cx, cy, bg, color);
                    cx = dOrigin.x();
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
                    value = sign + Math.abs(modifier);
                    game.drawString(g, value, cx, cy, bg, color);
                    cx += value.length();

                    value = " " + pair.getKey();
                    game.drawString(g, value, cx, cy, bg, fgValue);
                    cx = dOrigin.x();
                    cy += 1;

                }
            } else {
                value = "<none>";
                game.drawString(g, value, cx, cy, bg, fgValue);
                cx = dOrigin.x();
                cy += 1;
            }
            cy += 1;


        }


        // ------------------------------------------------
        // Draw menu borders
        // ------------------------------------------------
        drawMenuBorders(g, dOrigin, dWidth, dHeight);
        drawMenuBorders(g, bodyOrigin, width, height);

        // ------------------------------------------------
        // Draw header
        // ------------------------------------------------
        game.drawString(g, header, headerOrigin.x(), headerOrigin.y(), Color.BLACK, Color.WHITE);
        drawMenuBorders(g, headerOrigin, header.length(), 1);

    }

}
