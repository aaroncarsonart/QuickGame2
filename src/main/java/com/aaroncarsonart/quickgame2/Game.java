package com.aaroncarsonart.quickgame2;

import com.aaroncarsonart.quickgame2.hero.Hero;
import com.aaroncarsonart.quickgame2.hero.HeroCreator;
import com.aaroncarsonart.quickgame2.menu.BasicVerticalMenuView;
import com.aaroncarsonart.quickgame2.menu.Callback;
import com.aaroncarsonart.quickgame2.menu.CenterMenuView;
import com.aaroncarsonart.quickgame2.menu.ConsoleMenu;
import com.aaroncarsonart.quickgame2.menu.ConsoleMenuView;
import com.aaroncarsonart.quickgame2.menu.Menu;
import com.aaroncarsonart.quickgame2.menu.MenuItem;
import com.aaroncarsonart.quickgame2.menu.MenuLayout;
import com.aaroncarsonart.quickgame2.menu.MenuView;
import com.aaroncarsonart.quickgame2.menu.StatusMenuView;
import imbroglio.Direction;
import imbroglio.Maze;
import imbroglio.Position2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.function.Consumer;

public class Game {

    public static final Color BROWN = new Color(165, 82, 0);
    public static final Color DARK_BROWN = new Color(50, 15, 0);

    private int fontSize = 16;

    private JFrame frame;
    private JPanel canvas = new Canvas();
    private Font font = new Font("Courier", Font.PLAIN, fontSize);
    private FontMetrics fontMetrics = canvas.getFontMetrics(font);

    private int fontWidth = fontMetrics.stringWidth("@");
    private int fontHeight = fontMetrics.getHeight();
    private int fontAscent = fontMetrics.getAscent();

    private int tileHeight = fontHeight;
    private int tileWidth = fontHeight;
//    private int tileWidth = fontWidth;

    private int gridWidth = 60;
    private int gridHeight = 40;

    private int width = gridWidth * tileWidth;
    private int height = gridHeight * tileHeight;

    private char[][] charGrid;
    private Position2D player;
    private PlayerAction playerAction;
    private GameMode gameMode = GameMode.MAP;

    private List<String> oldMenuList;
    private int menuCursor;

    private Menu moveMenu;
    private Menu mainMenu;
    private Menu statusMenu;
    private ConsoleMenu renameMenu;
    private Stack<Menu> menuList;

    private Hero hero = HeroCreator.createDefaultHero();

    private static final Random RANDOM = Constants.RNG;

    /**
     * A custom Component for drawing the game graphics to the screen.
     */
    public class Canvas extends JPanel {
        @Override
        public void paint(Graphics g) {
            Graphics2D graphics2D = (Graphics2D) g;
            drawCharGrid(graphics2D);
            if (gameMode == GameMode.MENU) {
                //drawMenus(graphics2D);
                Menu menu = menuList.peek();
                menu.getMenuView().render(graphics2D, menu);
            }
        }
    }

    /**
     * Initialize the key game components.
     */
    Game() {
        // init swing
        frame = new JFrame("QuickGame 2");
        canvas.setPreferredSize(new Dimension(width, height));
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.addKeyListener(createKeyListener());

        // init oldMenuList
        oldMenuList = new ArrayList<>();

        // ------------------------------------------------
        // init charGrid
        // ------------------------------------------------
//        Maze maze = Maze.generateRandomWalledMaze(gridWidth, gridHeight);
        Maze maze = Maze.generateCellularAutomataRoom(gridWidth, gridHeight - 3);
        for (int i = 0; i < 3; i++) {
            maze.cellularAutomataIteration();
            maze.connectDisconnectedComponents();
        }
        charGrid = new char[gridHeight][gridWidth];
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                byte b = maze.getCell(x, y);
                char c;
                if (b == Maze.WALL) {
                    c = '#';
                } else if (b == Maze.PATH) {
                    c = '.';
                } else {
                    c = ' ';
                }
                charGrid[y][x] = c;
            }
        }

        // ------------------------------------------------
        // Populate charGrid
        // ------------------------------------------------
        List<Position2D> openPaths = new ArrayList<>();
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                char c = charGrid[y][x];
                if (c == '.') {
                    openPaths.add(new Position2D(x, y));
                }
            }
        }

        player = openPaths.remove(0);



        // ====================================================================
        // SETUP MENUS
        // ====================================================================
        menuList = new Stack<>();
        Callback menuCancelCallback = () -> {
            menuList.pop();
            if (menuList.empty()) {
                gameMode = GameMode.MAP;
            }
        };

        // ------------------------------------------------
        // Setup statusMenu
        // ------------------------------------------------
        statusMenu = new Menu(new StatusMenuView(this), MenuLayout.VERTICAL, menuCancelCallback);

        // ------------------
        // setup console menu
        // ------------------
        MenuView renameMenuView = new ConsoleMenuView(this, "Enter hero name:");
        Callback renameMenuCallback = () -> {
            gameMode = GameMode.MENU;
            menuList.push(renameMenu);
            frame.addKeyListener(renameMenu);
        };
        Callback renameCancelCallback = () -> {
            menuCancelCallback.execute();
            renameMenu.getCharacterBuffer().clear();
            frame.removeKeyListener(renameMenu);
        };
        Consumer<String> renameOkCallback = (str) -> {
            hero.setName(str);
            renameCancelCallback.execute();
        };
        renameMenu = new ConsoleMenu(
                renameMenuView,
                MenuLayout.HORIZONTAL,
                renameCancelCallback,
                renameOkCallback,
                20);

        // ------------------------------------------------
        // Setup mainMenu
        // ------------------------------------------------
        Position2D mainMenuOrigin = new Position2D(1, 1);
        MenuView mainMenuView = new BasicVerticalMenuView(this, mainMenuOrigin, true);
        mainMenu = new Menu(mainMenuView, MenuLayout.VERTICAL, menuCancelCallback);
        mainMenu.addMenuItem(new MenuItem("Rename Hero", renameMenuCallback));
        mainMenu.addMenuItem(new MenuItem("Status", () -> menuList.push(statusMenu)));
        mainMenu.addMenuItem(new MenuItem("Inventory", () -> {}));
        mainMenu.addMenuItem(new MenuItem("Equipment", () -> {}));
        mainMenu.addMenuItem(new MenuItem("Save", () -> {}));
        mainMenu.addMenuItem(new MenuItem("Exit", () -> System.exit(0)));


        // ------------------------------------------------
        // setup moveMenu test
        // ------------------------------------------------

        Position2D moveMenuOrigin = new Position2D(2,2);
        MenuView moveMenuView = new BasicVerticalMenuView(this, moveMenuOrigin, true);
        moveMenu = new Menu(moveMenuView, MenuLayout.VERTICAL, menuCancelCallback);
        moveMenu.addMenuItem(new MenuItem("Move UP", () -> moveMap(PlayerAction.UP)));
        moveMenu.addMenuItem(new MenuItem("Move DOWN", () -> moveMap(PlayerAction.DOWN)));
        moveMenu.addMenuItem(new MenuItem("Move RIGHT", () -> moveMap(PlayerAction.RIGHT)));
        moveMenu.addMenuItem(new MenuItem("Move LEFT", () -> moveMap(PlayerAction.LEFT)));
    }

    public void start() {
        System.out.println("Hello, world!");
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void drawChar(Graphics2D graphics2D, char c, int gx, int gy, Color bg, Color fg) {
        int x = gx * tileWidth;
        int y = gy * tileHeight;

        graphics2D.setColor(bg);
        graphics2D.fillRect(x, y, tileWidth, tileHeight);

        int ty = y + fontAscent;
        int tx = x + (tileWidth - fontWidth) / 2;

        graphics2D.setColor(fg);
        graphics2D.drawString(c + "", tx , ty);
    }

    public void drawString(Graphics2D g, String str, int gx, int gy, Color bg, Color fg) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            drawChar(g, c, gx + i, gy, bg, fg);
        }
    }

    public void drawCharGrid(Graphics2D graphics2D) {
        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(0, 0, width, height);

        graphics2D.setFont(font);
        graphics2D.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON));

        // draw background
        for (int gx = 0; gx < gridWidth; gx++) {
            for (int gy = 0; gy < gridHeight; gy++) {

                char c = charGrid[gy][gx];
                Color bg, fg;
                if (c == '#') {
                    bg = DARK_BROWN;
                    fg = BROWN;
                } else if (c == '.') {
                    bg = Color.BLACK;
                    fg = Color.DARK_GRAY;
                } else {
                    bg = Color.BLACK;
                    fg = Color.WHITE;
                }
                drawChar(graphics2D, c, gx, gy, bg, fg);
            }
        }

        // draw sprites
        int px = player.x();
        int py = player.y();
        Color pbg = Color.BLACK;
        Color pfg = Color.WHITE;

        drawChar(graphics2D, '@', px, py, pbg, pfg);
    }

    private void drawMenus(Graphics2D graphics2D) {
        int menuWidth = 0;
        for (String menuItem : oldMenuList) {
            menuWidth = Math.max(menuWidth, menuItem.length());
        }
        int menuHeight = oldMenuList.size();

        int mx = (gridWidth - menuWidth) / 2;
        int my = (gridHeight - menuHeight) / 2;

        // ------------------------------------------------
        // draw oldMenuList contents
        // ------------------------------------------------
        for (int y = 0; y < menuHeight; y++) {
            for (int x = 0; x < menuWidth; x++) {
                Color bg = Color.BLACK;
                Color fg = Color.WHITE;
                if (y == menuCursor) {
                    bg = Color.WHITE;
                    fg = Color.BLACK;
                }

                String menuItem = oldMenuList.get(y);
                char c;
                if (x < menuItem.length()) {
                    c = menuItem.charAt(x);
                } else {
                    c = ' ';
                }
                drawChar(graphics2D, c, mx + x, my + y, bg, fg);
            }
        }
        // ------------------------------------------------
        // draw oldMenuList borders
        // ------------------------------------------------
        Color bg = Color.BLACK;
        Color fg = Color.DARK_GRAY;
        // ─│┌┐└┘

        for (int y = 0; y < menuHeight; y++) {
            int x = mx - 1;
            drawChar(graphics2D, '│', x, my + y, bg, fg);
            x = mx + menuWidth;
            drawChar(graphics2D, '│', x, my + y, bg, fg);
        }

        for (int x = 0; x < menuWidth; x++) {
            int y = my - 1;
            drawChar(graphics2D, '─', mx + x, y, bg, fg);
            y = my + menuHeight;
            drawChar(graphics2D, '─', mx + x, y, bg, fg);
        }
        drawChar(graphics2D, '┌', -1 + mx, -1 + my, bg, fg);
        drawChar(graphics2D, '┐', mx + menuWidth, -1 + my, bg, fg);
        drawChar(graphics2D, '└', -1 + mx, my + menuHeight, bg, fg);
        drawChar(graphics2D, '┘', mx + menuWidth, my + menuHeight, bg, fg);


    }

    public KeyListener createKeyListener() {
        KeyListener keyListener = new KeyListener() {
            public void keyTyped(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_K:
                        playerAction = PlayerAction.UP;
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_J:
                        playerAction = PlayerAction.DOWN;
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_H:
                        playerAction = PlayerAction.LEFT;
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_L:
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
                    case KeyEvent.VK_Q:
                        playerAction = PlayerAction.QUIT;
                        break;
                    case KeyEvent.VK_M:
                        playerAction = PlayerAction.MOVE_MENU;
                        break;
                    case KeyEvent.VK_S:
                        playerAction = PlayerAction.STATUS_MENU;
                        break;
                    default:
                        playerAction = PlayerAction.UNKNOWN;
                        break;
                }
                gameLoop();
            }
        };
        return keyListener;
    }

    /**
     * Execute one iteration of the game loop.  To be called
     * immediately after processing user input.
     */
    private void gameLoop() {
        boolean updated = false;
        switch (gameMode) {
            // --------------------------------------------
            case MAP: {
            // --------------------------------------------
                switch(playerAction) {
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        updated = moveMap(playerAction);
                        break;
                    case CANCEL:
                        gameMode = GameMode.MENU;
                        menuList.push(mainMenu);
                        updated = true;
                        break;
                    case STATUS_MENU:
                        gameMode = GameMode.MENU;
                        menuList.push(statusMenu);
                        updated = true;
                        break;
                    case MOVE_MENU:
                        gameMode = GameMode.MENU;
                        menuList.push(moveMenu);
                        updated = true;
                        break;
                    case QUIT:
                        System.exit(0);
                }
            }
            break;
            // --------------------------------------------
            case MENU: {
            // --------------------------------------------
                menuList.peek().updateMenu(playerAction);
                updated = true;
//                switch(playerAction) {
//                    case UP:
//                    case DOWN:
//                    case LEFT:
//                    case RIGHT:
//                        updated = moveMenu(playerAction);
//                        break;
//                    case CANCEL:
//                        gameMode = GameMode.MAP;
//                        updated = true;
//                        break;
//                    case QUIT:
//                        System.exit(0);
//                }
            }
            break;
        }
        if (updated) {
            canvas.repaint();
        }
    }

    private boolean moveMap(PlayerAction playerAction) {
        boolean updated = false;
        Position2D next = player.moveTowards(getDirection(playerAction));
        boolean passable = false;
        if (withinBounds(next.y(), next.x())) {
            char c = charGrid[next.y()][next.x()];
            passable = c == '.';
        }
        if (passable) {
            player = next;
            updated = true;
        }
        System.out.println("PlayerPos: " + player);
        return updated;
    }

    private boolean withinBounds(int y, int x) {
        return 0 <= x && x < gridWidth && 0 <= y && y < gridHeight;
    }

    private Direction getDirection(PlayerAction action) {
        switch (action) {
            case UP: return Direction.UP;
            case DOWN: return Direction.DOWN;
            case LEFT: return Direction.LEFT;
            case RIGHT: return Direction.RIGHT;
            default: return Direction.NONE;
        }
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public Hero getHero() {
        return hero;
    }

}
