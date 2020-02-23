package com.aaroncarsonart.quickgame2.map;

import imbroglio.Position2D;

public class Stairs {
    private Position2D pos;
    private GameMap targetGameMap;
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

    public GameMap getTargetGameMap() {
        return targetGameMap;
    }

    public void setTargetGameMap(GameMap targetGameMap) {
        this.targetGameMap = targetGameMap;
    }

    public char getSprite() {
        return sprite;
    }

    public void setSprite(char sprite) {
        this.sprite = sprite;
    }
}
