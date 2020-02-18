package com.aaroncarsonart.quickgame2.hero;

public class HeroRace {

    public static final HeroRace HUMAN = new HeroRace("Human");
    public static final HeroRace ELF = new HeroRace("Elf");
    public static final HeroRace DWARF = new HeroRace("Dwarf");
    public static final HeroRace HALFLING = new HeroRace("Halfling");

    private String name;

    private HeroRace(String name) {
        this.name = name;
    }
}
