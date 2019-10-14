package com.aaroncarsonart.quickgame2.menu;

import com.aaroncarsonart.quickgame2.CharGrid;
import com.aaroncarsonart.quickgame2.PlayerAction;
import imbroglio.Position2D;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private List<MenuItem> menuItems;
    private MenuLayout menuLayout;
    private int index;
//    private int maxWidth;
//    private int maxHeight;
//    private int startDisplayY;
//    private int startDisplayX;
    private Position2D origin;

    private Callback cancelCallback;

    public Menu(int y, int x, MenuLayout menuLayout, Callback cancelCallback) {
        this(new Position2D(x, y), menuLayout, cancelCallback);
    }

    public Menu(Position2D origin, MenuLayout menuLayout, Callback cancelCallback) {
        this.menuItems = new ArrayList<>();
        this.index = 0;
        this.menuLayout = menuLayout;
        this.cancelCallback = cancelCallback;
        this.origin = origin;
    }

    public void addMenuItem(MenuItem menuItem) {
        this.menuItems.add(menuItem);
    }

    public void next() {
        if (index < menuItems.size() - 1) {
            index ++;
        }
    }

    public void previous() {
        if (index > 0) {
            index --;
        }
    }

    public void updateMenu(PlayerAction playerAction) {
        respondToPlayerInputs(playerAction);
    }

    public void respondToPlayerInputs(PlayerAction playerAction) {
        switch (playerAction) {
            case UP:
            case LEFT:
                previous();
                break;
            case DOWN:
            case RIGHT:
                next();
                break;
            case OK:
                MenuItem selectedMenuItem = menuItems.get(index);
                selectedMenuItem.callback.execute();
                break;
            default:
            case CANCEL:
                cancelCallback.execute();
                break;
        }
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public MenuLayout getMenuLayout() {
        return menuLayout;
    }

    public void setMenuLayout(MenuLayout menuLayout) {
        this.menuLayout = menuLayout;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Position2D getOrigin() {
        return origin;
    }

    public void setOrigin(Position2D origin) {
        this.origin = origin;
    }

    public Callback getCancelCallback() {
        return cancelCallback;
    }

    public void setCancelCallback(Callback cancelCallback) {
        this.cancelCallback = cancelCallback;
    }

    public MenuItem getMenuItem(int index) {
        return menuItems.get(index);
    }
}
