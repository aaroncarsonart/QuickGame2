package com.aaroncarsonart.quickgame2.hero;

public class Experience {

    public static final int[] TABLE = loadExpTable();
    public static final int MAX_LEVEL = 20;

    private static int[] loadExpTable() {
        int tableSize = MAX_LEVEL + 1;
        int[] exp = new int[tableSize];
        exp[1] = 0;
        exp[2] = 7;

        for (int level = 3; level < tableSize; level++) {
            int prevExp = exp[level - 1];
            exp[level] = prevExp + (int) (prevExp * 1.5);
        }
//        for (int level = 1; level < tableSize; level++) {
//            System.out.println("Level " + level  + ": " + exp[level]);
//        }

        return exp;
    }

    public static void main(String[] args) {
        System.out.println();
    }
}
