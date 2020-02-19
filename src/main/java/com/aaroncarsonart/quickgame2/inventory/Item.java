package com.aaroncarsonart.quickgame2.inventory;

public class Item {

    protected String name;
    protected double weight;
    protected int cost;

    public Item(String name, double weight, int cost) {
        this.name = name;
        this.weight = weight;
        this.cost = cost;
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

    public String toString() {
        return String.format("name=\"%s\",weight=%.1f,cost=%d",
                name, weight, cost);
    }
}
