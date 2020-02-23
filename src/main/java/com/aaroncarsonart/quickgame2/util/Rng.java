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
}
