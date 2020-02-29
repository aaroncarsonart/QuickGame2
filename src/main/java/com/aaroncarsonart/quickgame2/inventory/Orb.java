package com.aaroncarsonart.quickgame2.inventory;

import com.aaroncarsonart.quickgame2.util.StringUtils;

import java.awt.Color;

public class Orb extends Item {

    private OrbType type;

    public Orb(OrbType type) {
        super(StringUtils.capitalize(type.name()) + " Orb", 1, -1, false);
        this.type = type;
    }

    public OrbType getType() {
        return type;
    }

    public void setType(OrbType type) {
        this.type = type;
    }

    @Override
    public char getSprite() {
        return '*';
    }

    @Override
    public Color getColor() {
        switch (type) {
            case YELLOW: return Color.YELLOW;
            case GREEN: return Color.GREEN;
            case BLUE: return Color.CYAN;
            case PURPLE: return Color.MAGENTA;
            case RED: return Color.RED;
            default: return null;
        }
    }
}
