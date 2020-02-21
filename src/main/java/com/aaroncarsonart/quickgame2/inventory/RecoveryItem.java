package com.aaroncarsonart.quickgame2.inventory;

import java.util.ArrayList;
import java.util.List;

public class RecoveryItem extends Item {

    private int health;
    private int mana;
    private int energy;

    public RecoveryItem(String name, double weight, int cost, boolean stackable, int health, int mana, int energy) {
        super(name, weight, cost, stackable);
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
        List<String> values = new ArrayList<>();
        if (health > 0) {
            values.add("health+" + health);
        }
        if (mana > 0) {
            values.add("mana+" + mana);
        }
        if (energy > 0) {
            values.add("energy+" + energy);
        }
        return String.format("%s,recovery=%s",
                super.toString(), values);
    }
}
