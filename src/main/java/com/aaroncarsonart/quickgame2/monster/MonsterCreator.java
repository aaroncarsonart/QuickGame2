package com.aaroncarsonart.quickgame2.monster;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MonsterCreator {

    public static final Map<String, Monster> monsterMap = loadMonstersFromCSV();
    public static final List<Monster> MONSTER_LIST = monsterMap.values().stream()
            .sorted(Comparator.comparing(Monster::getName))
            .collect(Collectors.toList());

    public static Map<String, Monster> loadMonstersFromCSV() {
        Map<String, Monster> monsterMap = new HashMap<>();
        ClassLoader classLoader = MonsterCreator.class.getClassLoader();
        URL url = classLoader.getResource("monsters.csv");
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

                int health = Integer.parseInt(valuesMap.get("health"));

                int attack = Integer.parseInt(valuesMap.get("attack"));
                int defense = Integer.parseInt(valuesMap.get("defense"));
                int accuracy = Integer.parseInt(valuesMap.get("accuracy"));
                int evade = Integer.parseInt(valuesMap.get("evade"));

                int aggression = Integer.parseInt(valuesMap.get("aggression"));

                int minEncounter = Integer.parseInt(valuesMap.get("minEncounter"));
                int maxEncounter = Integer.parseInt(valuesMap.get("maxEncounter"));

                int gold = Integer.parseInt(valuesMap.get("gold"));
                int exp = Integer.parseInt(valuesMap.get("exp"));
                // TODO parse loot string later
                String loot = valuesMap.get("loot");
                int minDepth = Integer.parseInt(valuesMap.get("minDepth"));
                int maxDepth = Integer.parseInt(valuesMap.get("maxDepth"));

                int r = Integer.parseInt(valuesMap.get("r"));
                int g = Integer.parseInt(valuesMap.get("g"));
                int b = Integer.parseInt(valuesMap.get("b"));

                Color color = new Color(r, g, b);
                char sprite = valuesMap.get("sprite").charAt(0);

                // build item and add to inventory
                Monster monster = new Monster();
                monster.setName(name);
                monster.setHealth(health);
                monster.setMaxHealth(health);
                monster.setAttack(attack);
                monster.setDefense(defense);
                monster.setAccuracy(accuracy);
                monster.setMinEncounter(minEncounter);
                monster.setMaxEncounter(maxEncounter);
                monster.setAggression(aggression);
                monster.setBaseAggression(aggression);
                monster.setEvade(evade);
                monster.setGold(gold);
                // TODO add loot functionality
                monster.setLoot(null);
                monster.setExp(exp);
                monster.setMinDepth(minDepth);
                monster.setMaxDepth(maxDepth);
                monster.setColor(color);
                monster.setSprite(sprite);

                monsterMap.put(name, monster);
            }
        } catch (URISyntaxException e) {
            System.err.println(e);
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }
        return monsterMap;
    }

    public static final void main(String[] args) {
        ClassLoader classLoader = MonsterCreator.class.getClassLoader();
        URL url = classLoader.getResource("monster.csv");
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
            }
        } catch (URISyntaxException e) {
            System.err.println(e);
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }

    }
}
