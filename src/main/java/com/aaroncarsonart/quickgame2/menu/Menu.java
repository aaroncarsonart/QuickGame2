package com.aaroncarsonart.quickgame2.menu;

import com.aaroncarsonart.quickgame2.PlayerAction;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    protected List<MenuItem> menuItems;
    private MenuView menuView;
    private MenuLayout menuLayout;
    protected int index;

    private Callback cancelCallback;

    public Menu(MenuView menuView, MenuLayout menuLayout, Callback cancelCallback) {
        this.menuView = menuView;
        this.menuItems = new ArrayList<>();
        this.index = 0;
        this.menuLayout = menuLayout;
        this.cancelCallback = cancelCallback;
    }

    public void addMenuItem(MenuItem menuItem) {
        this.menuItems.add(menuItem);
    }

    public void next() {
        index +=1;
        if (index > menuItems.size() - 1) {
            index = 0;
        }
//        if (index < menuItems.size() - 1) {
//            index ++;
//        }
    }

    public void previous() {
        index -=1;
        if (index < 0) {
            index = menuItems.size() - 1;
        }
//        if (index > 0) {
//            index --;
//        }
    }

    public MenuView getMenuView() {
        return menuView;
    }

    public void setMenuView(MenuView menuView) {
        this.menuView = menuView;
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
            case CANCEL:
                cancelCallback.execute();
                break;
            default:
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
