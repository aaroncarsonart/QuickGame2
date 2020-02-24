package com.aaroncarsonart.quickgame2.monster;

import com.aaroncarsonart.quickgame2.inventory.Item;

public class Loot {

    private int chance;
    private Item item;

    public Loot(Item item, int chance) {
        this.chance = chance;
        this.item = item;
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
