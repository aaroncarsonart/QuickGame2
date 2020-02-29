package com.aaroncarsonart.quickgame2;

import com.aaroncarsonart.quickgame2.hero.Experience;
import com.aaroncarsonart.quickgame2.hero.Hero;
import com.aaroncarsonart.quickgame2.hero.HeroCreator;
import com.aaroncarsonart.quickgame2.inventory.Equipment;
import com.aaroncarsonart.quickgame2.inventory.Inventory;
import com.aaroncarsonart.quickgame2.inventory.Item;
import com.aaroncarsonart.quickgame2.inventory.RecoveryItem;
import com.aaroncarsonart.quickgame2.map.GameMap;
import com.aaroncarsonart.quickgame2.map.GameMapCreator;
import com.aaroncarsonart.quickgame2.menu.Callback;
import com.aaroncarsonart.quickgame2.menu.CenterMenuView;
import com.aaroncarsonart.quickgame2.menu.ConfirmMenu;
import com.aaroncarsonart.quickgame2.menu.ConsoleMenu;
import com.aaroncarsonart.quickgame2.menu.ConsoleMenuView;
import com.aaroncarsonart.quickgame2.menu.EquipmentMenuView;
import com.aaroncarsonart.quickgame2.menu.InventoryMenuView;
import com.aaroncarsonart.quickgame2.menu.LevelUpMenu;
import com.aaroncarsonart.quickgame2.menu.LevelUpMenuView;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Game {

    public static final Color BROWN = new Color(165, 82, 0);
    public static final Color DARK_BROWN = new Color(50, 15, 0);
    public static final Color DARKER_GRAY = new Color(15, 15, 15);

    boolean enableMeta = true;
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

    private int hudHeight = 5;

    private int mapWidth = gridWidth;
    private int mapHeight = gridHeight - hudHeight;

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
    private Callback menuCancelCallback = () -> {
        menuList.pop();
        if (menuList.empty()) {
            gameMode = GameMode.MAP;
        }
    };

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

    boolean victory = false;
    boolean gameOver = false;
    String gameOverMessage = null;

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
                if (menu.getMenuView() != null) {
                    menu.getMenuView().render(graphics2D, menu);
                }
            }

            if (gameOver) {
                drawGameOverScreen(graphics2D);
                frame.removeKeyListener(keyListener);
            }
            if (victory) {
                drawVictoryScreen(graphics2D);
                frame.removeKeyListener(keyListener);
            }
        }
    }

    private KeyListener keyListener;

    /**
     * Initialize the key game components.
     */
    Game() {
        // init swing
        frame = new JFrame("QuickGame 2");
        canvas.setPreferredSize(new Dimension(width, height));
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        keyListener = createKeyListener();
        frame.addKeyListener(keyListener);

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
        mainMenu.addMenuItem(new MenuItem("Equipment", () -> menuList.push(createEquipmentMenu())));
        mainMenu.addMenuItem(new MenuItem("Save", () -> {}));
        mainMenu.addMenuItem(new MenuItem("Exit", () -> System.exit(0)));
    }

    private Menu createEquipmentMenu() {
        Position2D menuOrigin = new Position2D(1, 1);
        MenuView menuView = new EquipmentMenuView(this, menuOrigin, "Equipment", hero.getInventory());
        Menu equipmentMenu = new Menu(menuView, MenuLayout.VERTICAL, menuCancelCallback);
        equipmentMenu.setMaxLength(30);

        Inventory inventory = hero.getInventory();

        Inventory.Slot[] inventorySlots = inventory.getSlots();
        for (Inventory.Slot slot : inventorySlots) {
            Item item = slot.getItem();
            String label = slot.getLabel();
            if (item != null && item instanceof Equipment) {
//                Equipment equipment = (Equipment) item;
//                Callback confirmCallback = () -> {
//                        equipItem(slot, equipment);
//                        menuCancelCallback.execute();
//                };
//                ConfirmMenu confirmMenu = new ConfirmMenu(null, MenuLayout.VERTICAL, menuCancelCallback, confirmCallback);
//                equipmentMenu.addMenuItem(new MenuItem(slot::getLabel, () -> menuList.push(confirmMenu)));
                equipmentMenu.addMenuItem(new MenuItem(slot::getLabel, () -> equipItem(slot, slot::getItem)));
            } else if (item == null) {
                // TODO implement unequip menu
                equipmentMenu.addMenuItem(new MenuItem(label, Color.DARK_GRAY, () -> {}));
            } else {
                equipmentMenu.addMenuItem(new MenuItem(label, Color.DARK_GRAY, () -> {}));
            }
        }
        return equipmentMenu;
    }

    private void equipItem(Inventory.Slot slot, Supplier<Item> supplier) {
        Item item = supplier.get();
        if (!(item instanceof Equipment)) {
            return;
        }
        Equipment newEquipment = (Equipment) item;
        Equipment prevEquipment = hero.equipItem(newEquipment, newEquipment.getType(), 1);

        // update inventory
        if (prevEquipment != null) {
            slot.setItem(prevEquipment);
            slot.setQuantity(1);
        } else {
            hero.getInventory().remove(newEquipment, slot);
        }
    }

    private Menu createInventoryMenu() {
        Position2D menuOrigin = new Position2D(1, 1);
        MenuView menuView = new InventoryMenuView(this, menuOrigin, "Inventory", hero.getInventory());
        Menu inventoryMenu = new Menu(menuView, MenuLayout.VERTICAL, menuCancelCallback);
        inventoryMenu.setMaxLength(30);

        Inventory inventory = hero.getInventory();
        Inventory.Slot[] inventorySlots = inventory.getSlots();
        for (Inventory.Slot slot : inventorySlots) {
            Item item = slot.getItem();
            String label = slot.getLabel();
            if (item != null && slot.getQuantity() > 0) {
                inventoryMenu.addMenuItem(new MenuItem(slot::getLabel, () -> useItem(slot)));
            } else {
                inventoryMenu.addMenuItem(new MenuItem(slot::getLabel, () -> {}));
            }
        }
        return inventoryMenu;
    }

    private void useItem(Inventory.Slot slot) {
        if (slot.getQuantity() == 0) {
            return;
        }
        Item item = slot.getItem();
        if (item instanceof RecoveryItem) {
            useRecoveryItem((RecoveryItem) item);
            hero.getInventory().remove(item);
        }
    }

    private void useRecoveryItem(RecoveryItem recoveryItem) {
        LogMessage message = new LogMessage();
        message.append("Recovered ");

        int health = recoveryItem.getHealth();
        int mana = recoveryItem.getMana();
        int energy = recoveryItem.getEnergy();
        if (health > 0) {
            int newHealth = Math.min(hero.getMaxHealth(), hero.getHealth() + health);
            hero.setHealth(newHealth);
            message.append("+" + health, Color.GREEN);
            message.append(" HP ");
        }

        if (mana > 0) {
            int newMana = Math.min(hero.getMaxMana(), hero.getMana() + mana);
            hero.setMana(newMana);
            message.append("+" + mana, Color.RED);
            message.append(" MP ");
        }

        if (energy > 0) {
            double newEnergy = Math.min(hero.getMaxEnergy(), hero.getEnergy() + energy);
            hero.setEnergy(newEnergy);
            message.append("+" + ((int) energy), Color.CYAN);
            message.append(" EP ");
        }
        messageLog.add(message);
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
                } else if (visible[gy][gx] == Constants.KNOWN && !drawAllSprites) {
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

        // draw items
        for (Map.Entry<Position2D, Item> entry : gameMap.getItems().entrySet()) {
            Position2D pos = entry.getKey();
            Item item = entry.getValue();
            if (drawAllSprites || visible[pos.y()][pos.x()] == Constants.VISIBLE) {
                Color color = item.getColor();
                char sprite = item.getSprite();
                drawChar(g, sprite, pos.x(), pos.y(), Color.BLACK, color);
            }
        }

        // draw monsters
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
        int cy = gridHeight - hudHeight;

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
        drawString(g, statValue, cx, cy, vbg, Color.BLUE);
        cx += statValue.length();

        statLabel = "/";
        drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(hero.getModifiedMaxEnergy());
        drawString(g, statValue, cx, cy, vbg, Color.BLUE);
        cx = 0;
        cy += 1;

        statLabel = "XP: ";
        drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(Double.valueOf(hero.getExp()).intValue());
        drawString(g, statValue, cx, cy, vbg, Color.CYAN);
        cx += statValue.length();

        statLabel = "/";
        drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(Experience.TABLE[hero.getLevel() + 1]);
        drawString(g, statValue, cx, cy, vbg, Color.CYAN);
        cx = 0;
        cy += 1;

        statLabel = "GP: ";
        drawString(g, statLabel, cx, cy, bg, fg);
        cx += statLabel.length();
        statValue = String.valueOf(Double.valueOf(hero.getGold()).intValue());
        drawString(g, statValue, cx, cy, vbg, Color.YELLOW);
        cx += statValue.length();
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
        int xStatusOffset = 13;
        int limit = Math.min(messageLog.size(), hudHeight);
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
                int keyCode = e.getKeyCode();
                switch (keyCode) {
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
                        if (enableMeta && metaDown) {
                            upstairs();
                            canvas.repaint();
                        }
                        break;
                    case KeyEvent.VK_PERIOD:
                        playerAction = PlayerAction.UNKNOWN;
                        if (enableMeta && metaDown) {
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
                        if (shiftDown && keyCode == KeyEvent.VK_L) {
                            playerAction = PlayerAction.TOGGLE_AUTO_LOOT;
                        } else if (enableMeta && metaDown && keyCode == KeyEvent.VK_L) {
                            playerAction = PlayerAction.CHEAT_LEVEL_UP;
                        } else {
                            playerAction = PlayerAction.RIGHT;
                        }
                        break;
                    case KeyEvent.VK_C:
                        if (metaDown) {
                            playerAction = PlayerAction.CHEAT_COLLECT_ITEMS;
                        } else {
                            playerAction = PlayerAction.UNKNOWN;
                        }
                        break;
                    case KeyEvent.VK_M:
                        if (metaDown) {
                            playerAction = PlayerAction.CHEAT_DRAW_ALL_SPRITES;
                        } else {
                            playerAction = PlayerAction.UNKNOWN;
                        }
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
                    case KeyEvent.VK_S:
                        playerAction = PlayerAction.STATUS_MENU;
                        break;
                    case KeyEvent.VK_I:
                        playerAction = PlayerAction.INVENTORY_MENU;
                        break;
                    case KeyEvent.VK_E:
                        playerAction = PlayerAction.EQUIPMENT_MENU;
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
                    case TOGGLE_AUTO_LOOT:
                        toggleAutoLoot();
                        updated = true;
                        break;
                    case CHEAT_COLLECT_ITEMS:
                        for (Item item : gameMap.getItems().values()) {
                            hero.getInventory().add(item);
                        }
                        gameMap.getItems().clear();
                        messageLog.add(new LogMessage("Collected items on this floor."));
                        updated = true;
                        break;
                    case CHEAT_LEVEL_UP:
                        hero.setExp(Experience.TABLE[hero.getLevel() + 1]);
                        updated = true;
                        break;
                    case CHEAT_DRAW_ALL_SPRITES:
                        drawAllSprites = !drawAllSprites;
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
                    case EQUIPMENT_MENU:
                        gameMode = GameMode.MENU;
                        menuList.push(createEquipmentMenu());
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
            if (gameMode == GameMode.MAP) {
                tick();
            }
            canvas.repaint();
        }
    }

    private void toggleAutoLoot() {
        autoLootItems = !autoLootItems;
        LogMessage message = new LogMessage();
        message.append("Auto-loot mode ");
        message.append(autoLootItems ? "enabled" : "disabled", Color.YELLOW);
        message.append(".");
        messageLog.add(message);
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
            if (withinBounds && (passable || (enableMeta && (metaDown && !occupiedByMonster)))) {
                heroPos = next;
                fov(heroPos, FIELD_OF_VIEW_RANGE);
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

        // hack to easy-kill monsters in dev testing
        if (1 + rng.nextInt(100) == 100 || enableMeta && metaDown) {
            damage = monster.getMaxHealth();
            int newHealth = Math.max(0, monster.getHealth() - damage);
            monster.setHealth(newHealth);
            messageLog.add(new LogMessage("Perfect Strike!", Color.GREEN));
        }

        if (damage == 0) {
            LogMessage message = new LogMessage();
            message.append("You missed the ") ;
            message.append(monster.getName(), monster.getColor());
            message.append(".") ;
            messageLog.add(message);
        } else {
            if (damage == -1) {
                messageLog.add(new LogMessage("Critical Hit!", Color.GREEN));
                damage = hero.getCriticalHitDamage(monster);
                int newHealth = Math.max(0, monster.getHealth() - damage);
                monster.setHealth(newHealth);
            }
            LogMessage message = new LogMessage();
            message.append("You dealt the ");
            message.append(monster.getName(), monster.getColor());
            message.append(" ");
            message.append("" + damage, Color.RED);
            message.append(" damage!");
            messageLog.add(message);

            int aggression = Math.min(monster.getAggression() + 1 + rng.nextInt(5), 10);
            monster.setAggression(aggression);
        }
        if (monster.getHealth() <= 0) {
            gameMap.getMonsterMap().remove(monster.getPos());
            hero.setExp(hero.getExp() + monster.getExp());
            hero.setGold(hero.getGold() + monster.getGold());

            LogMessage message = new LogMessage();
            message.append("Defeated the ");
            message.append(monster.getName(), monster.getColor());
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
        // handle items
        // ------------------------------------------------
        updated = collectItem();


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

    private boolean collectItem() {
        Item item = gameMap.getItems().get(heroPos);
        if (item != null) {
            gameMap.getItems().remove(heroPos);
            hero.getInventory().add(item);
            LogMessage logMessage = new LogMessage();
            logMessage.append("You pick up the ");
            logMessage.append(item.getName(), Color.YELLOW);
            logMessage.append(".");
            messageLog.add(logMessage);
            return true;
        }
        return false;
    }

    private boolean autoLootItems = true;

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
        } else if (gameMap.getItems().get(heroPos) != null) {
            if (autoLootItems) {
                collectItem();
            } else {
                String itemName = gameMap.getItems().get(heroPos).getName();
                messageLog.add(new LogMessage(itemName + " lies below you."));
            }
        }
//        else if (heroTile == '+') {
//            messageLog.add(new LogMessage("You're standing in a doorway."));
//        }

        // spot new monsters
        Map<Monster, Integer> monstersSeen = new HashMap<>();
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                if (visible[y][x] == Constants.VISIBLE) {
                    Position2D pos = new Position2D(x, y);
                    Monster monster = gameMap.getMonsterMap().get(pos);
                    if (monster != null && !monster.isSeen()) {
                        monster.setSeen(true);
                        if (monstersSeen.get(monster) == null) {
                            monstersSeen.put(monster, 1);
                        } else {
                            monstersSeen.put(monster, monstersSeen.get(monster) + 1);
                        }
                    }
                }
            }
        }
        for (Map.Entry<Monster, Integer> entry : monstersSeen.entrySet()) {
            Monster monster = entry.getKey();
            int count = entry.getValue();

            String monsterName = monster.getName();
            String countStr;
            String plural;
            String wasWere;
            if (count == 1) {
                plural = "";
                wasWere = "was";
                if ("aeiou".indexOf(monsterName.charAt(0)) == -1) {
                    countStr = "An ";
                } else {
                    countStr = "A ";
                }
            } else {
                countStr = count + " ";
                wasWere = "were";
                if (monsterName.charAt(monsterName.length() - 1) == 's') {
                    plural = "es";
                } else {
                    plural = "s";
                }
            }

            LogMessage message = new LogMessage();
            message.append(countStr);
            message.append(monsterName + plural, Color.RED);
            message.append(" "+ wasWere + " spotted!");
        }

        updateHero();
        updateMonsters();
        checkForGameOver();
        checkForVictory();
    }

    private void updateHero() {
        hero.setEnergy(hero.getEnergy() - (0.05 - 0.001 * hero.getStamina()));

        if (hero.getEnergy() == 50) {
            messageLog.add(new LogMessage("You begin to sweat."));
        } else if (hero.getEnergy() == 25) {
            messageLog.add(new LogMessage("You feel exhausted."));
        } else if (hero.getEnergy() == 12) {
            messageLog.add(new LogMessage("It's difficult to stand."));
        } else if (hero.getEnergy() == 5) {
            messageLog.add(new LogMessage("You're about to collapse."));
        }

        // handle level-up
        if (hero.getExp() >= Experience.TABLE[hero.getLevel() + 1]) {
            hero.setLevel(hero.getLevel() + 1);

            int gainedHp = 1 + rng.nextInt(10) + hero.getModifiedStamina() / 2;
            hero.setMaxHealth(hero.getMaxHealth() + gainedHp);
            hero.setHealth(hero.getHealth() + gainedHp);

            int gainedMp = 1 + rng.nextInt(5) + hero.getModifiedWisdom() / 2;
            hero.setMaxMana(hero.getMaxMana() + gainedMp);
            hero.setMana(hero.getMana() + gainedMp);

//            int gainedEp = 1 + rng.nextInt(10) + hero.getModifiedStrength() / 4 + hero.getModifiedStamina() / 4;
//            hero.setMaxEnergy(hero.getMaxEnergy() + gainedEp);
//            hero.setEnergy(hero.getEnergy() + gainedEp);

            gameMode = GameMode.MENU;
            menuList.push(createLevelUpMenu());
        }
    }

    public Menu createLevelUpMenu() {
        Position2D origin = new Position2D(1, 1);
        LevelUpMenuView menuView = new LevelUpMenuView(this, origin, "Level Up!");
        int statPoints = hero.getLevel() / 2 + hero.getModifiedIntelligence() / 2;
        Menu menu = new LevelUpMenu(menuView, MenuLayout.VERTICAL, this, statPoints);
        return menu;
    }

    public Callback getMenuCancelCallback() {
        return menuCancelCallback;
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
                if (newPos != null && !newPos.equals(heroPos)) {
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
                                message.append(monster.getName(), monster.getColor());
                                message.append(" missed you.") ;
                                messageLog.add(message);
                            } else {
                                if (damage == -1) {
                                    messageLog.add(new LogMessage("Critical Hit!", Color.RED));
                                    damage = monster.getCriticalHitDamage(hero);
                                    int newHealth = Math.max(0, hero.getHealth() - damage);
                                    hero.setHealth(newHealth);
                                }
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
                    if (newPos != null && !newPos.equals(heroPos)) {
                        gameMap.getMonsterMap().remove(monsterPos);
                        gameMap.getMonsterMap().put(newPos, monster);
                        monster.setPos(newPos);
                    }
                }
            }

            // update aggression
            int newAggression = Math.min(monster.getBaseAggression(), monster.getAggression() - 1);
            monster.setAggression(newAggression);
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
            if (withinBounds(next)) {
                char c = charGrid[next.y()][next.x()];
                if ("#+".indexOf(c) != -1 ||
                        next.equals(heroPos) ||
                        gameMap.getMonsterMap().get(next) != null) {
                    it.remove();
                }
            } else {
                it.remove();
            }
        }
        if (!neighbors.isEmpty()) {
            Position2D newPos = neighbors.get(rng.nextInt(neighbors.size()));
            return newPos;
        }
        return null;
    }

    private void checkForGameOver() {
        if (hero.getHealth() < 1) {
            gameOver = true;
            gameOverMessage = hero.getName() + " was slain.";
        } else if (hero.getEnergy() < 1) {
            gameOver = true;
            gameOverMessage = hero.getName() + " ran out of energy.";
        }
    }

    private void drawGameOverScreen(Graphics2D g) {
        Color bg = Color.BLACK;
        Color fg = Color.WHITE;

        int px = gridWidth / 2 - gameOverMessage.length() / 2;
        int py = gridHeight / 2 - 2;
        int pw = gameOverMessage.length();
        int ph = 3;

        Position2D origin = new Position2D(px, py);

        // just use a menuView for drawing borders
        MenuView menuView = new CenterMenuView(this);
        menuView.drawMenuBorders(g, origin, pw, ph);

        for (int x = px; x < px + pw; x++) {
            for (int y = py; y < py + ph; y++) {
                drawChar(g, ' ', x, y, bg, fg);
            }
        }
        drawString(g, gameOverMessage, px, py + 2, bg, fg);
        String gameOver = "GAME OVER";
        px = gridWidth / 2 - gameOver.length() / 2 - 1;
        drawString(g, gameOver,px, py, bg, Color.RED);
    }

    private void checkForVictory() {

    }

    private void drawVictoryScreen(Graphics2D g) {
        String victoryMessage = hero.getName() + " has successfully plundered";
        String victoryMessage2 = "the depths of Capricious Caverns";
        String victoryMessage3 = "and collected the 5 Orbs of Destiny.";

        Color bg = Color.BLACK;
        Color fg = Color.WHITE;

        int pw = Math.max(victoryMessage.length(), victoryMessage3.length());
        int ph = 5;
        int px = gridWidth / 2 - pw / 2;
        int py = gridHeight / 2 - 3;

        Position2D origin = new Position2D(px, py);

        // just use a menuView for drawing borders
        MenuView menuView = new CenterMenuView(this);
        menuView.drawMenuBorders(g, origin, pw, ph);

        for (int x = px; x < pw; x++) {
            for (int y = py; y < ph; y++) {
                drawChar(g, ' ', x, y, bg, fg);
            }
        }
        px = gridWidth / 2 - victoryMessage.length() / 2;
        drawString(g, victoryMessage, px, py + 2, bg, fg);

        px = gridWidth / 2 - victoryMessage2.length() / 2;
        drawString(g, victoryMessage2, px, py + 3, bg, fg);

        px = gridWidth / 2 - victoryMessage3.length() / 2;
        drawString(g, victoryMessage3, px, py + 4, bg, fg);

        String victoryLabel = "VICTORY!";
        px = gridWidth / 2 - victoryLabel.length() / 2;
        drawString(g, victoryLabel,px, py, bg, Color.GREEN);
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
