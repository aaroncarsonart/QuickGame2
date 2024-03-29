package com.aaroncarsonart.quickgame2.hero;

import com.aaroncarsonart.quickgame2.Constants;
import com.aaroncarsonart.quickgame2.inventory.Inventory;
import com.aaroncarsonart.quickgame2.inventory.Item;
import com.aaroncarsonart.quickgame2.inventory.ItemCreator;

import java.util.ArrayList;
import java.util.List;

public class HeroCreator {

    public static Hero createDefaultHero() {
        Hero hero = new Hero();
        hero.setName("James");
        hero.setHeroClass(HeroClass.FIGHTER);
        hero.setHeroRace(HeroRace.HUMAN);
        hero.setLevel(1);
        hero.setExp(0);

        int health = 0;
        for (int i = 0; i < 3; i++) {
            health += 1 + Constants.RNG.nextInt(10) + hero.getModifiedStamina() / 2;
        }
        hero.setHealth(health);
        hero.setMaxHealth(health);

        int mana = 1 + Constants.RNG.nextInt(5) + hero.getModifiedWisdom() / 2;
        hero.setMana(mana);
        hero.setMaxMana(mana);

        int energy = 100;
        hero.setEnergy(energy);
        hero.setMaxEnergy(energy);

        hero.setStrength(1);
        hero.setStamina(1);
        hero.setAgility(1);
        hero.setIntelligence(1);
        hero.setWisdom(1);
        hero.setCharisma(1);

        int statPoints = 4 * 6;
        for (int i = 0; i < statPoints; i++) {
            int next = Constants.RNG.nextInt(6);
            if (next == 0) {
                int newValue = hero.getStrength() + 1;
                hero.setStrength(newValue);
            } else if (next == 1) {
                int newValue = hero.getStamina() + 1;
                hero.setStamina(newValue);
            } else if (next == 2) {
                int newValue = hero.getAgility() + 1;
                hero.setAgility(newValue);
            } else if (next == 3) {
                int newValue = hero.getIntelligence() + 1;
                hero.setIntelligence(newValue);
            } else if (next == 4) {
                int newValue = hero.getWisdom() + 1;
                hero.setWisdom(newValue);
            } else if (next == 5) {
                int newValue = hero.getCharisma() + 1;
                hero.setCharisma(newValue);
            } else {
                System.out.println(next);
            }
        }

//        hero.setStrength(Constants.RNG.nextInt(6)
//                + Constants.RNG.nextInt(6)
//                + Constants.RNG.nextInt(6));
//        hero.setStamina(Constants.RNG.nextInt(6)
//                + Constants.RNG.nextInt(6)
//                + Constants.RNG.nextInt(6));
//        hero.setAgility(Constants.RNG.nextInt(6)
//                + Constants.RNG.nextInt(6)
//                + Constants.RNG.nextInt(6));
//        hero.setIntelligence(Constants.RNG.nextInt(6)
//                + Constants.RNG.nextInt(6)
//                + Constants.RNG.nextInt(6));
//        hero.setWisdom(Constants.RNG.nextInt(6)
//                + Constants.RNG.nextInt(6)
//                + Constants.RNG.nextInt(6));
//        hero.setCharisma(Constants.RNG.nextInt(6)
//                + Constants.RNG.nextInt(6)
//                + Constants.RNG.nextInt(6));

        hero.setGold(0);

        // ---------------------------------------------------------
        // populate mock inventory
        // ---------------------------------------------------------
        Inventory inventory = new Inventory(60);
        inventory.add(ItemCreator.ITEM_MAP.get("Herb"));
        inventory.add(ItemCreator.ITEM_MAP.get("Herb"));
        inventory.add(ItemCreator.ITEM_MAP.get("Herb"));
        inventory.add(ItemCreator.ITEM_MAP.get("Herb"));
        inventory.add(ItemCreator.ITEM_MAP.get("Herb"));
        inventory.add(ItemCreator.ITEM_MAP.get("Herb"));
        inventory.add(ItemCreator.ITEM_MAP.get("Herb"));
        inventory.add(ItemCreator.ITEM_MAP.get("Herb"));
        inventory.add(ItemCreator.ITEM_MAP.get("Herb"));
        inventory.add(ItemCreator.ITEM_MAP.get("Herb"));

        inventory.add(ItemCreator.ITEM_MAP.get("Food"));
        inventory.add(ItemCreator.ITEM_MAP.get("Food"));
        inventory.add(ItemCreator.ITEM_MAP.get("Food"));
        inventory.add(ItemCreator.ITEM_MAP.get("Food"));
        inventory.add(ItemCreator.ITEM_MAP.get("Food"));

        inventory.add(ItemCreator.ITEM_MAP.get("Drink"));
        inventory.add(ItemCreator.ITEM_MAP.get("Drink"));
        inventory.add(ItemCreator.ITEM_MAP.get("Drink"));
        inventory.add(ItemCreator.ITEM_MAP.get("Drink"));
        inventory.add(ItemCreator.ITEM_MAP.get("Drink"));

//        inventory.add(ItemCreator.ITEM_MAP.get("Dagger"));
//        inventory.add(ItemCreator.ITEM_MAP.get("Longsword"));
//        inventory.add(ItemCreator.ITEM_MAP.get("Buckler"));
//        inventory.add(ItemCreator.ITEM_MAP.get("Wooden Shield"));
//        inventory.add(ItemCreator.ITEM_MAP.get("Cloth Cap"));
//        inventory.add(ItemCreator.ITEM_MAP.get("Leather Helm"));
//        inventory.add(ItemCreator.ITEM_MAP.get("Clothes"));
//        inventory.add(ItemCreator.ITEM_MAP.get("Padded Armor"));
//        inventory.add(ItemCreator.ITEM_MAP.get("Ring of Blessing"));
//        inventory.add(ItemCreator.ITEM_MAP.get("Ring of Strength"));

        hero.setInventory(inventory);

        return hero;
    }
}
