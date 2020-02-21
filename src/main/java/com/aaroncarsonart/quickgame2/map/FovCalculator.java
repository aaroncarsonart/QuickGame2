package com.aaroncarsonart.quickgame2.map;

import com.aaroncarsonart.quickgame2.Game;
import imbroglio.Position2D;

import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.List;

public class FovCalculator {

    public static final char VISIBLE = '.';
    public static final char HIDDEN = '%';

    int width;
    int height;
    char[][] charGrid;
    boolean[][] visible;

    public FovCalculator(char[][] charGrid, int width, int height) {
        this.width = width;
        this.height = height;
        this.charGrid = charGrid;
        this.visible = new boolean[height][width];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                visible[y][x] = false;
            }
        }
    }

    public int distance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt((x2 - x1) * (x2 - x1)  + (y2 - y1) * (y2 - y1));
    }

    public void calculateFov(Position2D center, int rangeLimit) {

        for (int x = center.x() - rangeLimit; x < center.x() + rangeLimit; x++) {
            for (int y = center.y() - rangeLimit; y < center.y() + rangeLimit; y++) {
                if (distance(center.x(), center.y(), x, y) < rangeLimit) {
                    List<Position2D> plot = plotLine(center.x(), center.y(), x, y);
                    for (Position2D pos : plot) {
                        char c = charGrid[pos.y()][pos.x()];
                        visible[pos.y()][pos.x()] = true;
                        if ("#".indexOf(c) == -1) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public String toString() {
        return toBorderedString();
    }

    public String toBorderedString() {
        StringBuilder sb = new StringBuilder();

        sb.append('+');
        for (int x = 0; x < width; x++) {
            sb.append(" -");
        }
        sb.append(" +\n");

        for (int y = 0; y < height; y++) {
            sb.append('|');
            for (int x = 0; x < width; x++) {
                sb.append(' ');
                sb.append(charGrid[y][x]);
            }
            sb.append(" |\n");
        }

        sb.append('+');
        for (int x = 0; x < width; x++) {
            sb.append(" -");
        }
        sb.append(" +\n");

        return sb.toString();
    }

    /**
     * Algorithm taken directly from: https://rosettacode.org/wiki/Bitmap/Bresenham%27s_line_algorithm#Java
     * @param x1 The first x coordinate.
     * @param y1 The first y coordinate.
     * @param x2 The second x coordinate.
     * @param y2 The second y coordinate.
     * @return A list of positions representing a line plotted between the coordinates.
     */
    private List<Position2D> plotLine(int x1, int y1, int x2, int y2) {
        List<Position2D> plot = new ArrayList<>();
        // delta of exact value and rounded value of the dependent variable
        int d = 0;

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int dx2 = 2 * dx; // slope scaling factors to
        int dy2 = 2 * dy; // avoid floating point

        int ix = x1 < x2 ? 1 : -1; // increment direction
        int iy = y1 < y2 ? 1 : -1;

        int x = x1;
        int y = y1;

        if (dx >= dy) {
            while (true) {
                plot.add(new Position2D(x, y));
                if (x == x2)
                    break;
                x += ix;
                d += dy2;
                if (d > dx) {
                    y += iy;
                    d -= dx2;
                }
            }
        } else {
            while (true) {
                plot.add(new Position2D(x, y));
                if (y == y2)
                    break;
                y += iy;
                d += dx2;
                if (d > dy) {
                    x += ix;
                    d -= dy2;
                }
            }
        }
        return plot;
    }

    public static void main(String[] args) {
        DungeonGenerator dungeonGenerator = new DungeonGenerator(60, 40);
        int width = dungeonGenerator.getWidth();
        int height = dungeonGenerator.getHeight();
        char[][] charGrid = dungeonGenerator.getCells();

        FovCalculator fovCalculator = new FovCalculator(charGrid, width, height);
        Position2D center = new Position2D(width / 2, height / 2);
        List<Position2D> plot = fovCalculator.plotLine(center.x(), center.y(), 0, 0);
        for (Position2D p : plot) {
            fovCalculator.charGrid[p.y()][p.x()] = '@';
        }
        System.out.println(fovCalculator);
    }


}
