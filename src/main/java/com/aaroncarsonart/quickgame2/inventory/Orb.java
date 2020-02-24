package com.aaroncarsonart.quickgame2.inventory;

import com.aaroncarsonart.quickgame2.util.StringUtils;

public class Orb extends Item {

    private OrbType type;

    public Orb(OrbType type) {
        super(StringUtils.capitalize(type.name()), 1, -1, false);
        this.type = type;
    }

    public OrbType getType() {
        return type;
    }

    public void setType(OrbType type) {
        this.type = type;
    }
}
