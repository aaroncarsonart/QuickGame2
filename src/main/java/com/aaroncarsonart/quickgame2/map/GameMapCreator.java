package com.aaroncarsonart.quickgame2.map;

import com.aaroncarsonart.quickgame2.Constants;
import com.aaroncarsonart.quickgame2.util.Rng;
import imbroglio.Difficulty;
import imbroglio.Maze;
import imbroglio.Position2D;

import java.util.List;
import java.util.Random;

public class GameMapCreator {

    private static Random rng = Constants.RNG;
    private static int nextType = 0;
    private static int nextColor = 2;

    public static GameMap createGameMap(int width, int height) {
        GameMap gameMap = new GameMap(width, height);

        MapType[] mapTypes = MapType.values();
        // int nextType = rng.nextInt(mapTypes.length);
        MapType mapType = mapTypes[nextType];
        generateMapType(gameMap, mapType);

//        ColorSet[] colorSets = ColorSet.values();
////        nextColor = rng.nextInt(colorSets.length);
//        ColorSet colorSet = colorSets[nextColor];
//        gameMap.setColorSet(colorSet);



        return gameMap;
    }

    public static GameMap createGameMap(int width, int height, int depth) {
        GameMap gameMap = new GameMap(width, height);
        gameMap.setDepth(depth);

        // determine map type and color
        if (0 <= depth && depth < 3) {
            generateMapType(gameMap, MapType.DUNGEON);
            gameMap.setColorSet(ColorSet.BROWN);
        } else if (3 <= depth && depth < 6) {
            generateMapType(gameMap, MapType.CAVES);
            gameMap.setColorSet(ColorSet.PURPLE);
        } else if (6 <= depth && depth < 9) {
            generateMapType(gameMap, MapType.DOUBLE_MAZE);
            gameMap.setColorSet(ColorSet.BLUE);
        } else if (depth == 9) {
            generateMapType(gameMap, MapType.MAZE);
            gameMap.setColorSet(ColorSet.RED);
        } else if (10 <= depth && depth < 13) {
            generateMapType(gameMap, MapType.DUNGEON);
            gameMap.setColorSet(ColorSet.YELLOW);
        } else if (13 <= depth && depth < 16) {
            generateMapType(gameMap, MapType.CAVES);
            gameMap.setColorSet(ColorSet.GREEN);
        } else if (16 <= depth && depth < 19) {
            generateMapType(gameMap, MapType.DOUBLE_MAZE);
            gameMap.setColorSet(ColorSet.BROWN);
        } else if (depth == 19) {
            generateMapType(gameMap, MapType.MAZE);
            gameMap.setColorSet(ColorSet.RED);
        }

        // populate stairs, items, monsters
        List<Position2D> emptyPositions = gameMap.getEmptyPositions();
        if (depth != 0) {
            Position2D upPos = emptyPositions.remove(rng.nextInt(emptyPositions.size()));
            Stairs upstairs = new Stairs(upPos, Constants.UPSTAIRS);
            gameMap.setUpstairs(upstairs);
            gameMap.setCell(upPos, upstairs.getSprite());
        }
        if (depth != 19) {
            Position2D downPos = emptyPositions.remove(rng.nextInt(emptyPositions.size()));
            Stairs downstairs = new Stairs(downPos, Constants.DOWNSTAIRS);
            gameMap.setDownstairs(downstairs);
            gameMap.setCell(downPos, downstairs.getSprite());
        }

        return gameMap;
    }

    private static void generateMapType(GameMap gameMap, MapType mapType) {
        System.out.println("Generating " + mapType + "...");
        int width = gameMap.getWidth();
        int height = gameMap.getHeight();
        ColorSet colorSet = ColorSet.RED;
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

}
