package com.aaroncarsonart.quickgame2.monster;

import com.aaroncarsonart.quickgame2.inventory.Item;
import imbroglio.Position2D;

import java.awt.Color;
import java.util.List;

public class Monster implements Battler {
    private Position2D pos;
    private String name;

    private int health;
    private int maxHealth;

    private int attack;
    private int defense;
    private int accuracy;
    private int evade;

    private int aggression;
    private int baseAggression;

    private int minEncounter;
    private int maxEncounter;

    private int gold;
    private int exp;
    private List<Loot> loot;
    private int minDepth;
    private int maxDepth;

    private Color color;
    private char sprite;

    private boolean seen;

    private List<Position2D> path;

    public Monster() {}

    /**
     * Make a copy of this monster.
     * @return
     */
    public Monster copy() {
        Monster monster = new Monster();
        monster.setName(name);
        monster.setHealth(health);
        monster.setMaxHealth(health);
        monster.setAttack(attack);
        monster.setDefense(defense);
        monster.setAccuracy(accuracy);
        monster.setMinEncounter(minEncounter);
        monster.setMaxEncounter(maxEncounter);
        monster.setAggression(aggression);
        monster.setBaseAggression(aggression);
        monster.setEvade(evade);
        monster.setGold(gold);
        // TODO add loot functionality
        monster.setLoot(null);
        monster.setExp(exp);
        monster.setMinDepth(minDepth);
        monster.setMaxDepth(maxDepth);
        monster.setColor(color);
        monster.setSprite(sprite);
        return monster;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public List<Loot> getLoot() {
        return loot;
    }

    public void setLoot(List<Loot> loot) {
        this.loot = loot;
    }

    public int getMinDepth() {
        return minDepth;
    }

    public void setMinDepth(int minDepth) {
        this.minDepth = minDepth;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public char getSprite() {
        return sprite;
    }

    public void setSprite(char sprite) {
        this.sprite = sprite;
    }

    public int getAggression() {
        return aggression;
    }

    public void setAggression(int aggression) {
        this.aggression = aggression;
    }

    public int getBaseAggression() {
        return baseAggression;
    }

    public void setBaseAggression(int baseAggression) {
        this.baseAggression = baseAggression;
    }

    public int getMinEncounter() {
        return minEncounter;
    }

    public void setMinEncounter(int minEncounter) {
        this.minEncounter = minEncounter;
    }

    public int getMaxEncounter() {
        return maxEncounter;
    }

    public void setMaxEncounter(int maxEncounter) {
        this.maxEncounter = maxEncounter;
    }

    public boolean hasPath() {
        return path != null;
    }

    public List<Position2D> getPath() {
        return path;
    }

    public void setPath(List<Position2D> path) {
        this.path = path;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    @Override
    public String toString() {
        return name;
    }
}
