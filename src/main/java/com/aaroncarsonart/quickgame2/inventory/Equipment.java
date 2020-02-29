package com.aaroncarsonart.quickgame2.inventory;

import com.aaroncarsonart.quickgame2.hero.Stat;
import com.aaroncarsonart.quickgame2.hero.StatModifier;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Equipment extends Item {

    private LinkedHashMap<Stat, StatModifier> statModifiers;
    private EquipmentType type;

    public Equipment(String name, double weight, int cost, boolean stackable, EquipmentType type) {
        this(name, weight, cost, stackable, type, new LinkedHashMap<>());
    }

    public Equipment(String name, double weight, int cost, boolean stackable, EquipmentType type, LinkedHashMap<Stat, StatModifier> statModifiers) {
        super(name, weight, cost, stackable);
        this.statModifiers = statModifiers;
        this.type = type;
    }

    public Map<Stat, StatModifier> getStatModifiers() {
        return statModifiers;
    }

    public EquipmentType getType() {
        return type;
    }

    public String toString() {
        return String.format("%s,type=%s,statModifiers=%s",
                super.toString(), type, statModifiers);
    }

    @Override
    public char getSprite() {
        switch (type) {
            case WEAPON: return '/';
            case SHIELD: return '}';
            case ARMOR: return ']';
            case HELMET: return '^';
            case ETC: return '=';
            default: return '_';
        }
    }
}
