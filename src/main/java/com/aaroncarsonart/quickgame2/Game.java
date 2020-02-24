package com.aaroncarsonart.quickgame2;

import com.aaroncarsonart.quickgame2.hero.Hero;
import com.aaroncarsonart.quickgame2.hero.HeroCreator;
import com.aaroncarsonart.quickgame2.inventory.Inventory;
import com.aaroncarsonart.quickgame2.map.GameMap;
import com.aaroncarsonart.quickgame2.map.GameMapCreator;
import com.aaroncarsonart.quickgame2.menu.Callback;
import com.aaroncarsonart.quickgame2.menu.ConsoleMenu;
import com.aaroncarsonart.quickgame2.menu.ConsoleMenuView;
import com.aaroncarsonart.quickgame2.menu.InventoryMenuView;
import com.aaroncarsonart.quickgame2.menu.Menu;
import com.aaroncarsonart.quickgame2.menu.MenuItem;
import com.aaroncarsonart.quickgame2.menu.MenuLayout;
import com.aaroncarsonart.quickgame2.menu.MenuView;
import com.aaroncarsonart.quickgame2.menu.StatusMenuView;
import com.aaroncarsonart.quickgame2.menu.VerticalMenuView;
import com.aaroncarsonart.quickgame2.monster.Monster;
import com.aaroncarsonart.quickgame2.status.ColoredString;
import com.aaroncarsonart.quickgame2.status.LogMessage;
import com.aaroncarsonart.quickgame2.util.Bresenham;
import imbroglio.Direction;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Game {

    public static final Color BROWN = new Color(165, 82, 0);
    public static final Color DARK_BROWN = new Color(50, 15, 0);
    public static final Color DARKER_GRAY = new Color(15, 15, 15);
    boolean drawAllSprites = false;
    boolean drawMonsterPaths = false;

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

    private int mapWidth = gridWidth;
    private int mapHeight = gridHeight - 3;

    private int width = gridWidth * tileWidth;
    private int height = gridHeight * tileHeight;

    private char[][] charGrid;
    private Position2D heroPos;
    private PlayerAction playerAction;
    private GameMode gameMode = GameMode.MAP;

    private List<String> oldMenuList;
    private int menuCursor;

    private Menu moveMenu;
    private Menu mainMenu;
    private Menu statusMenu;
    private ConsoleMenu renameMenu;
    private Stack<Menu> menuList;

    private int messageLogMaxSize = 100;
    private List<LogMessage> messageLog = new ArrayList<>();

    private boolean shiftDown;
    private boolean ctrlDown;
    private boolean metaDown;

    private Hero hero = HeroCreator.createDefaultHero();
    private GameMap gameMap;
    private Random rng = Constants.RNG;
    private int turns;

    private char[][] visible;

    private static final int FIELD_OF_VIEW_RANGE = 10;
    private static final Random RANDOM = Constants.RNG;

    /**
     * A custom Component for drawing the game graphics to the screen.
     */
    public class Canvas extends JPanel {
        @Override
        public void paint(Graphics g) {
            Graphics2D graphics2D = (Graphics2D) g;
            graphics2D.setFont(font);
            graphics2D.setRenderingHints(new RenderingHints(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
            drawCharGrid(graphics2D);
            drawHud(graphics2D);
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
        initMenus();
    }

    private void initMenus() {
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

    private void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
        charGrid = gameMap.getCells();
        visible = gameMap.getVisible();
    }

    private void initCharGrid() {
        setGameMap(GameMapCreator.createGameMap(mapWidth, mapHeight, 1));

        // ------------------------------------------------
        // Populate charGrid
        // ------------------------------------------------
        List<Position2D> openPaths = new ArrayList<>();
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                char c = charGrid[y][x];
                if (c == '.') {
                    openPaths.add(new Position2D(x, y));
                }
            }
        }

        for (Monster monster : gameMap.getMonsterMap().values()) {
            openPaths.remove(monster.getPos());
        }

        heroPos = openPaths.remove(0);
        fov(heroPos, FIELD_OF_VIEW_RANGE);
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

    public void drawCharGrid(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        // draw background
        for (int gx = 0; gx < mapWidth; gx++) {
            for (int gy = 0; gy < mapHeight; gy++) {

                char c = charGrid[gy][gx];
                Color bg, fg;
                if (c == '#') {
                    bg = gameMap.getColorSet().bg;
                    fg = gameMap.getColorSet().fg;
                } else if (c == '=') {
                    bg = DARKER_GRAY;
                    fg = Color.DARK_GRAY;
                } else if (c == '.') {
                    bg = Color.BLACK;
                    fg = Color.LIGHT_GRAY;
                } else if (c == '+' || c == '>' || c == '<') {
                    bg = Color.BLACK;
                    fg = Color.YELLOW;
                } else {
                    bg = Color.BLACK;
                    fg = Color.WHITE;
                }

                // FOV colors
                if (visible[gy][gx] == Constants.UNKNOWN && !drawAllSprites) {
                    bg = Color.BLACK;
                    fg = Color.BLACK;
                } else if (visible[gy][gx] == Constants.KNOWN) {
                    if (c == '#') {
                        bg = DARKER_GRAY;
                        fg = Color.GRAY;
                    } else if (c == '.') {
                        bg = Color.BLACK;
                        fg = Color.DARK_GRAY;
                    } else {
                        bg = Color.BLACK;
                        fg = Color.GRAY;
                    }
                }

                drawChar(g, c, gx, gy, bg, fg);

            }
        }

        // draw sprites
//        for (int x = 0; x < mapWidth; x++) {
//            for (int y = 0; y < mapHeight; y++) {
        for (Monster monster : gameMap.getMonsterMap().values()) {
            Position2D pos = monster.getPos();
            if (drawAllSprites || visible[pos.y()][pos.x()] == Constants.VISIBLE) {
                char sprite = monster.getSprite();
                Color color = monster.getColor();

                if (drawMonsterPaths && monster.getPath() != null) {
                    for (Position2D path : monster.getPath()) {
                        drawChar(g, sprite, path.x(), path.y(), Color.BLACK, Color.GRAY);
                    }
                }

                drawChar(g, sprite, pos.x(), pos.y(), Color.BLACK, color);
            }
        }

        int px = heroPos.x();
        int py = heroPos.y();
        Color pbg = Color.BLACK;
        Color pfg = Color.WHITE;

        drawChar(g, '@', px, py, pbg, pfg);
    }

    private void drawHud(Graphics2D g) {
        Color bg = Color.BLACK;
        Color fg = Color.WHITE;
        Color vbg = Color.BLACK;
        Color vfg = Color.YELLOW;

        int cx = 0;
        int cy = gridHeight - 3;

        String statLabel, statValue;

        statLabel = "HP: ";
        drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getHealth());
        drawString(g, statValue, cx, cy, vbg, Color.GREEN);
        cx += statValue.length();

        statLabel = "/";
        drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getModifiedMaxHealth());
        drawString(g, statValue, cx, cy, vbg, Color.GREEN);
        cx = 0;
        cy += 1;

        statLabel = "MP: ";
        drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getMana());
        drawString(g, statValue, cx, cy, vbg, Color.RED);
        cx += statValue.length();

        statLabel = "/";
        drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getModifiedMaxMana());
        drawString(g, statValue, cx, cy, vbg, Color.RED);
        cx = 0;
        cy += 1;

        statLabel = "EP: ";
        drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(Double.valueOf(hero.getEnergy()).intValue());
        drawString(g, statValue, cx, cy, vbg, Color.CYAN);
        cx += statValue.length();

        statLabel = "/";
        drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getModifiedMaxEnergy());
        drawString(g, statValue, cx, cy, vbg, Color.CYAN);
        cx = 0;
        cy += 1;

        statValue = String.valueOf(gameMap.getDepth());
        cx = gridWidth - statValue.length();
        cy = gridHeight - 2;
        drawString(g, statValue, cx, cy, vbg, vfg);
        statLabel = "Depth: ";
        cx -= statLabel.length();
        drawString(g, statLabel, cx, cy, bg, fg);


        statValue = String.valueOf(turns);
        cx = gridWidth - statValue.length();
        cy = gridHeight - 1;
        drawString(g, statValue, cx, cy, vbg, vfg);
        statLabel = "Turns: ";
        cx -= statLabel.length();
        drawString(g, statLabel, cx, cy, bg, fg);


        // draw last 3 status messages
        int xStatusOffset = 12;
        int limit = Math.min(messageLog.size(), 3);
        cx = xStatusOffset;
        cy = gridHeight - 1;
        for (int i = 0; i < limit; i++) {
            LogMessage logMessage = messageLog.get(messageLog.size() - 1 - i);
            for (ColoredString cStr : logMessage.getMessage()) {
                drawString(g, cStr.getMessage(), cx, cy, bg, cStr.getColor());
                cx += cStr.getMessage().length();
            }

            cx = xStatusOffset;
            cy -= 1;
        }
    }

    public KeyListener createKeyListener() {
        KeyListener keyListener = new KeyListener() {
            public void keyTyped(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SHIFT:
                        System.out.println("shift released");
                        shiftDown = false;
                        break;
                    case KeyEvent.VK_CONTROL:
                        System.out.println("control released");
                        ctrlDown = false;
                        break;
                    case KeyEvent.VK_META:
                        System.out.println("meta released");
                        metaDown = false;
                        break;
                    default:
                }
            }
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SHIFT:
                        System.out.println("shift pressed");
                        shiftDown = true;
                        playerAction = PlayerAction.UNKNOWN;
                        break;
                    case KeyEvent.VK_CONTROL:
                        System.out.println("control pressed");
                        ctrlDown = true;
                        playerAction = PlayerAction.UNKNOWN;
                        break;
                    case KeyEvent.VK_META:
                        System.out.println("meta pressed");
                        metaDown = true;
                        playerAction = PlayerAction.UNKNOWN;
                        break;
                    case KeyEvent.VK_COMMA:
                        playerAction = PlayerAction.UNKNOWN;
                        if (metaDown) {
                            upstairs();
                            canvas.repaint();
                        }
                        break;
                    case KeyEvent.VK_PERIOD:
                        playerAction = PlayerAction.UNKNOWN;
                        if (metaDown) {
                            downstairs();
                            canvas.repaint();
                        }
                        break;
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
                    case KeyEvent.VK_SPACE:
                        playerAction = PlayerAction.WAIT;
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
//                    case KeyEvent.VK_R:
//                        playerAction = PlayerAction.UNKNOWN;
//                        initCharGrid();
//                        canvas.repaint();
//                        break;
                    default:
                        playerAction = PlayerAction.UNKNOWN;
                        break;
                }
                if (playerAction != PlayerAction.UNKNOWN) {
                    gameLoop();
                }
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
                    case OK:
                        updated = okAction();
                        break;
                    case WAIT:
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
            case MENU:
                menuList.peek().updateMenu(playerAction);
                updated = true;
            break;
        }
        if (updated) {
            tick();
            canvas.repaint();
        }
    }

    private boolean moveMap(PlayerAction playerAction) {
        boolean updated = false;
        boolean keepMoving = shiftDown;
        do {
            Position2D next = heroPos.moveTowards(getDirection(playerAction));

            boolean passable = false;
            boolean withinBounds = withinBounds(next.y(), next.x());
            boolean occupiedByMonster = gameMap.getMonsterMap().get(next) != null;
            if (withinBounds) {
                char c = charGrid[next.y()][next.x()];
                passable = ".+<>".indexOf(c) != -1 && !occupiedByMonster;
            }
            if (withinBounds && (passable || metaDown)) {
                heroPos = next;
                fov(heroPos, FIELD_OF_VIEW_RANGE);
                hero.setEnergy(hero.getEnergy() - 0.05);
                updated = true;
            } else if (occupiedByMonster) {
                fightMonster(next);
                updated = true;
            } else {
                messageLog.add(new LogMessage("You bumped into a wall."));
                updated = true;
            }

            if (keepMoving) {
                keepMoving = passable;
            }
            if (keepMoving) {
                // check if neighboring squares are of interest
                List<Position2D> cardinalPositionsToCheck = heroPos.getNeighbors();
                cardinalPositionsToCheck.add(heroPos);
                Iterator<Position2D> it = cardinalPositionsToCheck.iterator();
                while (it.hasNext()) {
                    Position2D pos = it.next();
                    char c = charGrid[pos.y()][pos.x()];
                    if ("+".indexOf(c) != -1) {
                        keepMoving = false;
                        break;
                    } else if (c != '#') {
                        it.remove();
                    }
                }

//                // TODO clean up the auto-run code
//
//                List<Position2D> cornerPositions = new ArrayList<>();
//                cornerPositions.add(heroPos.above().left());
//                cornerPositions.add(heroPos.above().right());
//                cornerPositions.add(heroPos.below().left());
//                cornerPositions.add(heroPos.below().right());
//                it = cornerPositions.iterator();
//                while (it.hasNext()) {
//                    Position2D pos = it.next();
//                    char c = charGrid[pos.y()][pos.x()];
//                    if (c != '#') {
//                        it.remove();
//                    }
//                }
//
                if (cardinalPositionsToCheck.size() > 2
//                        && cornerPositions.size() != 0
                ) {
                    keepMoving = false;
                }
            }
            if (keepMoving) {
                tick();
//                canvas.repaint();
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                }
            }
        } while (keepMoving);
        return updated;

    }

    private void fightMonster(Position2D target) {
        Monster monster = gameMap.getMonsterMap().get(target);
        if (monster == null) {
            return;
        }
        int damage = hero.attack(monster);
        if (damage == 0) {
            LogMessage message = new LogMessage();
            message.append("Missed the ") ;
            message.append(monster.getName(), Color.YELLOW);
            message.append(".") ;
            messageLog.add(message);
        } else {
            LogMessage message = new LogMessage();
            message.append("Dealt the ");
            message.append(monster.getName(), Color.YELLOW);
            message.append(" ");
            message.append("" + damage, Color.RED);
            message.append(" damage!");
            messageLog.add(message);
        }
        if (monster.getHealth() <= 0) {
            gameMap.getMonsterMap().remove(monster.getPos());
            hero.setExp(hero.getExp() + monster.getExp());
            hero.setGold(hero.getGold() + monster.getGold());

            LogMessage message = new LogMessage();
            message.append("Defeated the ");
            message.append(monster.getName(), Color.YELLOW);
            message.append("!");
            messageLog.add(message);
            message = new LogMessage();
            message.append("Earned ");
            message.append("" + monster.getExp(), Color.CYAN);
            message.append(" exp and ");
            message.append("" + monster.getGold(), Color.YELLOW);
            message.append(" gold.");
            messageLog.add(message);
        }
    }

    public int distance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt((x2 - x1) * (x2 - x1)  + (y2 - y1) * (y2 - y1));
    }

    private void fov(Position2D center, int range) {
        // clear old lighting
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                if (visible[y][x] == Constants.VISIBLE) {
                    visible[y][x] = Constants.KNOWN;
                }
            }
        }

        // use simple line tracing
        for (int x = center.x() - range; x < center.x() + range; x++) {
            for (int y = center.y() - range; y < center.y() + range; y++) {
                if (withinBounds(y, x)) {
                    if (distance(center.x(), center.y(), x, y) <= range) {
                        List<Position2D> line = Bresenham.plotLine(center.x(), center.y(), x, y);

                        // the first position is always visible
                        Iterator<Position2D> it = line.iterator();
                        Position2D next = it.next();
                        visible[next.y()][next.x()] = Constants.VISIBLE;

                        while (it.hasNext()) {
                            next = it.next();
                            visible[next.y()][next.x()] = Constants.VISIBLE;

                            // check if a blocking character is encountered
                            char c = charGrid[next.y()][next.x()];
                            if ("#+".indexOf(c) != -1) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void upstairs() {
        GameMap nextMap = gameMap.getUpstairs().getTargetGameMap();
        setGameMap(nextMap);
        heroPos = gameMap.getDownstairs().getPos();
        fov(heroPos, FIELD_OF_VIEW_RANGE);
    }

    private void downstairs() {
        if (gameMap.getDownstairs().getTargetGameMap() == null) {
            int depth = gameMap.getDepth() + 1;
            GameMap nextMap = GameMapCreator.createGameMap(mapWidth, mapHeight, depth);
            nextMap.getUpstairs().setTargetGameMap(gameMap);
            gameMap.getDownstairs().setTargetGameMap(nextMap);
        }
        setGameMap(gameMap.getDownstairs().getTargetGameMap());
        heroPos = gameMap.getUpstairs().getPos();
        fov(heroPos, FIELD_OF_VIEW_RANGE);
    }


    private boolean okAction() {
        boolean updated = false;
        char c = gameMap.getCell(heroPos);

        // ------------------------------------------------
        // handle stairs
        // ------------------------------------------------
        if (c == Constants.UPSTAIRS) {
            upstairs();
            messageLog.add(new LogMessage("You ascend the stairs."));
            updated = true;
        } else if (c == Constants.DOWNSTAIRS) {
            messageLog.add(new LogMessage("You descend the stairs."));
            downstairs();
            updated = true;
        }

        return updated;
    }

    /**
     * Update all non-heroPos entities here.
     */
    private void tick() {
        turns++;
        char heroTile = charGrid[heroPos.y()][heroPos.x()];
        if (heroTile == '<') {
            messageLog.add(new LogMessage("Stairs are leading up."));
        } else if (heroTile == '>') {
            messageLog.add(new LogMessage("Stairs are leading down."));
        }
//        else if (heroTile == '+') {
//            messageLog.add(new LogMessage("You're standing in a doorway."));
//        }
        updateMonsters();
    }

    private void updateMonsters() {
        List<Monster> monsters = gameMap.getMonsterMap().values().stream()
                .collect(Collectors.toList());
        for (Monster monster : monsters) {
            Position2D monsterPos = monster.getPos();
            boolean isVisible = visible[monsterPos.y()][monsterPos.x()] == Constants.VISIBLE;

            // ----------------------------------------
            // move randomly if not visible
            // ----------------------------------------
            if (!isVisible && rng.nextBoolean()) {
                // move on existing path, else just move randomly
                Position2D newPos;
                if (monster.getPath() != null && !monster.getPath().isEmpty()) {
                    newPos = monster.getPath().remove(monster.getPath().size() - 1);
                } else {
                    newPos = getEmptyNeighboringPosition(monsterPos);
                }
                if (newPos != null) {
                    gameMap.getMonsterMap().remove(monsterPos);
                    gameMap.getMonsterMap().put(newPos, monster);
                    monster.setPos(newPos);
                }
            } else {
                // ----------------------------------------
                // hunt the player, they can be seen!
                // ----------------------------------------
                int mAgg = monster.getAggression();
                int aggression = 1 + rng.nextInt(10);
                if (isVisible && mAgg <= aggression) {
                    // pathfind;
                    if (monster.getPath() == null || !monster.getPath().contains(heroPos)) {
                        List<Position2D> path;
                        boolean useBfs = rng.nextBoolean();
                        if (useBfs) {
                            path = pathfindBFS(monsterPos, heroPos);
                        } else {
                            path = pathfindDFS(monsterPos, heroPos);
                        }
                        monster.setPath(path);
                    }
                    if (monster.getPath() != null && !monster.getPath().isEmpty()) {
                        Position2D newPos = monster.getPath().remove(monster.getPath().size() - 1);
                        if (newPos.equals(heroPos)) {
                            monster.setPath(null);
                            // do stuff
                            int damage = monster.attack(hero);
                            if (damage == 0) {
                                LogMessage message = new LogMessage();
                                message.append(monster.getName(), Color.YELLOW);
                                message.append(" missed you.") ;
                                messageLog.add(message);
                            } else {
                                LogMessage message = new LogMessage();
                                message.append(monster.getName(), monster.getColor());
                                message.append(" dealt you ");
                                message.append("" + damage, Color.RED);
                                message.append(" damage!");
                                messageLog.add(message);
                            }


                        } else if (gameMap.getMonsterMap().get(newPos) == null) {
                            gameMap.getMonsterMap().remove(monsterPos);
                            gameMap.getMonsterMap().put(newPos, monster);
                            monster.setPos(newPos);
                        }
                    }

                }
                // ----------------------------------------
                // aggression failed, just move randomly
                // ----------------------------------------
                else if (rng.nextBoolean()){
                    // move on existing path, else just move randomly
                    Position2D newPos;
                    if (monster.getPath() != null && !monster.getPath().isEmpty()) {
                        newPos = monster.getPath().remove(monster.getPath().size() - 1);
                    } else {
                        newPos = getEmptyNeighboringPosition(monsterPos);
                    }
                    if (newPos != null) {
                        gameMap.getMonsterMap().remove(monsterPos);
                        gameMap.getMonsterMap().put(newPos, monster);
                        monster.setPos(newPos);
                    }
                }
            }
        }
    }

    /**
     * Find a path of open positions ' ' between a starting and ending position
     * using a breadth first search.  Allow early termination upon discovery of
     * the terminalChars.
     * @param start The starting position.
     * @param finish The ending position.
     * @return A list of positions connecting start and finish.
     */
    public List<Position2D> pathfindBFS(Position2D start, Position2D finish) {
//        Direction startingDirection = getEmptyDirection(start);
//        if (startingDirection == null) {
//            return new ArrayList<>();
//        }
//        Position2D digger = start.moveTowards(startingDirection);
        Position2D digger = start;

        List<Position2D> visited = new ArrayList<>();
        visited.add(digger);

        List<Position2D> initial = new ArrayList<>();
        initial.add(digger);

        Queue<List<Position2D>> queue = new LinkedList<>();
        queue.add(initial);

        while (!queue.isEmpty()) {
            List<Position2D> next = queue.remove();
            Position2D head = next.get(0);
            if (head.equals(finish)) {
                next.remove(next.size() - 1);
                return next;
            }

            for (Position2D neighbor : head.getNeighbors()) {
                if (!visited.contains(neighbor)
                        && withinBounds(neighbor)
                        && "#+".indexOf(charGrid[head.y()][head.x()]) == -1
                        && gameMap.getMonsterMap().get(neighbor) == null) {
                    visited.add(neighbor);
                    List<Position2D> copy = new ArrayList<>(next);
                    copy.add(0, neighbor);
                    queue.add(copy);
                }
            }
        }
        return new ArrayList<>();
    }

    private List<Position2D> pathfindDFS(Position2D start, Position2D finish) {
        Set<Position2D> visited = new HashSet<>();
//        Direction startingDirection = getEmptyDirection(start);
//
////        cells[start.y()][start.x()] = '&';
//        if (startingDirection == null) {
//            return new ArrayList<>();
//        }
//        Position2D digger = start.moveTowards(startingDirection);
        Position2D digger = start;
        visited.add(start);
        visited.add(digger);
        Stack<List<Position2D>> stack = new Stack<>();
        List<Position2D> initial = new ArrayList<>();
        initial.add(digger);

        stack.push(initial);

        List<Direction> directions = new ArrayList<>();
        directions.add(Direction.UP);
        directions.add(Direction.DOWN);
        directions.add(Direction.LEFT);
        directions.add(Direction.RIGHT);
        Collections.shuffle(directions);
        int iterationRngLimit = 4 + rng.nextInt(4);
        int iterations = 1 + rng.nextInt(iterationRngLimit);

        while (!stack.isEmpty()) {
            List<Position2D> next = stack.pop();
            Position2D head = next.get(0);
            if (head.equals(finish)) {
                next.remove(next.size() - 1);
                return next;
            }

            List<Position2D> neighbors = new ArrayList<>();
            neighbors.add(head.moveTowards(directions.get(0)));
            neighbors.add(head.moveTowards(directions.get(1)));
            neighbors.add(head.moveTowards(directions.get(2)));
            neighbors.add(head.moveTowards(directions.get(3)));

            // reset if necessary
            iterations --;
            if (iterations == 0) {
                directions.clear();
                directions.add(Direction.UP);
                directions.add(Direction.DOWN);
                directions.add(Direction.LEFT);
                directions.add(Direction.RIGHT);
                Collections.shuffle(directions);
                iterations = 1 + rng.nextInt(iterationRngLimit);
            }

            for (int i = 0; i < 4; i++) {
                Position2D neighbor = neighbors.remove(0);
                if (!visited.contains(neighbor)
                        && withinBounds(neighbor)
                        && "#+".indexOf(charGrid[head.y()][head.x()]) == -1) {
                    visited.add(neighbor);
                    List<Position2D> copy = new ArrayList<>(next);
                    copy.add(0, neighbor);
                    stack.add(copy);
                }
            }
        }
        return new ArrayList<>();
    }


    private Direction getEmptyDirection(Position2D digger/*, String candidateChars*/){
        List<Direction> neighbors = new ArrayList<>();
        neighbors.add(Direction.UP);
        neighbors.add(Direction.DOWN);
        neighbors.add(Direction.LEFT);
        neighbors.add(Direction.RIGHT);
        Iterator<Direction> it2 = neighbors.iterator();
        while (it2.hasNext()) {
            Direction direction = it2.next();
            Position2D neighbor = digger.moveTowards(direction);
            if (!withinBounds(neighbor)) {
                it2.remove();
            } else {
                char cell = charGrid[neighbor.y()][neighbor.x()];
                if (cell != '.') {
                    it2.remove();
                }
            }
        }
        if (neighbors.isEmpty()) {
            return null;
        }
        Direction direction = neighbors.get(rng.nextInt(neighbors.size()));
        return direction;
    }


    private Position2D getEmptyNeighboringPosition(Position2D pos) {
        List<Position2D> neighbors = pos.getNeighbors();
        Iterator<Position2D> it = neighbors.iterator();
        while (it.hasNext()) {
            Position2D next = it.next();
            char c = charGrid[next.y()][next.x()];
            if ("#+".indexOf(c) != -1 ||
                    gameMap.getMonsterMap().get(next) != null) {
                it.remove();
            }
        }
        if (!neighbors.isEmpty()) {
            Position2D newPos = neighbors.get(rng.nextInt(neighbors.size()));
            return newPos;
        }
        return null;
    }

    private boolean withinBounds(Position2D pos) {
        return withinBounds(pos.y(), pos.x());
    }

    private boolean withinBounds(int y, int x) {
        return 0 <= x && x < mapWidth && 0 <= y && y < mapHeight;
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
