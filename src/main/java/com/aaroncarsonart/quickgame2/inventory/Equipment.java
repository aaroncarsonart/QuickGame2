package com.aaroncarsonart.quickgame2.inventory;

import com.aaroncarsonart.quickgame2.hero.StatModifier;

import java.util.Arrays;
import java.util.List;

public class Equipment extends Item {

    private List<StatModifier> statModifiers;
    private EquipmentType type;

    public Equipment(String name, double weight, int cost, EquipmentType type, StatModifier ... statModifiers) {
        this(name, weight, cost, type, Arrays.asList(statModifiers));
    }

    public Equipment(String name, double weight, int cost, EquipmentType type, List<StatModifier> statModifiers) {
        super(name, weight, cost);
        this.statModifiers = statModifiers;
        this.type = type;
    }

    public List<StatModifier> getStatModifiers() {
        return statModifiers;
    }

    public EquipmentType getType() {
        return type;
    }

    public String toString() {
        return String.format("%s,type=%s,statModifiers=%s",
                super.toString(), type, statModifiers);
    }
}
