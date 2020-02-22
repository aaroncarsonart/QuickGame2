package com.aaroncarsonart.quickgame2.map;

import com.aaroncarsonart.quickgame2.Constants;
import imbroglio.Difficulty;
import imbroglio.Maze;

import java.util.Random;

public class GameMapCreator {

    private static Random rng = Constants.RNG;
    private static int nextType = 0;
    private static int nextColor = 0;

    public static GameMap getGameMap(int width, int height) {
        GameMap gameMap = new GameMap(width, height);

        MapType[] mapTypes = MapType.values();
        // int nextType = rng.nextInt(mapTypes.length);
        MapType mapType = mapTypes[nextType];
        generateMapType(gameMap, mapType);

        ColorSet[] colorSets = ColorSet.values();
//        nextColor = rng.nextInt(colorSets.length);
        ColorSet colorSet = colorSets[nextColor];
        gameMap.setColorSet(colorSet);



        return gameMap;
    }

    private static void generateMapType(GameMap gameMap, MapType mapType) {
        System.out.println("Generating " + mapType + "...");
        int width = gameMap.getWidth();
        int height = gameMap.getHeight();
        switch (mapType) {
            case DUNGEON: {
                DungeonGenerator generator = new DungeonGenerator(width, height);
                char[][] cells = generator.getCells();

                for (int x = 0; x < generator.getWidth(); x++) {
                    for (int y = 0; y < generator.getHeight(); y++) {
                        gameMap.setCell(y, x, cells[y][x]);
                    }
                }
                break;
            }
            case CAVES: {
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

}
