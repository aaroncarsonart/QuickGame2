package com.aaroncarsonart.quickgame2.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * An inventory represents a collection of Items.
 * Ea
 */
public class Inventory {
    public class Slot {
        int quantity = 0;
        Item item = null;

        public int getQuantity() {
            return quantity;
        }

        public Item getItem() {
            return item;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public void setItem(Item item) {
            this.item = item;
        }

        public String getLabel() {
            String label;
            if (item == null || quantity == 0) {
                label = "";
            } else if (quantity > 1) {
//                label = String.format("x%d %s \n", quantity, item.name);
                label = String.format("%s x%d\n", item.name, quantity);
            } else {
                label = item.name;
            }
            return label;
        }
    }

    private Slot[] slots;
    private int size;

    public Inventory(int size) {
        this.size = size;
        slots = new Slot[size];
        for (int i = 0; i < size; i++) {
            slots[i] = new Slot();
        }
    }

    public void sort() {
        Comparator<Slot> comparator = Comparator.comparing(s -> s.item, Comparator.nullsLast(Comparator.naturalOrder()));
        Arrays.sort(slots, comparator);
    }

    public void reverseSort() {
        Comparator<Slot> comparator = Comparator.comparing(s -> s.item, Comparator.nullsLast(Comparator.reverseOrder()));
        Arrays.sort(slots, comparator);
    }

    public boolean add(Item item) {
        // check if item is already in list
        if (item.isStackable()) {
            for (int i = 0; i < size; i++) {
                Slot slot = slots[i];
                if (slot.item != null && slot.item.equals(item)) {
                    slot.quantity += 1;
                    return true;
                }
            }
        }
        // otherwise, add to first empty slot
        for (int i = 0; i < size; i++) {
            Slot slot = slots[i];
            if (slot.item == null) {
                slot.item = item;
                slot.quantity += 1;
                return true;
            }
        }
        return false;
    }

    public boolean remove(Item item) {
        for (int i = 0; i < size; i++) {
            Slot slot = slots[i];
            if (slot.item != null && slot.item.equals(item)) {
                if (slot.quantity == 1) {
                    slot.item = null;
                    slot.quantity = 0;
                    //slots[i] = new Slot();
                } else {
                    slot.quantity -= 1;
                }
                return true;
            }
        }
        return false;
    }

    public boolean remove(Item item, Slot slot) {
        if (slot.item != null && slot.item.equals(item)) {
            if (slot.quantity == 1) {
                slot.item = null;
                slot.quantity = 0;
            } else {
                slot.quantity -= 1;
            }
            return true;
        }
        return false;
    }



    public Slot[] getSlots() {
        return slots;
    }

    public static void main(String[] args) {
        Inventory inventory = new Inventory(10);

    }
}
