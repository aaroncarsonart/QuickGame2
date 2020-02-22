package com.aaroncarsonart.quickgame2.map;

import com.aaroncarsonart.quickgame2.Constants;
import com.aaroncarsonart.quickgame2.inventory.Item;
import com.aaroncarsonart.quickgame2.monster.Monster;
import imbroglio.Position2D;

import java.util.Map;

public class GameMap {

    private int width;
    private int height;
    private char[][] cells;
    private char[][] visible;

    private Stairs downstairs;
    private Stairs upstairs;

    private Map<Position2D, Item> items;
    private Map<Position2D, Monster> monsters;

    private ColorSet colorSet;

    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new char[height][width];
        this.visible = new char[height][width];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[y][x] = Constants.EMPTY;
                visible[y][x] = Constants.UNKNOWN;
            }
        }
    }

    public char getCell(int y, int x) {
        return cells[y][x];
    }

    public char getCell(Position2D pos) {
        return cells[pos.y()][pos.x()];
    }

    public void setCell(int y, int x, char c) {
        cells[y][x] = c;
    }

    public void setCell(Position2D pos, char c) {
        cells[pos.y()][pos.x()] = c;
    }

    public char getVisible(int y, int x) {
        return visible[y][x];
    }

    public char getVisible(Position2D pos) {
        return visible[pos.y()][pos.x()];
    }

    public void setVisible(int y, int x, char c) {
        visible[y][x] = c;
    }

    public void setVisible(Position2D pos, char c) {
        visible[pos.y()][pos.x()] = c;
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

    public char[][] getCells() {
        return cells;
    }

    public void setCells(char[][] cells) {
        this.cells = cells;
    }

    public char[][] getVisible() {
        return visible;
    }

    public void setVisible(char[][] visible) {
        this.visible = visible;
    }

    public Stairs getDownstairs() {
        return downstairs;
    }

    public void setDownstairs(Stairs downstairs) {
        this.downstairs = downstairs;
    }

    public Stairs getUpstairs() {
        return upstairs;
    }

    public void setUpstairs(Stairs upstairs) {
        this.upstairs = upstairs;
    }

    public Map<Position2D, Item> getItems() {
        return items;
    }

    public void setItems(Map<Position2D, Item> items) {
        this.items = items;
    }

    public Map<Position2D, Monster> getMonsters() {
        return monsters;
    }

    public void setMonsters(Map<Position2D, Monster> monsters) {
        this.monsters = monsters;
    }

    public ColorSet getColorSet() {
        return colorSet;
    }

    public void setColorSet(ColorSet colorSet) {
        this.colorSet = colorSet;
    }
}
