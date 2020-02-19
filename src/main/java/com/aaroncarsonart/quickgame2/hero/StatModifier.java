package com.aaroncarsonart.quickgame2.hero;

public class StatModifier {

    private Stat stat;
    private int modifier;

    public StatModifier(Stat stat, int modifier) {
        this.stat = stat;
        this.modifier = modifier;
    }

    public Stat getStat() {
        return stat;
    }

    public int getModifier() {
        return modifier;
    }

    public String toString() {
        String sign = modifier >= 0 ? "+" : "-";
        return stat.name().toLowerCase() + sign + modifier;
    }
}
