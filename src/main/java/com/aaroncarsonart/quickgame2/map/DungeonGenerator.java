package com.aaroncarsonart.quickgame2.map;

import imbroglio.Direction;
import imbroglio.Position2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

public class DungeonGenerator {

    public static final char PATH = '.';
    public static final char WALL = '#';
    public static final char CORRIDOR_WALL = '#';

    private int width;
    private int height;
    private char[][] cells;
    private static Random rng = new Random(1); // Constants.RNG;

    private List<Room> rooms;

    public DungeonGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new char[height][width];
        rooms = generateRoomedDungeon();
        for (Room room : rooms) {
            for (Position2D door : room.getDoors()) {
                cells[door.y()][door.x()] = '.';
            }
        }
        List<Set<Position2D>> components = this.getConnectedComponents('.');
//        Iterator<Set<Position2D>> componentIterator = components.iterator();
//        while (componentIterator.hasNext()) {
//            Set<Position2D> component = componentIterator.next();
//            Position2D candidate = component.iterator().next();
//            for (Room room : rooms) {
//                if (room.contains(candidate)) {
//                    componentIterator.remove();
//                    break;
//                }
//            }
//        }
        List<Position2D> cooridors = connectDisconnectedComponents(components, '.');
        drawBorders(cooridors, CORRIDOR_WALL);
        for (Room room : rooms) {
            for (Position2D door : room.getDoors()) {
                Position2D above = door.above();
                Position2D below = door.below();
                Position2D left = door.left();
                Position2D right = door.right();
                if (cells[above.y()][above.x()] == ' ' || cells[below.y()][below.x()] == ' ' ||
                        cells[left.y()][left.x()] == ' ' || cells[right.y()][right.x()] == ' ') {
                    cells[door.y()][door.x()] = '#';
                } else if (((cells[above.y()][above.x()] == '#' && cells[below.y()][below.x()] == '#') ||
                        (cells[left.y()][left.x()] == '#' && cells[right.y()][right.x()] == '#'))) {
                    cells[door.y()][door.x()] = '+';
                }
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public char[][] getCells() {
        return cells;
    }

    private List<Room> generateRoomedDungeon() {
        List<Position2D> openCells = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[y][x] = ' ';
                openCells.add(new Position2D(x, y));
            }
        }

        // ------------------------------------------------
        // create rooms
        // ------------------------------------------------
        int min = 5;
        int max = 12;
        int attempts = 30;
        List<Room> rooms = new ArrayList<>();

        for (int i = 0; i < attempts; i++) {
            int x1 = rng.nextInt(width - max);
            int y1 = rng.nextInt(height - max);

            int x2 = x1 + min + rng.nextInt(max - min);
            int y2 = y1 + min + rng.nextInt(max - min);

            // skip overlapping rooms
            boolean overlapping = false;
            for (int x = x1; x < x2; x++) {
                for (int y = y1; y < y2; y++) {
                    if (cells[y][x] == '.') {
                        overlapping = true;
                        break;
                    }
                }
                if (overlapping) {
                    break;
                }
            }
            if (overlapping) {
                continue;
            }

            // draw room
            Position2D roomPos = new Position2D(x1, y1);
            int roomWidth = x2 - x1;
            int roomHeight = y2 - y1;
            Room rect = new Room(roomPos, roomWidth, roomHeight);
            rooms.add(rect);
            for (int x = x1; x < x2; x++) {
                for (int y = y1; y < y2; y++) {
                    Position2D pos = new Position2D(x, y);
                    openCells.remove(pos);
                    if (x == x1 || x == x2 - 1 || y == y1 || y == y2 - 1) {
                        cells[y][x] = '#';
                    } else {
                        cells[y][x] = '.';
                    }
                }
            }
        }

        // ------------------------------------------------
        // add doors to rooms
        // ------------------------------------------------
        Iterator<Room> it = rooms.iterator();
        List<Position2D> doors = new ArrayList<>();
        while(it.hasNext()) {
            Room room = it.next();
            //it.remove();

            int doorsToAdd = 1 + rng.nextInt(4);
            List<Integer> walls = new ArrayList<>();
            walls.add(0);
            walls.add(1);
            walls.add(2);
            walls.add(3);
            for (int i = 0; i < doorsToAdd; i++) {
                // pick a random wall
                int wall = walls.remove(rng.nextInt(walls.size()));

                // pick a middle section of that wall
                int wx, wy;
                switch(wall) {
                    default:
                    case 0: // NORTH
                        wx = room.getOrigin().x() + 1 + rng.nextInt(room.getWidth() - 2);
                        wy = room.getOrigin().y();
                        break;
                    case 1: // SOUTH
                        wx = room.getOrigin().x() + 1 + rng.nextInt(room.getWidth() - 2);
                        wy = room.getOrigin().y() + room.getHeight() - 1;
                        break;
                    case 2: // WEST
                        wx = room.getOrigin().x();
                        wy = room.getOrigin().y() + 1 + rng.nextInt(room.getHeight() - 2);
                        break;
                    case 3: // EAST
                        wx = room.getOrigin().x() + room.getWidth() - 1;
                        wy = room.getOrigin().y() + 1 + rng.nextInt(room.getHeight() - 2);
                        break;
                }
                Position2D door = new Position2D(wx, wy);
                if (!adjacentToEdgeOfMap(door) && !adjacentToWall(door)) {

                    cells[wy][wx] = '+';
                    doors.add(door);
                    room.getDoors().add(door);
                }
            }
        }

        // ------------------------------------------------
        // connect doors
        // ------------------------------------------------
        List<Position2D> cooridors = new ArrayList<>();
        for (Room room : rooms) {
            for (Position2D door : room.getDoors()) {

                Position2D door1 = door;
                Position2D door2;
                if (!doors.isEmpty()) {
                    door2 = doors.get(rng.nextInt(doors.size()));
                    while (room.contains(door2)) {
                        door2 = doors.get(rng.nextInt(doors.size()));
                    }
                } else {
                    door2 = openCells.get(rng.nextInt(openCells.size()));
                }

                List<Position2D> path;
                if (rng.nextBoolean()) {
                    path = pathfindBFS(door1, door2, ".+");
                } else {
                    path = pathfindDFS(door1, door2, ".+");
                }
//                if (path.isEmpty()) {
//                    cells[door1.y()][door1.x()] = '#';
//                } else {
                    for (Position2D cell : path) {
                        cells[cell.y()][cell.x()] = '.';
                        cooridors.add(cell);
//                    }
                }
            }
        }
        drawBorders(cooridors, CORRIDOR_WALL);
        return rooms;
    }

    private void drawBorders(List<Position2D> cooridors, char borderChar) {
        for (Position2D pos : cooridors) {
            List<Position2D> surroundingCells = Arrays.asList(
                    pos.above().left(), pos.above(), pos.above().right(),
                    pos.left(), pos.right(),
                    pos.below().left(), pos.below(), pos.below().right());
            for (Position2D surroundingCell : surroundingCells) {
                if (//withinBounds(surroundingCell) &&
                        cells[surroundingCell.y()][surroundingCell.x()] == ' ') {
                    cells[surroundingCell.y()][surroundingCell.x()] = borderChar;
                }
            }
        }

    }

    private Direction getEmptyDirection(Position2D digger){
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
                char cell = cells[neighbor.y()][neighbor.x()];
                if (cell != ' ') {
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

    private boolean adjacentToEdgeOfMap(Position2D pos) {
        return pos.x() == 0 || pos.x() == width - 1
                || pos.y() == 0 || pos.y() == height - 1;
    }

    private boolean adjacentToWall(Position2D pos) {
        return cells[pos.y() + 1][pos.x()] == '.' && cells[pos.y() - 1][pos.x()] == '#' ||
                cells[pos.y() + 1][pos.x()] == '#' && cells[pos.y() - 1][pos.x()] == '.' ||
                cells[pos.y()][pos.x() + 1] == '.' && cells[pos.y()][pos.x() - 1] == '#' ||
                cells[pos.y()][pos.x() + 1] == '#' && cells[pos.y()][pos.x() - 1] == '.';
    }

    /**
     * Find a path of open positions ' ' between a starting and ending position
     * using a depth first search.  Allow early termination upon discovery of
     * the terminalChars.
     * @param start The starting position.
     * @param finish The ending position.
     * @param terminalChars Characters to terminate the search early upon discovery.
     * @return A list of positions connecting start and finish.
     */
    private List<Position2D> pathfindDFS(Position2D start, Position2D finish, String terminalChars) {
        Set<Position2D> visited = new HashSet<>();
        Direction startingDirection = getEmptyDirection(start);

//        cells[start.y()][start.x()] = '&';
        if (startingDirection == null) {
            return new ArrayList<>();
        }
        Position2D digger = start.moveTowards(startingDirection);
        visited.add(start);
        visited.add(digger);
        Stack<List<Position2D>> stack = new Stack<>();
        List<Position2D> initial = new ArrayList<>();
        initial.add(digger);

        stack.push(initial);

        // TODO WORKING ON THIS

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
            if (head.equals(finish) || terminalChars.indexOf(cells[head.y()][head.x()]) != -1) {
                next.remove(0);
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
                        && cells[head.y()][head.x()] != '#') {
                    visited.add(neighbor);
                    List<Position2D> copy = new ArrayList<>(next);
                    copy.add(0, neighbor);
                    stack.add(copy);
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Find a path of open positions ' ' between a starting and ending position
     * using a breadth first search.  Allow early termination upon discovery of
     * the terminalChars.
     * @param start The starting position.
     * @param finish The ending position.
     * @param terminalChars Characters to terminate the search early upon discovery.
     * @return A list of positions connecting start and finish.
     */
    public List<Position2D> pathfindBFS(Position2D start, Position2D finish, String terminalChars) {
        Direction startingDirection = getEmptyDirection(start);
        if (startingDirection == null) {
            return new ArrayList<>();
        }
        Position2D digger = start.moveTowards(startingDirection);

        List<Position2D> visited = new ArrayList<>();
        visited.add(start);
        visited.add(digger);

        List<Position2D> initial = new ArrayList<>();
        initial.add(digger);

        Queue<List<Position2D>> queue = new LinkedList<>();
        queue.add(initial);

        while (!queue.isEmpty()) {
            List<Position2D> next = queue.remove();
            Position2D head = next.get(0);
            if (head.equals(finish) || terminalChars.indexOf(cells[head.y()][head.x()]) != -1) {
                next.remove(0);
                return next;
            }

            for (Position2D neighbor : head.getNeighbors()) {
                if (!visited.contains(neighbor)
                        && withinBounds(neighbor)
                        && cells[head.y()][head.x()] != '#') {
                    visited.add(neighbor);
                    List<Position2D> copy = new ArrayList<>(next);
                    copy.add(0, neighbor);
                    queue.add(copy);
                }
            }
        }
        return new ArrayList<>();
    }


    private boolean withinBounds(Position2D pos) {
        return 1 <= pos.x() && pos.x() < width - 1 &&
                1 <= pos.y() && pos.y() < height - 1;
    }

    private boolean occupied(Position2D pos) {
        char cell = cells[pos.y()][pos.x()];
        return cell != ' ' && cell != '.';
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
                sb.append(cells[y][x]);
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
     * Creates a graph traversal of this maze.
     *
     * @return A graph of positional data.
     */
    public List<Set<Position2D>> getConnectedComponents(char componentChar) {

        List<Set<Position2D>> components = new ArrayList<Set<Position2D>>();
        HashSet<Position2D> visited = new HashSet<Position2D>();
        Queue<Position2D> queue = new LinkedList<Position2D>();

        Position2D start = findFirstUnvisitedCell(componentChar, visited);

        while (start != null) {
            Set<Position2D> component = new HashSet<Position2D>();
            components.add(component);
            // add them to the queue, and begin searching.
            visited.add(start);
            queue.add(start);
            component.add(start);

            // find all connected components to this element.
            while (!queue.isEmpty()) {
                Position2D current = queue.remove();
                for (Position2D neighbor : current.getNeighbors()) {

                    // case 1: been here already.
                    if (visited.contains(neighbor)) {
                        // LOG.trace("already visited");
                        continue;
                    }

                    // case 2: it is a wall
                    else if (cells[neighbor.y()][neighbor.x()] != componentChar) {
                        // LOG.trace("found wall");
                        continue;
                    }

                    // case 4: it is an open path
                    else {
                        // LOG.trace("found path from " + current + " to " + neighbor);

                        // mark this origin as added
                        visited.add(neighbor);

                        //graph.addVertex(neighbor);
                        //graph.addUndirectedEdge(current, neighbor);
                        component.add(neighbor);

                        // add this state to the state queue.
                        queue.add(neighbor);
                    }
                }
            }
            // start = null;
            start = findFirstUnvisitedCell(PATH, visited);
        }

        return components;
    }

    /**
     * Get the first unvisited cell from this Maze of the given type, using a
     * boolean visited array that is tracking what positions have been visited.
     *
     * @param cellType The type of cell to find.
     * @param visited The array of booleans that contain visited data
     * @return The top, left-most unvisited origin, or null if every origin
     *         has been visited.
     */
    public Position2D findFirstUnvisitedCell(char cellType,
                                             HashSet<Position2D> visited) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!visited.contains(new Position2D(x, y))
                        && cells[y][x] == cellType) {
                    return new Position2D(x, y);
                }
            }
        }
        return null;
    }


    /**
     * Using a graph, finds all connected components, then connects them.
     * @return A list of the positions that were added.
     */
    public List<Position2D> connectDisconnectedComponents(List<Set<Position2D>> components, char componentChar) {
        List<Position2D> addedPositions = new ArrayList<>();

        // ***************************************
        // 1. get disconnected components.
        // ***************************************
//        System.out.printf("Disconnected Components: %d\n", components.size());
//        System.out.printf("Paths: %d\n", countPaths());

        // *******************************************************
        // 2. remove the front set, as a starting point.
        // ********************************************************
        Iterator<Set<Position2D>> setIterator = components.iterator();
        Set<Position2D> result = null;
        if(setIterator.hasNext()){
            result = setIterator.next();
            setIterator.remove();
        }

        // connect each other component to this one.
        while(setIterator.hasNext()){
            // get and remove the nextInt element.
            Set<Position2D> nextComponent = setIterator.next();
            setIterator.remove();

            // dig a path from this set to the result.
            //TODO: dig to the nextInt thing.
            Iterator<Position2D> it = nextComponent.iterator();
            Position2D digger = null;
            if (it.hasNext()){
                digger = it.next();
                it.remove();
            }

            // find a path to the result.
            List<Position2D> path = findPathToComponent(result, digger);

            // make the path positions, and add them to the result.
            for(Position2D p : path){
                cells[p.y()][p.x()] = PATH;
                result.add(p);
                addedPositions.add(p);
            }

            // add all the positions in this set to the result set.
            for(Position2D p : nextComponent){
                result.add(p);
            }
        }
        return addedPositions;
    }

    /**
     * This method finds a path from a starting point to a set of Positions.
     * This path may meander over existing paths or walls to get there.  It
     * does not modify the underlying data structure.
     * @param component The set of Positions to find a path to.
     * @param start The starting origin.
     * @return
     */
    public List<Position2D> findPathToComponent(Set<Position2D> component, Position2D start){
        HashSet<Position2D> visited = new HashSet<Position2D>();
        LinkedList<Position2D> path = new LinkedList<Position2D>();
        path.add(start);
        visited.add(start);

        //check neighboring positions.
        List<Direction> directions = new ArrayList<>();
        directions.add(Direction.UP);
        directions.add(Direction.DOWN);
        directions.add(Direction.LEFT);
        directions.add(Direction.RIGHT);
        Collections.shuffle(directions);
        int iterationRngLimit = 4 + rng.nextInt(4);
        int iterations = 1 + rng.nextInt(iterationRngLimit);

        // run search
        while(!path.isEmpty()){
            Position2D current = path.peek();

            // TODO WORKING ON THIS

            List<Position2D> neighbors = new ArrayList<>();
            neighbors.add(current.moveTowards(directions.get(0)));
            neighbors.add(current.moveTowards(directions.get(1)));
            neighbors.add(current.moveTowards(directions.get(2)));
            neighbors.add(current.moveTowards(directions.get(3)));

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

            Iterator<Position2D> it = neighbors.iterator();
            while(it.hasNext()){
                Position2D neighbor = it.next();

                // found a collect_treasure (finished)
                if(component.contains(neighbor)) {
                    path.push(neighbor);
                    return path;
                }
                // otherwise, filter invalid positions.
                else if(!withinBounds(neighbor) || visited.contains(neighbor)){
                    it.remove();
                }
            }

            switch(neighbors.size()){
                case 0:
                    // origin is exhausted.
                    path.pop();
                    break;

                default:
                    // push a random neighboring Position2D onto the stack.
                    Position2D next = neighbors.get(rng.nextInt(neighbors.size()));
                    path.push(next);
                    visited.add(next);
                    break;
            }
        }

        // if reached, no treasures were found.
        return new LinkedList<>();
    }



    public static void main(String[] args) {
        DungeonGenerator dungeonGenerator = new DungeonGenerator(60, 40);
//        dungeonGenerator.generateRoomedDungeonWithWalls();
//        dungeonGenerator.generateRoomedDungeon();
//        List<Set<Position2D>> components = dungeonGenerator.getConnectedComponents();
//        String s = "!@#$%^&*()_+-={}|[]\\:\";'<>?,./";
//        for (Set<Position2D> component : components) {
//            int index = rng.nextInt(s.length());
//            char displayChar = s.charAt(index);
//            s = s.substring(0,index) + s.substring(index + 1);
//            for (Position2D p : component) {
//                dungeonGenerator.cells[p.y()][p.x()] = displayChar;
//            }
//        }



        System.out.println(dungeonGenerator);
    }

    public List<Room> getRooms() {
        return rooms;
    }
}
