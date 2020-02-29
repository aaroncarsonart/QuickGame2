package com.aaroncarsonart.quickgame2.map;

import com.aaroncarsonart.quickgame2.Constants;
import com.aaroncarsonart.quickgame2.inventory.Equipment;
import com.aaroncarsonart.quickgame2.inventory.Item;
import com.aaroncarsonart.quickgame2.inventory.ItemCreator;
import com.aaroncarsonart.quickgame2.inventory.Orb;
import com.aaroncarsonart.quickgame2.inventory.RecoveryItem;
import com.aaroncarsonart.quickgame2.monster.Monster;
import com.aaroncarsonart.quickgame2.monster.MonsterCreator;
import imbroglio.Difficulty;
import imbroglio.Maze;
import imbroglio.Position2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GameMapCreator {

    private static Random rng = Constants.RNG;
    private static int nextType = 0;
    private static int nextColor = 2;

    private static Map<Integer, Orb> orbLocations = loadOrbLocations();

    public static Map<Integer, Orb> loadOrbLocations() {
        Map<Integer, Orb> orbLocations = new HashMap<>();
        List<Orb> orbs = ItemCreator.loadOrbs();

        List<Integer> depths = new ArrayList<>();
        for (int i = 1; i <= 19; i++) {
            depths.add(i);
        }
        List<Integer> depthsToUse = new ArrayList<>();
        for (int i = 0; i < orbs.size() - 1; i++) {
            int depth = depths.get(rng.nextInt(depths.size()));
            depthsToUse.add(depth);
        }
        depthsToUse.add(20);
        Collections.sort(depthsToUse);
//        for (Integer i : depthsToUse) {
//            System.out.println(i);
//        }

        for (int i = 0; i < orbs.size(); i++) {
            int depth = depthsToUse.get(i);
            Orb orb = orbs.get(i);
            orbLocations.put(depth, orb);
        }
        return orbLocations;
    }

    public static GameMap createGameMap(int width, int height, int depth) {
        GameMap gameMap = new GameMap(width, height);
        gameMap.setDepth(depth);

        // determine map type and color
        if (1 <= depth && depth < 4) {
            generateMapType(gameMap, MapType.DUNGEON);
            gameMap.setColorSet(ColorSet.BROWN);
        } else if (4 <= depth && depth < 7) {
            generateMapType(gameMap, MapType.CAVES);
            gameMap.setColorSet(ColorSet.PURPLE);
        } else if (7 <= depth && depth < 10) {
            generateMapType(gameMap, MapType.DOUBLE_MAZE);
            gameMap.setColorSet(ColorSet.BLUE);
        } else if (depth == 10) {
            generateMapType(gameMap, MapType.MAZE);
            gameMap.setColorSet(ColorSet.RED);
        } else if (11 <= depth && depth < 14) {
            generateMapType(gameMap, MapType.DUNGEON);
            gameMap.setColorSet(ColorSet.YELLOW);
        } else if (14 <= depth && depth < 17) {
            generateMapType(gameMap, MapType.CAVES);
            gameMap.setColorSet(ColorSet.GREEN);
        } else if (17 <= depth && depth < 20) {
            generateMapType(gameMap, MapType.DOUBLE_MAZE);
            gameMap.setColorSet(ColorSet.BROWN);
        } else if (depth == 20) {
            generateMapType(gameMap, MapType.MAZE);
            gameMap.setColorSet(ColorSet.PURPLE);
        }

        // populate stairs, items, monsters
        List<Position2D> emptyPositions = gameMap.getEmptyPositions();

        // ------------------------------------------------
        // Add stairs
        // ------------------------------------------------
        if (depth != 1) {
            Position2D upPos = emptyPositions.remove(rng.nextInt(emptyPositions.size()));
            Stairs upstairs = new Stairs(upPos, Constants.UPSTAIRS);
            gameMap.setUpstairs(upstairs);
            gameMap.setCell(upPos, upstairs.getSprite());
        }
        if (depth != 20) {
            Position2D downPos = emptyPositions.remove(rng.nextInt(emptyPositions.size()));
            Stairs downstairs = new Stairs(downPos, Constants.DOWNSTAIRS);
            gameMap.setDownstairs(downstairs);
            gameMap.setCell(downPos, downstairs.getSprite());
        }

        // ------------------------------------------------
        // Add orbs
        // ------------------------------------------------
        Orb orb = orbLocations.get(depth);
        if (orb != null) {
            Position2D orbPos = emptyPositions.remove(rng.nextInt(emptyPositions.size()));
            gameMap.getItems().put(orbPos, orb);
        }

        // ------------------------------------------------
        // Add monsters
        // ------------------------------------------------

        Predicate<Monster> isValidPredicate;
        if (depth == 10 || depth == 20) {
            // use monsters from all previous floors for maze levels
            isValidPredicate = m -> m.getMinDepth() <= depth;
        } else {
            isValidPredicate = m -> m.getMinDepth() <= depth && depth <= m.getMaxDepth();
        }
        List<Monster> validMonsters = MonsterCreator.MONSTER_LIST.stream()
                .filter(isValidPredicate)
                .collect(Collectors.toList());


        int monsterGroupsToAdd = 10;

        // special handling for dungeons to ensure monsters in rooms;
        if (gameMap.getMapType() == MapType.DUNGEON) {
            monsterGroupsToAdd = 5;
            List<Position2D> candidatePositions = getCandidateRoomPositions(gameMap, 3);
            while (!candidatePositions.isEmpty() && !validMonsters.isEmpty()) {
                Monster next = validMonsters.get(rng.nextInt(validMonsters.size()));
                int encounter = next.getMinEncounter();
                if (next.getMinEncounter() != next.getMaxEncounter()) {
                    encounter += rng.nextInt(next.getMaxEncounter() - next.getMinEncounter());
                }
                for (int j = 0; ((gameMap.getMapType() != MapType.MAZE && (j < encounter)) || (j < 1)) && !candidatePositions.isEmpty(); j++) {
                    Monster monster = next.copy();
                    Position2D pos = candidatePositions.remove(rng.nextInt(candidatePositions.size()));
                    monster.setPos(pos);
                    gameMap.getMonsterMap().put(pos, monster);
                }
            }
        }

        // other maps handling (plus some handling for dungeons)
        for (int i = 0; i < monsterGroupsToAdd && !validMonsters.isEmpty(); i++) {
            Monster next = validMonsters.get(rng.nextInt(validMonsters.size()));
            int encounter = next.getMinEncounter();
            if (next.getMinEncounter() != next.getMaxEncounter()) {
                encounter += rng.nextInt(next.getMaxEncounter() - next.getMinEncounter());
            }
            for (int j = 0; ((gameMap.getMapType() != MapType.MAZE && (j < encounter)) || (j < 1)) && !emptyPositions.isEmpty(); j++) {
                Monster monster = next.copy();
                Position2D pos = emptyPositions.remove(rng.nextInt(emptyPositions.size()));
                monster.setPos(pos);
                gameMap.getMonsterMap().put(pos, monster);
            }
        }

        // ------------------------------------------------
        // Add items
        // ------------------------------------------------

        // add recovery items
        List<Item> candidateItems = ItemCreator.ITEM_LIST.stream()
                .filter(i -> i instanceof RecoveryItem)
                .filter(i -> i.getMinDepth() <= depth && depth <= i.getMaxDepth())
                .collect(Collectors.toCollection(ArrayList::new));

        List<Item> higherLevelItems = ItemCreator.ITEM_LIST.stream()
                .filter(i -> i instanceof RecoveryItem)
                .filter(i -> depth < i.getMinDepth())
                .collect(Collectors.toCollection(ArrayList::new));

        int itemsToAddPerLevel = 1 + rng.nextInt(6);

        for (int i = 0; i < itemsToAddPerLevel && !candidateItems.isEmpty(); i++) {
            List<Item> items;
            int chance = 1 + rng.nextInt(20);
            if (chance == 20) {
                if (!higherLevelItems.isEmpty()) {
                    items = higherLevelItems;
                } else {
                    items = candidateItems;
                }
            } else {
                items = candidateItems;
            }
            Item item = items.get(rng.nextInt(items.size()));
            Position2D itemPos = emptyPositions.remove(rng.nextInt(emptyPositions.size()));
            gameMap.getItems().put(itemPos, item);
        }

        // add equipment
        candidateItems = ItemCreator.ITEM_LIST.stream()
                .filter(i -> i instanceof Equipment)
                .filter(i -> i.getMinDepth() <= depth && depth <= i.getMaxDepth())
                .filter(i -> !i.isAddedToMap())
                .collect(Collectors.toCollection(ArrayList::new));

        higherLevelItems = ItemCreator.ITEM_LIST.stream()
                .filter(i -> i instanceof Equipment)
                .filter(i -> depth < i.getMinDepth())
                .filter(i -> !i.isAddedToMap())
                .collect(Collectors.toCollection(ArrayList::new));

        itemsToAddPerLevel = 1 + rng.nextInt(3);

        for (int i = 0; i < itemsToAddPerLevel && !candidateItems.isEmpty(); i++) {
            List<Item> items;
            int chance = 1 + rng.nextInt(20);
            if (chance == 20) {
                items = higherLevelItems;
            } else {
                items = candidateItems;
            }
            Item item = items.remove(rng.nextInt(items.size()));
            item.setAddedToMap(true);
            Position2D itemPos = emptyPositions.remove(rng.nextInt(emptyPositions.size()));
            gameMap.getItems().put(itemPos, item);
        }

        return gameMap;
    }

    private static void generateMapType(GameMap gameMap, MapType mapType) {
        System.out.println("Generating " + mapType + "...");
        int width = gameMap.getWidth();
        int height = gameMap.getHeight();
        ColorSet colorSet = ColorSet.RED;
        gameMap.setMapType(mapType);
        switch (mapType) {
            case DUNGEON: {
                colorSet = ColorSet.BROWN;
                DungeonGenerator generator = new DungeonGenerator(width, height);
                char[][] cells = generator.getCells();

                for (int x = 0; x < generator.getWidth(); x++) {
                    for (int y = 0; y < generator.getHeight(); y++) {
                        gameMap.setCell(y, x, cells[y][x]);
                    }
                }
                gameMap.setRooms(generator.getRooms());
                break;
            }
            case CAVES: {
                colorSet = ColorSet.PURPLE; //Rng.nextValue(ColorSet.PURPLE, ColorSet.GREEN);
                Maze maze = Maze.generateCellularAutomataRoom(width, height);
                for (int i = 0; i < 3; i++) {
                    maze.cellularAutomataIteration();
                    maze.connectDisconnectedComponents();
                }
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        byte b = maze.getCell(x, y);
                        char c;
                        if (b == Maze.WALL) {
                            c = '#';
                        } else if (b == Maze.PATH) {
                            c = '.';
                        } else {
                            c = ' ';
                        }
                        gameMap.setCell(y, x, c);
                    }
                }
                break;
            }
            case MAZE: {
                colorSet = ColorSet.RED;
                if (width %2 == 0) {
                    width -= 1;
                }
                if (height %2 == 0) {
                    height -= 1;
                }
                Maze maze = Maze.generateRandomWalledMaze(width, height);
                maze.setDifficulty(Difficulty.EASY);
                populateMapFromMaze(gameMap, maze);
                break;
            }
            case DOUBLE_MAZE: {
                colorSet = ColorSet.BLUE;
//                if (width %2 == 1) {
//                    width -= 1;
//                }
//                if (height %2 == 1) {
//                    height -= 1;
//                }
                Maze maze = Maze.generateRandomWalledMaze(width / 2 - 1, height / 2 - 1);
                maze.setDifficulty(Difficulty.EASY);
                maze = Maze.createScaledCopy(maze, 2);
                System.out.println("width: " + width);
                System.out.println("height: " + width);
                System.out.println("scaled width: " + maze.getWidth());
                System.out.println("scaled height: " + maze.getHeight());
                populateMapFromMaze(gameMap, maze);
                break;
            }
        }
        gameMap.setColorSet(colorSet);
    }

    private static void populateMapFromMaze(GameMap gameMap, Maze maze) {
        int width = maze.getWidth();
        int height = maze.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                byte b = maze.getCell(x, y);
                char c;
                if (b == Maze.WALL) {
                    c = '#';
                } else if (b == Maze.PATH) {
                    c = '.';
                } else {
                    c = ' ';
                }
                gameMap.setCell(y, x, c);
            }
        }
    }

    public static List<Position2D> getCandidateRoomPositions(GameMap gameMap, int maxPositionsPerRoom) {
        List<Position2D> emptyPositions = new ArrayList<>();
        for (Room room : gameMap.getRooms()) {
            List<Position2D> roomPositions = new ArrayList<>();
            for (int x = room.getOrigin().x(); x < room.getOrigin().x() + room.getWidth(); x++){
                for (int y = room.getOrigin().y(); y < room.getOrigin().y() + room.getHeight(); y++) {
                    char c = gameMap.getCell(y, x);
                    if (c == '.') {
                        roomPositions.add(new Position2D(x, y));
                    }
                }
            }
            int positionsToGet = 1 + rng.nextInt(Math.min(maxPositionsPerRoom - 1, room.getHeight()));
            for (int i = 0; i < positionsToGet; i++) {
                Position2D next = roomPositions.get(rng.nextInt(roomPositions.size()));
                emptyPositions.add(next);
            }
        }
        return emptyPositions;
    }

}
