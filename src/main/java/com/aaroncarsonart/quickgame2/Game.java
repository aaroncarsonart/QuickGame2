package com.aaroncarsonart.quickgame2;

import imbroglio.Maze;
import imbroglio.Position2D;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Stack;

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

    boolean[][] highlight = new boolean[WINDOW_HEIGHT][WINDOW_WIDTH];
    boolean ignoreInvertOnMouseExit = false;

    int treasure = 0;

    public Game() {
        charGrid = new CharGrid(WINDOW_HEIGHT, WINDOW_WIDTH);
        highlight = new boolean[WINDOW_HEIGHT][WINDOW_WIDTH];

        keyListener = createKeyListener();
        charGrid.getJFrame().addKeyListener(keyListener);

        for (int y = 0; y < WINDOW_HEIGHT; ++y) {
            for (int x = 0; x < WINDOW_WIDTH; ++x) {
                createMouseListener(charGrid, y, x);
            }
        }

        setupGameData();
        redrawScreen();
    }

    public MouseListener createMouseListener(CharGrid charGrid, int y, int x) {
        CharPanel panel = charGrid.getTilePanel(y, x);
        MouseListener listener = new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Position2D camera = squadMembers.get(selectedMember).position;
                Position2D center = new Position2D(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);

                for (SquadMember member : squadMembers) {
                    int tx = member.position.x() + center.x() - camera.x();
                    int ty = member.position.y() + center.y() - camera.y();

//                    System.out.println("Member " + member.numeral + " at " + new Position2D(tx, ty));

                    if (x == tx && y == ty) {
                        selectedMember = Character.getNumericValue(member.numeral) - 1;
                        ignoreInvertOnMouseExit = true;
                        redrawScreen();
                    }
                }
            }
            public void mousePressed(MouseEvent e) {
            }
            public void mouseReleased(MouseEvent e) {
            }
            public void mouseEntered(MouseEvent e) {
                highlight[y][x] = true;
                redrawScreen();

//                Color bg = panel.getBgColor();
//                Color fg = panel.getFgColor();
//                panel.setForeground(bg);
//                panel.setBackground(fg);
                System.out.println("Entered " + new Position2D(x, y));
            }
            public void mouseExited(MouseEvent e) {
                highlight[y][x] = false;
                redrawScreen();

//                Color bg = panel.getBgColor();
//                 Color fg = panel.getFgColor();
//                 panel.setForeground(bg);
//                 panel.setBackground(fg);
            }
        };
        panel.getJPanel().addMouseListener(listener);
        return listener;
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
        redrawScreen();
    }

    public void redrawScreen() {
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

            // check for treasure
            if (gameMap[nextPos.y()][nextPos.x()] == '$') {
                gameMap[nextPos.y()][nextPos.x()] = '.';
                treasure += 1;
            }
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

        // -------------------------------------------------------------
        // Create terrain
        // -------------------------------------------------------------

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

//        // create water
//        maze = Maze.generateCellularAutomataRoom(width, height);
//        for (int i = 0; i < 3; i++) {
//            maze.cellularAutomataIteration();
//            maze.connectDisconnectedComponents();
//        }
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                byte cell = maze.getCell(x, y);
//                char sprite = cell == Maze.WALL ? '~' : '.';
//
//                if (gameMap[y][x] == '.' && sprite == '~') {
//                    gameMap[y][x] = '~';
//                }
//            }
//        }

        // ------------------------------------------------------
        // create grass
        // ------------------------------------------------------
        int foodCount = RNG.nextInt(20) + 20;
        for (int i = 0; i < foodCount; i++) {
            if (openPaths.isEmpty()) {
                break;
            }
            Position2D foodPos = openPaths.remove(RNG.nextInt(openPaths.size()));
            gameMap[foodPos.y()][foodPos.x()] = '"';
        }

        char tile = '"';
        int count = 5;
        int maxNeighbors = Math.max(0, 20) + RNG.nextInt(Math.min(1, 20));
        if (maxNeighbors > 0) {
            addBlobsOfTiles(tile, openPaths, count, maxNeighbors);
        }

        // ------------------------------------------------------
        // create water
        // ------------------------------------------------------
        tile = '~';
        count = 5;
        maxNeighbors = Math.max(1, 10) + RNG.nextInt(Math.max(1, 10));
        if (maxNeighbors > 0) {
            addBlobsOfTiles(tile, openPaths, count, maxNeighbors);
        }
        tile = '~';
        count = Math.max( 1, 10);
        maxNeighbors = 3 + RNG.nextInt(3);
        addBlobsOfTiles(tile, openPaths, count, maxNeighbors);

//        maze = Maze.generateCellularAutomataRoom(width, height);
//        for (int i = 0; i < 3; i++) {
//            maze.cellularAutomataIteration();
//            maze.connectDisconnectedComponents();
//        }
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                byte cell = maze.getCell(x, y);
//                char sprite = cell == Maze.WALL ? '#' : '.';
//
//                if (gameMap[y][x] == '.' && sprite == '.') {
//                    gameMap[y][x] = '"';
//                }
//            }
//        }

        // ------------------------------------------------------
        // add treasure
        // ------------------------------------------------------
        treasures = new ArrayList<>();
        int treasureCount = RNG.nextInt(5) + 5;
        for (int i = 0; i < treasureCount; i++) {
            if (openPaths.isEmpty()) {
                break;
            }
            Position2D treasurePos = openPaths.remove(RNG.nextInt(openPaths.size()));
            gameMap[treasurePos.y()][treasurePos.x()] = '$';
            treasures.add(treasurePos);
        }

        tile = '$';
        count = 5;
        maxNeighbors = 5 + RNG.nextInt(5);
        boolean useTreasure = true;
        addBlobsOfTiles(tile, openPaths, count, maxNeighbors, useTreasure);


        // -------------------------------------------------------------
        // Create SquadMembers
        // -------------------------------------------------------------

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
        for (int i = 1; i <= squadCount; i++) {
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
                    Color bg, fg;
                    if (c == '.') {
                        bg = Color.BLACK;
                        fg = Color.DARK_GRAY;
                    } else if (c == '#') {
                        bg = DARK_BROWN;
                        fg = BROWN;
                    } else if (c == '"') {
                        bg = Color.BLACK;
                        fg = Color.GREEN;
                    } else if (c == '$') {
                        bg = Color.BLACK;
                        fg = Color.YELLOW;
                    } else if (c == 'M') {
                        bg = Color.BLACK;
                        fg = Color.RED;
                    } else if (c == '~') {
                        bg = DARK_CYAN;
                        fg = Color.CYAN;
                    } else if (c == '!') {
                        bg = Color.BLACK;
                        fg = Color.WHITE;
                    } else {
                        bg = Color.BLACK;
                        fg = Color.WHITE;
                    }
                    if (highlight[ty][tx]) {
                        charGrid.setColors(fg, bg, ty, tx);
                    } else {
                        charGrid.setColors(bg, fg, ty, tx);
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
//            System.out.println("Member " + squadMember.numeral + " at " + new Position2D(tx, ty));
            if (withinBounds(ty, tx, WINDOW_HEIGHT, WINDOW_WIDTH)) {
                char c = squadMember.numeral;
                charGrid.setChar(c, ty, tx);
                if (Character.getNumericValue(squadMember.numeral) -1 == selectedMember || highlight[ty][tx]) {
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

        int wWidth = 15;
        int wHeight = 5;
        int wx = WINDOW_WIDTH - wWidth;
        int wy = WINDOW_HEIGHT - wHeight;
        charGrid.fillRect(' ', Color.BLACK, Color.WHITE, wy, wx, wHeight, wWidth);
        charGrid.drawBoxBorders(wy, wx, wHeight - 1, wWidth - 1);
        charGrid.drawBoxCorners(wy, wx, wHeight - 1, wWidth - 1);

        int cursor = 0;
        int tx = wx + 1;
        int ty = wy + 1;
        String text = "Treasure: ";
        charGrid.drawText(text, Color.WHITE, ty, tx + cursor);
        cursor += text.length();
        text = String.valueOf(treasure);
        charGrid.drawText(text, Color.YELLOW, ty, tx + cursor);

    }



    List<Position2D> treasures = new ArrayList<>();

    public void addBlobsOfTiles(char tile, List<Position2D> openPaths, int count, int maxNeighbors) {
        addBlobsOfTiles(tile, openPaths, count, maxNeighbors, false);
    }

    public void addBlobsOfTiles(char tile, List<Position2D> openPaths, int count, int maxNeighbors, boolean treasures) {
        for (int i = 0; i < count; i++) {
            if (openPaths.isEmpty()) {
                break;
            }
            Position2D pos = openPaths.remove(RNG.nextInt(openPaths.size()));

            Stack<Position2D> positions = new Stack<>();
            positions.add(pos);

            for (int j = 0; j < maxNeighbors; j++) {
                Position2D next = positions.peek();
                List<Position2D> neighbors = next.getNeighbors();
                for (int k = 0; k < neighbors.size(); k++) {
                    Position2D neighbor = neighbors.get(k);
                    if (positions.contains(neighbor) || !withinBounds(neighbor, height, width)) {
                        neighbors.remove(neighbor);
                    }
                }
                if (neighbors.isEmpty()) {
                    continue;
                }
                Position2D nextNeighbor = neighbors.get(RNG.nextInt(neighbors.size()));
                positions.add(nextNeighbor);
            }

            for (Position2D position : positions) {
                if (withinBounds(position, height, width)) {
                    openPaths.remove(position);
                    gameMap[position.y()][position.x()] = tile;
                    if (treasures) {
                        this.treasures.add(position);
                    }
                }
            }
        }
    }

}
