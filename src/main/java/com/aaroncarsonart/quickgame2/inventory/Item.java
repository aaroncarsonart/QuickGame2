package com.aaroncarsonart.quickgame2.inventory;

import com.sun.xml.internal.ws.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;

public class Item implements Comparable<Item> {

    protected String name;
    protected double weight;
    protected int cost;
    protected boolean stackable;
    protected int id;

    public Item(String name, double weight, int cost, boolean stackable) {
        this.name = name;
        this.weight = weight;
        this.cost = cost;
        this.stackable = stackable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

}
