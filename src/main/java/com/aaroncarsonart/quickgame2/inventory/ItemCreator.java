package com.aaroncarsonart.quickgame2.inventory;

import com.aaroncarsonart.quickgame2.hero.Stat;
import com.aaroncarsonart.quickgame2.hero.StatModifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ItemCreator {

    public static List<Item> createTestInventory() {
        List<Item> inventory = new ArrayList<>();

        inventory.add(new Equipment("Longsword", 2, 20, EquipmentType.WEAPON,
                new StatModifier(Stat.ATTACK, 10)));
        inventory.add(new Equipment("WoodenShield", 3, 10, EquipmentType.SHIELD,
                new StatModifier(Stat.DEFENSE, 3)));
        inventory.add(new Equipment("Chain Mail", 20, 20, EquipmentType.ARMOR,
                new StatModifier(Stat.DEFENSE, 10)));
        inventory.add(new Equipment("Longsword", 2, 20, EquipmentType.WEAPON,
                new StatModifier(Stat.ATTACK, 10)));

        return inventory;
    }

    public static List<Item> loadEquipmentFromCSV() {
        List<Item> inventory = new ArrayList<>();
        ClassLoader classLoader = ItemCreator.class.getClassLoader();
        URL url = classLoader.getResource("equipment.csv");
        try {
            File file = new File(url.toURI());
            Scanner scanner = new Scanner(file);
            String headerLine = scanner.nextLine();
            String[] headers = headerLine.split(",", -1);
            Map<String, String> valuesMap = new HashMap<>();

            while(scanner.hasNextLine()) {
                String record = scanner.nextLine();
                String[] fields = record.split(",", -1);
                for (int i = 0; i < headers.length; i++) {
                    valuesMap.put(headers[i], fields[i]);
                }

                // read field values
                String name = valuesMap.get("name");
                if (name.isEmpty()) {
                    continue;
                }

                double weight = Double.parseDouble(valuesMap.get("weight"));
                int cost = Integer.parseInt(valuesMap.get("cost"));
                EquipmentType type = EquipmentType.valueOf(valuesMap.get("type"));

                // read stat modifiers
                List<StatModifier> statModifiers = new ArrayList<>();
                String field;

                for (Stat stat : Stat.values()) {
                    String statName = stat.name().toLowerCase();
                    field = valuesMap.get(statName);
                    if (field != null && field.length() > 0) {
                        int value = Integer.parseInt(field);
                        StatModifier modifier = new StatModifier(stat, value);
                        statModifiers.add(modifier);
                    }
                }

                // build item and add to inventory
                Equipment equipment = new Equipment(name, weight, cost, type, statModifiers);
                inventory.add(equipment);
            }
        } catch (URISyntaxException e) {
            System.err.println(e);
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }
        return inventory;
    }

    public static List<Item> loadRecoveryItemsFromCSV() {
        List<Item> inventory = new ArrayList<>();
        ClassLoader classLoader = ItemCreator.class.getClassLoader();
        URL url = classLoader.getResource("recovery_items.csv");
        try {
            File file = new File(url.toURI());
            Scanner scanner = new Scanner(file);
            String headerLine = scanner.nextLine();
            String[] headers = headerLine.split(",", -1);
            Map<String, String> valuesMap = new HashMap<>();

            while(scanner.hasNextLine()) {
                String record = scanner.nextLine();
                String[] fields = record.split(",", -1);
                for (int i = 0; i < headers.length; i++) {
                    valuesMap.put(headers[i], fields[i]);
                }

                // read field values
                String name = valuesMap.get("name");
                if (name.isEmpty()) {
                    continue;
                }

                double weight = Double.parseDouble(valuesMap.get("weight"));
                int cost = Integer.parseInt(valuesMap.get("cost"));

                int health = Integer.parseInt(valuesMap.get("health"));
                int mana = Integer.parseInt(valuesMap.get("mana"));
                int energy = Integer.parseInt(valuesMap.get("energy"));


                // build item and add to inventory
                RecoveryItem item = new RecoveryItem(name, weight, cost, health, mana, energy);
                inventory.add(item);
            }
        } catch (URISyntaxException e) {
            System.err.println(e);
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }
        return inventory;
    }


    public static void main(String[] args) {
        List<Item> inventory = loadEquipmentFromCSV();
        inventory.addAll(loadRecoveryItemsFromCSV());
        for (Item item : inventory) {
            System.out.println(item);
        }
    }
}
