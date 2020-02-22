package com.aaroncarsonart.quickgame2.map;

import imbroglio.Position2D;

public class Stairs {
    private Position2D pos;
    private GameMap target;
    private char sprite;

    public Stairs(Position2D pos, char sprite) {
        this.pos = pos;
        this.sprite = sprite;
    }

    public Position2D getPos() {
        return pos;
    }

    public void setPos(Position2D pos) {
        this.pos = pos;
    }

    public GameMap getTarget() {
        return target;
    }

    public void setTarget(GameMap target) {
        this.target = target;
    }
}
