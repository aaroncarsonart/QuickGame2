package com.aaroncarsonart.quickgame2.monster;

import imbroglio.Position2D;

public class Monster {
    private Position2D pos;
    private int health;
    private int maxHealth;
    private int mana;
    private int maxMana;
    private int attack;
    private int defense;
    private int accuracy;
    private int evade;

    public Monster() {}

    public Position2D getPos() {
        return pos;
    }

    public void setPos(Position2D pos) {
        this.pos = pos;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getEvade() {
        return evade;
    }

    public void setEvade(int evade) {
        this.evade = evade;
    }
}
