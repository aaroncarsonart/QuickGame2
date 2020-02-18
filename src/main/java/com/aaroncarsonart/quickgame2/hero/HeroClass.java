package com.aaroncarsonart.quickgame2.hero;

public class HeroClass {

    public static final HeroClass FIGHTER = new HeroClass("Fighter");
    public static final HeroClass WIZARD = new HeroClass("Wizard");
    public static final HeroClass CLERIC = new HeroClass("Cleric");
    public static final HeroClass THIEF = new HeroClass("Thief");

    private String name;

    private HeroClass(String name) {
        this.name = name;
    }
}
