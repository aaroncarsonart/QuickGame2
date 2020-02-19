package com.aaroncarsonart.quickgame2.inventory;

public class RecoveryItem extends Item {

    private int health;
    private int mana;
    private int energy;

    public RecoveryItem(String name, double weight, int cost, int health, int mana, int energy) {
        super(name, weight, cost);
        this.health = health;
        this.mana = mana;
        this.energy = energy;
    }

    public int getHealth() {
        return health;
    }

    public int getMana() {
        return mana;
    }

    public int getEnergy() {
        return energy;
    }

    public String toString() {
        return String.format("%s,recovery=[health+%d,mana+%d,energy+%d]",
                super.toString(), health, mana, energy);
    }
}
