package com.aaroncarsonart.quickgame2.inventory;

import com.aaroncarsonart.quickgame2.hero.Stat;
import com.aaroncarsonart.quickgame2.hero.StatModifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemCreator {

    private static int itemId = 0;
    public static Map<String, Item> ITEM_MAP = loadItemsFromCSV();
    public static List<Item> ITEM_LIST = ITEM_MAP.values().stream()
            .sorted(Comparator.comparing(Item::getName))
            .collect(Collectors.toList());
    public static List<Orb> ORBS_LIST = loadOrbs();

    private static Map<String, Item> loadItemsFromCSV() {
        Map<String, Item> itemMap = new HashMap<>();

        List<Item> equipment = loadEquipmentFromCSV();
        List<Item> recoveryItems = loadRecoveryItemsFromCSV();

        equipment.forEach(item -> itemMap.put(item.getName(), item));
        recoveryItems.forEach(item -> itemMap.put(item.getName(), item));

        return itemMap;
    }

    public static List<Item> loadEquipmentFromCSV() {
        List<Item> inventory = new ArrayList<>();
        ClassLoader classLoader = ItemCreator.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("equipment.csv");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String headerLine = reader.readLine();
            String[] headers = headerLine.split(",", -1);
            Map<String, String> valuesMap = new HashMap<>();

            while(reader.ready()) {
                String record = reader.readLine();
                String[] fields = record.split(",", -1);
                for (int i = 0; i < headers.length; i++) {
                    valuesMap.put(headers[i], fields[i]);
                }

                // read field values
                String name = valuesMap.get("name");
                if (name.isEmpty()) {
                    continue;
                }

//                int id = Integer.parseInt(valuesMap.get("id"));
                double weight = Double.parseDouble(valuesMap.get("weight"));
                int cost = Integer.parseInt(valuesMap.get("cost"));
                boolean stackable = Boolean.parseBoolean(valuesMap.get("stackable"));
                EquipmentType type = EquipmentType.valueOf(valuesMap.get("type"));

                int minDepth = Integer.parseInt(valuesMap.get("minDepth"));
                int maxDepth = Integer.parseInt(valuesMap.get("maxDepth"));

                // read stat modifiers
                LinkedHashMap<Stat, StatModifier> statModifiers = new LinkedHashMap<>();
                String field;

                for (Stat stat : Stat.values()) {
                    String statName = stat.name().toLowerCase();
                    field = valuesMap.get(statName);
                    if (field != null && field.length() > 0) {
                        int value = Integer.parseInt(field);
                        StatModifier modifier = new StatModifier(stat, value);
                        statModifiers.put(stat, modifier);
                    }
                }

                // build item and add to inventory
                Equipment equipment = new Equipment(name, weight, cost, stackable, type, statModifiers);
                equipment.setMinDepth(minDepth);
                equipment.setMaxDepth(maxDepth);
//                equipment.setId(itemId++);
                inventory.add(equipment);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return inventory;
    }

    public static List<Item> loadRecoveryItemsFromCSV() {
        List<Item> inventory = new ArrayList<>();
        ClassLoader classLoader = ItemCreator.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("recovery_items.csv");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String headerLine = reader.readLine();
            String[] headers = headerLine.split(",", -1);
            Map<String, String> valuesMap = new HashMap<>();

            while(reader.ready()) {
                String record = reader.readLine();
                String[] fields = record.split(",", -1);
                for (int i = 0; i < headers.length; i++) {
                    valuesMap.put(headers[i], fields[i]);
                }

                // read field values
                String name = valuesMap.get("name");
                if (name.isEmpty()) {
                    continue;
                }

//                int id = Integer.parseInt(valuesMap.get("id"));
                double weight = Double.parseDouble(valuesMap.get("weight"));
                int cost = Integer.parseInt(valuesMap.get("cost"));
                boolean stackable = Boolean.parseBoolean(valuesMap.get("stackable"));

                int health = Integer.parseInt(valuesMap.get("health"));
                int mana = Integer.parseInt(valuesMap.get("mana"));
                int energy = Integer.parseInt(valuesMap.get("energy"));

                int minDepth = Integer.parseInt(valuesMap.get("minDepth"));
                int maxDepth = Integer.parseInt(valuesMap.get("maxDepth"));

                // build item and add to inventory
                RecoveryItem item = new RecoveryItem(name, weight, cost, stackable, health, mana, energy);
                item.setMinDepth(minDepth);
                item.setMaxDepth(maxDepth);
                inventory.add(item);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return inventory;
    }

    public static List<Orb> loadOrbs() {
        List<Orb> inventory = new ArrayList<>();

        inventory.add(new Orb(OrbType.YELLOW));
        inventory.add(new Orb(OrbType.GREEN));
        inventory.add(new Orb(OrbType.BLUE));
        inventory.add(new Orb(OrbType.PURPLE));
        inventory.add(new Orb(OrbType.RED));

        return inventory;
    }


    public static void main(String[] args) {
        List<Item> itemList = loadEquipmentFromCSV();
        itemList.addAll(loadRecoveryItemsFromCSV());
        for (Item item : itemList) {
            System.out.println(item);
        }

        Inventory inventory = new Inventory(20);
        for (Item item : itemList) {
            inventory.add(item);
            inventory.add(item);
        }
        inventory.sort();
        for (Inventory.Slot slot : inventory.getSlots()) {
            if (slot.item == null) {
                System.out.println();
            } else if (slot.quantity > 1) {
                System.out.printf("%s x%d\n", slot.item.name, slot.quantity);
            } else {
                System.out.println(slot.item.name);
            }
        }
    }
}
