package com.aaroncarsonart.quickgame2.util;

import com.aaroncarsonart.quickgame2.Constants;

import java.util.Random;

public class Rng extends Random {

    public Rng() {
        super();
    }

    public Rng(int seed) {
        super(seed);
    }

    private static Random rng = Constants.RNG;

    public static <T> T nextValue(T ... values) {
        return values[rng.nextInt(values.length)];
    }

    /**
     * Accepts arguments such as "1d6" and outputs the result
     * of rolling said many dies.
     * @param desc The string describing the die roll to make.
     * @return The result of rolling the dice.
     */
    public static int roll(String desc) {
        String[] values = desc.split("d");
        int count = Integer.parseInt(values[0]);
        int faces = Integer.parseInt(values[1]);
        return roll(count, faces);
    }

    public static int roll(int count, int faces) {
        int result = 0;
        for (int i = 0; i < count; i++) {
            int next = 1 + rng.nextInt(faces);
            result += next;
        }
        return result;
    }
}
