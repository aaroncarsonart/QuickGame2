package com.aaroncarsonart.quickgame2;

import com.aaroncarsonart.quickgame2.hero.Hero;
import com.aaroncarsonart.quickgame2.hero.HeroCreator;
import com.aaroncarsonart.quickgame2.inventory.Inventory;
import com.aaroncarsonart.quickgame2.map.DungeonGenerator;
import com.aaroncarsonart.quickgame2.menu.BasicVerticalMenuView;
import com.aaroncarsonart.quickgame2.menu.Callback;
import com.aaroncarsonart.quickgame2.menu.CenterMenuView;
import com.aaroncarsonart.quickgame2.menu.ConsoleMenu;
import com.aaroncarsonart.quickgame2.menu.ConsoleMenuView;
import com.aaroncarsonart.quickgame2.menu.InventoryMenuView;
import com.aaroncarsonart.quickgame2.menu.Menu;
import com.aaroncarsonart.quickgame2.menu.MenuItem;
import com.aaroncarsonart.quickgame2.menu.MenuLayout;
import com.aaroncarsonart.quickgame2.menu.MenuView;
import com.aaroncarsonart.quickgame2.menu.StatusMenuView;
import com.aaroncarsonart.quickgame2.menu.VerticalMenuView;
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

        initCharGrid();


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
            renameMenu.setIndex(0);
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
        MenuView mainMenuView = new VerticalMenuView(this, mainMenuOrigin, "Main Menu");
        mainMenu = new Menu(mainMenuView, MenuLayout.VERTICAL, menuCancelCallback);
//        mainMenu.setMaxLength(3);
        mainMenu.addMenuItem(new MenuItem("Rename Hero", renameMenuCallback));
        mainMenu.addMenuItem(new MenuItem("Status", () -> menuList.push(statusMenu)));
        mainMenu.addMenuItem(new MenuItem("Inventory", () -> menuList.push(createInventoryMenu())));
        mainMenu.addMenuItem(new MenuItem("Equipment", () -> {}));
        mainMenu.addMenuItem(new MenuItem("Save", () -> {}));
        mainMenu.addMenuItem(new MenuItem("Exit", () -> System.exit(0)));
    }

    private Menu createInventoryMenu() {
        Callback menuCancelCallback = () -> {
            menuList.pop();
            if (menuList.empty()) {
                gameMode = GameMode.MAP;
            }
        };

        Position2D menuOrigin = new Position2D(1, 1);
        MenuView menuView = new InventoryMenuView(this, menuOrigin, "Inventory", hero.getInventory());
        Menu inventoryMenu = new Menu(menuView, MenuLayout.VERTICAL, menuCancelCallback);
        inventoryMenu.setMaxLength(30);

        Inventory inventory = hero.getInventory();
        Inventory.Slot[] inventorySlots = inventory.getSlots();
        for (Inventory.Slot slot : inventorySlots) {
            String label = slot.getLabel();
            inventoryMenu.addMenuItem(new MenuItem(label, () -> {}));
        }
        return inventoryMenu;
    }

    private void initCharGrid() {
        // ------------------------------------------------
        // init charGrid
        // ------------------------------------------------
        DungeonGenerator generator = new DungeonGenerator(gridWidth, gridHeight - 3);
        char[][] cells = generator.getCells();
        charGrid = new char[gridHeight][gridWidth];
        for (int x = 0; x < generator.getWidth(); x++) {
            for (int y = 0; y < generator.getHeight(); y++) {
                charGrid[y][x] = cells[y][x];
            }
        }

//        Maze maze = Maze.generateRandomWalledMaze(gridWidth, gridHeight);
//        Maze maze = Maze.generateCellularAutomataRoom(gridWidth, gridHeight - 3);
//        for (int i = 0; i < 3; i++) {
//            maze.cellularAutomataIteration();
//            maze.connectDisconnectedComponents();
//        }
//        charGrid = new char[gridHeight][gridWidth];
//        for (int x = 0; x < gridWidth; x++) {
//            for (int y = 0; y < gridHeight; y++) {
//                byte b = maze.getCell(x, y);
//                char c;
//                if (b == Maze.WALL) {
//                    c = '#';
//                } else if (b == Maze.PATH) {
//                    c = '.';
//                } else {
//                    c = ' ';
//                }
//                charGrid[y][x] = c;
//            }
//        }

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
                } else if (c == '+') {
                    bg = Color.BLACK;
                    fg = Color.YELLOW;
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
                    case KeyEvent.VK_I:
                        playerAction = PlayerAction.INVENTORY_MENU;
                        break;
                    case KeyEvent.VK_R:
                        playerAction = PlayerAction.UNKNOWN;
                        initCharGrid();
                        canvas.repaint();
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
                    case INVENTORY_MENU:
                        gameMode = GameMode.MENU;
                        menuList.push(createInventoryMenu());
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
            passable = ".+".indexOf(c) != -1;
        }
        if (passable) {
            player = next;
            hero.setEnergy(hero.getEnergy() - 0.05);
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
