package com.aaroncarsonart.quickgame2.inventory;

import com.aaroncarsonart.quickgame2.util.StringUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;

public class Item implements Comparable<Item> {

    protected String name;
    protected double weight;
    protected int cost;
    protected boolean stackable;
    private boolean addedToMap;

    private int minDepth;
    private int maxDepth;

    public Item(String name, double weight, int cost, boolean stackable) {
        this.name = name;
        this.weight = weight;
        this.cost = cost;
        this.stackable = stackable;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Item) {
            Item item = (Item) obj;
            return this.name.equals(item.name);
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public int getCost() {
        return cost;
    }

    public boolean isStackable() {
        return stackable;
    }

    public int getMinDepth() {
        return minDepth;
    }

    public void setMinDepth(int minDepth) {
        this.minDepth = minDepth;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public boolean isAddedToMap() {
        return addedToMap;
    }

    public void setAddedToMap(boolean addedToMap) {
        this.addedToMap = addedToMap;
    }

    public String toString() {
        return String.format("name=\"%s\",weight=%.1f,cost=%d,stackable=%s",
                name, weight, cost, stackable);
    }

    @Override
    public int compareTo(Item item) {
        return name.compareTo(item.name);
    }

    public static String getItemType(Item item) {
        if (item instanceof RecoveryItem) {
            return "Recovery";
        } else if (item instanceof Equipment) {
            Equipment equipment = (Equipment) item;
            EquipmentType equipmentType = equipment.getType();
            String name = equipmentType.name();
            return StringUtils.capitalize(name);
        }
        return "Item";
    }

    public Color getColor() {
        return Color.WHITE;
    }
    public char getSprite() {
        return '?';
    }

    /**
     * Override this method to implement item functionality.
     * @param target An optional parameter to use.
     */
    public void use(Object target) {

    }
}
