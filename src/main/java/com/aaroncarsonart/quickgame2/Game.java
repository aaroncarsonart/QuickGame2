package com.aaroncarsonart.quickgame2;

import imbroglio.Maze;
import imbroglio.Position2D;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

    public static final int WINDOW_WIDTH = 60;
    public static final int WINDOW_HEIGHT = 30;

    public static final Random RNG = Constants.RNG;

    public static final Color BROWN = new Color(165, 82, 0);
    public static final Color DARK_BROWN = new Color(50, 15, 0);
    public static final Color DARK_CYAN = new Color(0, 30, 30);

    CharGrid charGrid;
    KeyListener keyListener;
    PlayerAction playerAction;


    public Game() {
        charGrid = new CharGrid(WINDOW_HEIGHT, WINDOW_WIDTH);

        keyListener = createKeyListener();
        charGrid.getJFrame().addKeyListener(keyListener);

        setupGameData();
        drawMap();
    }

    public void start() {
        charGrid.show();
    }

    /**
     * @return The KeyListener that handles raw KeyEvents.
     */
    public KeyListener createKeyListener() {
        return new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        playerAction = PlayerAction.UP;
                        break;
                    case KeyEvent.VK_DOWN:
                        playerAction = PlayerAction.DOWN;
                        break;
                    case KeyEvent.VK_LEFT:
                        playerAction = PlayerAction.LEFT;
                        break;
                    case KeyEvent.VK_RIGHT:
                        playerAction = PlayerAction.RIGHT;
                        break;
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_Z:
                        playerAction = PlayerAction.OK;
                        break;
                    case KeyEvent.VK_ESCAPE:
                    case KeyEvent.VK_X:
                        playerAction = PlayerAction.CANCEL;
                        break;
                    case KeyEvent.VK_TAB:
                    case KeyEvent.VK_C:
                        playerAction = PlayerAction.TAB;
                        break;
                    default:
                        playerAction = PlayerAction.UNKNOWN;
                        break;
                }
                gameLoop();
            }
        };
    }

    public void gameLoop() {
        respondToUserInput();
        drawMap();
        charGrid.getJFrame().repaint();
    }

    public void respondToUserInput() {
        if (playerAction == PlayerAction.TAB) {
            selectedMember ++;
            if (selectedMember >= squadMembers.size()) {
                selectedMember = 0;
            }
        } else {
            movePlayer();
        }

    }

    public void movePlayer() {
        SquadMember activeMember = squadMembers.get(selectedMember);
        Position2D nextPos;
        Position2D currentPos = activeMember.position;

        switch (playerAction) {
            case UP:
                nextPos = currentPos.above();
                break;
            case DOWN:
                nextPos = currentPos.below();
                break;
            case LEFT:
                nextPos = currentPos.left();
                break;
            case RIGHT:
                nextPos = currentPos.right();
                break;
            default:
                nextPos = currentPos;
                break;
        }
        if (withinBounds(nextPos, height, width) && !occupied(nextPos)) {
            entityMap[currentPos.y()][currentPos.x()] = '\u0000';
            entityMap[nextPos.y()][nextPos.x()] = activeMember.numeral;
            activeMember.position = nextPos;

        }
    }

    private int width;
    private int height;
    private char[][] gameMap;
    private List<SquadMember> squadMembers;
    private int selectedMember;

    private char[][] entityMap;

    public void setupGameData() {
        width = RNG.nextInt(40) + 40;
        height = RNG.nextInt(20 + 20);
        gameMap = new char[height][width];
        entityMap = new char[height][width];

        List<Position2D> openPaths = new ArrayList<>();

        Maze maze = Maze.generateCellularAutomataRoom(width, height);
        for (int i = 0; i < 3; i++) {
            maze.cellularAutomataIteration();
            maze.connectDisconnectedComponents();
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                byte cell = maze.getCell(x, y);
                char sprite = cell == Maze.WALL ? '#' : '.';
                gameMap[y][x] = sprite;
//                gameMap[y][x] = '.';
                if (!occupied(y, x)) {
                    openPaths.add(new Position2D(x, y));
                }
            }
        }


        List<Position2D> bottomPaths = new ArrayList<>();
        for (int y = height - 1; y >= height * 3 / 4; y--) {
            for (int x = 0; x < width / 4; x++) {
                if (!occupied(y, x)) {
                    bottomPaths.add(new Position2D(x, y));
                }
            }
        }
        int squadCount = 4;
        squadMembers = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            Position2D openPath = bottomPaths.remove(Constants.RNG.nextInt(bottomPaths.size()));
            SquadMember squadMember = new SquadMember();
            squadMember.numeral = Character.forDigit(i, 10);
            squadMember.position = openPath;
            squadMembers.add(squadMember);
            entityMap[openPath.y()][openPath.x()] = squadMember.numeral;
        }
    }

    public boolean occupied(int y, int x) {
        return occupied(new Position2D(x, y), "#");
    }


    public boolean occupied(Position2D p) {
        return occupied(p, "#");
    }

    public boolean occupied(Position2D p, String chars) {
        char c = gameMap[p.y()][p.x()];
        char entity = entityMap[p.y()][p.x()];
        return chars.contains(String.valueOf(c)) || entity != '\u0000';
    }

    public boolean occupied(Position2D p, char sprite) {
        return gameMap[p.y()][p.x()] == sprite || entityMap[p.y()][p.x()] != '\u0000';
    }


    public boolean withinBounds(Position2D p, int height, int width) {
        return withinBounds(p.y(), p.x(), height, width);
    }

    public boolean withinBounds(int y, int x, int height, int width) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public void drawMap() {

        // ------------------------------------------------------------------
        // Clear ScreenBuffer
        // ------------------------------------------------------------------

        for (int y = 0; y < WINDOW_HEIGHT; y++) {
            for (int x = 0; x < WINDOW_WIDTH; x++) {
//                charGrid.setChar(' ', y, x);
//                charGrid.setColors(Color.BLACK, Color.WHITE, y, x);
                charGrid.setChar('#', y, x);
                charGrid.setColors(DARK_BROWN, BROWN, y, x);
            }
        }

        // ------------------------------------------------------------------
        // Draw Map Data
        // ------------------------------------------------------------------

        SquadMember selected = squadMembers.get(selectedMember);
        Position2D camera = selected.position;

        Position2D center = new Position2D(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int tx = x + center.x() - camera.x();
                int ty = y + center.y() - camera.y();
                if (withinBounds(ty, tx, WINDOW_HEIGHT, WINDOW_WIDTH)) {
                    char c = gameMap[y][x];
                    charGrid.setChar(c, ty, tx);
                    if (c == '.') {
                        charGrid.setColors(Color.BLACK, Color.DARK_GRAY, ty, tx);
                    } else if (c == '#') {
                        charGrid.setColors(DARK_BROWN, BROWN, ty, tx);
                    } else if (c == '"') {
                        charGrid.setColors(Color.BLACK, Color.BLACK, ty, tx);
                    } else if (c == '$') {
                        charGrid.setColors(Color.BLACK, Color.YELLOW, ty, tx);
                    } else if (c == 'M') {
                        charGrid.setColors(Color.BLACK, Color.RED, ty, tx);
                    } else if (c == '~') {
                        charGrid.setColors(DARK_CYAN, Color.CYAN, ty, tx);
                    } else if (c == '!') {
                        charGrid.setColors(Color.BLACK, Color.BLACK, ty, tx);
                    } else {
                        charGrid.setColors(Color.BLACK, Color.WHITE, ty, tx);
                    }
                }
            }
        }

        // TODO add draw inverse = true property
        // add targeting with A and mouseover .... maybe

        // ------------------------------------------------------------------
        // Draw SquadMembers
        // ------------------------------------------------------------------

        for (SquadMember squadMember : squadMembers) {
            int tx = squadMember.position.x() + center.x() - camera.x();
            int ty = squadMember.position.y() + center.y() - camera.y();
            if (withinBounds(ty, tx, WINDOW_HEIGHT, WINDOW_WIDTH)) {
                char c = squadMember.numeral;
                charGrid.setChar(c, ty, tx);
                if (Character.getNumericValue(squadMember.numeral) -1 == selectedMember) {
                    charGrid.setColors(Color.WHITE, Color.BLACK, ty, tx);
                } else {
                    charGrid.setColors(Color.BLACK, Color.WHITE, ty, tx);
                }
            }
        }


        // ------------------------------------------------------------------
        // Draw HUD displays
        // ------------------------------------------------------------------

        charGrid.drawBoxBorders(0, 0, WINDOW_HEIGHT - 1, WINDOW_WIDTH - 1);
        charGrid.drawBoxCorners(0, 0, WINDOW_HEIGHT - 1, WINDOW_WIDTH - 1);


    }
}
