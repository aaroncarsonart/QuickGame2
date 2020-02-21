package com.aaroncarsonart.quickgame2.map;

import imbroglio.Position2D;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private Position2D origin;
    private int width;
    private int height;
    private List<Position2D> doors;

    public Room(Position2D origin, int width, int height) {
        this.origin = origin;
        this.width = width;
        this.height = height;
        this.doors = new ArrayList<>();
    }

    public Position2D getOrigin() {
        return origin;
    }

    public void setOrigin(Position2D origin) {
        this.origin = origin;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<Position2D> getDoors() {
        return doors;
    }

    public void setDoors(List<Position2D> doors) {
        this.doors = doors;
    }

    public boolean contains(Position2D pos) {
        return origin.x() <= pos.x() && pos.x() < origin.x() + width
                && origin.y() <= pos.y() && pos.y() < origin.y() + height;
    }
}
