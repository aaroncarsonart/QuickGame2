package com.aaroncarsonart.quickgame2.hero;

import com.aaroncarsonart.quickgame2.inventory.Equipment;
import com.aaroncarsonart.quickgame2.inventory.EquipmentType;
import com.aaroncarsonart.quickgame2.inventory.Inventory;
import com.aaroncarsonart.quickgame2.monster.Battler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Hero implements Battler {

    private String name;

    private HeroClass heroClass;
    private HeroRace heroRace;
    private int level;
    private int exp;

    private int health;
    private int maxHealth;

    private int mana;
    private int maxMana;

    private double energy;
    private int maxEnergy;

    private int strength;
    private int stamina;
    private int agility;
    private int intelligence;
    private int wisdom;
    private int charisma;

    // inventory
    private int gold;
    private Inventory inventory;

    // placeholder equipment

    private Equipment weapon;
    private Equipment shield;
    private Equipment armor;
    private Equipment helmet;
    private Equipment etc1;
    private Equipment etc2;

    Map<Stat, List<StatModifier>> statModifiers;

    public Hero() {
        statModifiers = new HashMap();
        for (Stat stat : Stat.values()) {
            statModifiers.put(stat, new ArrayList<>());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HeroClass getHeroClass() {
        return heroClass;
    }

    public void setHeroClass(HeroClass heroClass) {
        this.heroClass = heroClass;
    }

    public HeroRace getHeroRace() {
        return heroRace;
    }

    public void setHeroRace(HeroRace heroRace) {
        this.heroRace = heroRace;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
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

    public int getModifiedMaxHealth() {
        int modifiers = statModifiers.get(Stat.MAX_HEALTH).stream()
                .mapToInt(StatModifier::getModifier)
                .sum();
        return maxHealth + modifiers;
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

    public int getModifiedMaxMana() {
        int modifiers = statModifiers.get(Stat.MAX_MANA).stream()
                .mapToInt(StatModifier::getModifier)
                .sum();
        return maxMana + modifiers;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getModifiedMaxEnergy() {
        int modifiers = statModifiers.get(Stat.MAX_ENERGY).stream()
                .mapToInt(StatModifier::getModifier)
                .sum();
        return maxEnergy + modifiers;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public int getStrength() {
        return strength;
    }

    public int getModifiedStrength() {
        int modifiers = statModifiers.get(Stat.STRENGTH).stream()
                .mapToInt(StatModifier::getModifier)
                .sum();
        return strength + modifiers;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getStamina() {
        return stamina;
    }

    public int getModifiedStamina() {
        int modifiers = statModifiers.get(Stat.STAMINA).stream()
                .mapToInt(StatModifier::getModifier)
                .sum();
        return stamina + modifiers;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getAgility() {
        return agility;
    }

    public int getModifiedAgility() {
        int modifiers = statModifiers.get(Stat.AGILITY).stream()
                .mapToInt(StatModifier::getModifier)
                .sum();
        return agility + modifiers;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getModifiedIntelligence() {
        int modifiers = statModifiers.get(Stat.INTELLIGENCE).stream()
                .mapToInt(StatModifier::getModifier)
                .sum();
        return intelligence + modifiers;
    }
    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getWisdom() {
        return wisdom;
    }

    public int getModifiedWisdom() {
        int modifiers = statModifiers.get(Stat.WISDOM).stream()
                .mapToInt(StatModifier::getModifier)
                .sum();
        return wisdom + modifiers;
    }

    public void setWisdom(int wisdom) {
        this.wisdom = wisdom;
    }

    public int getCharisma() {
        return charisma;
    }

    public int getModifiedCharisma() {
        int modifiers = statModifiers.get(Stat.CHARISMA).stream()
                .mapToInt(StatModifier::getModifier)
                .sum();
        return charisma + modifiers;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public Equipment getWeapon() {
        return weapon;
    }

    public void setWeapon(Equipment weapon) {
        this.weapon = weapon;
    }

    public Equipment getShield() {
        return shield;
    }

    public void setShield(Equipment shield) {
        this.shield = shield;
    }

    public Equipment getArmor() {
        return armor;
    }

    public void setArmor(Equipment armor) {
        this.armor = armor;
    }

    public Equipment getHelmet() {
        return helmet;
    }

    public void setHelmet(Equipment helmet) {
        this.helmet = helmet;
    }

    public Equipment getEtc1() {
        return etc1;
    }

    public void setEtc1(Equipment etc1) {
        this.etc1 = etc1;
    }

    public Equipment getEtc2() {
        return etc2;
    }

    public void setEtc2(Equipment etc2) {
        this.etc2 = etc2;
    }

    public int getAttack() {
        int base = getModifiedStrength();
        int modifiers = statModifiers.get(Stat.ATTACK).stream()
                .mapToInt(StatModifier::getModifier)
                .sum();
        return base + modifiers;
    }

    public int getDefense() {
        int base = getModifiedStamina();
        int modifiers = statModifiers.get(Stat.DEFENSE).stream()
                .mapToInt(StatModifier::getModifier)
                .sum();
        return base + modifiers;
    }

    public int getAccuracy() {
        int base = getModifiedStrength() / 2 + getModifiedAgility() / 2;
        int modifiers = statModifiers.get(Stat.ACCURACY).stream()
                .mapToInt(StatModifier::getModifier)
                .sum();
        return base + modifiers;
    }

    public int getEvade() {
        int base = getModifiedAgility();
        int modifiers = statModifiers.get(Stat.EVADE).stream()
                .mapToInt(StatModifier::getModifier)
                .sum();
        return base + modifiers;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Equipment equipItem(Equipment newEquipment, EquipmentType type, int etcSlot) {
        Hero hero = this;
        Equipment prevEquipment = null;
        switch (type) {
            case WEAPON:
                prevEquipment = hero.getWeapon();
                hero.setWeapon(newEquipment);
                break;
            case SHIELD:
                prevEquipment = hero.getShield();
                hero.setShield(newEquipment);
                break;
            case ARMOR:
                prevEquipment = hero.getArmor();
                hero.setArmor(newEquipment);
                break;
            case HELMET:
                prevEquipment = hero.getHelmet();
                hero.setHelmet(newEquipment);
                break;
            case ETC:
                if (etcSlot == 1) {
                    prevEquipment = hero.getEtc1();
                    hero.setEtc1(newEquipment);
                } else if (etcSlot == 2) {
                    prevEquipment = hero.getEtc2();
                    hero.setEtc1(newEquipment);
                }
                break;
        }
        if (prevEquipment != null) {
            for (StatModifier modifier : prevEquipment.getStatModifiers().values()) {
                statModifiers.get(modifier.getStat()).remove(modifier);
//                Iterator<StatModifier> it = statModifiers.get(modifier.getStat()).iterator();
//                while(it.hasNext()) {
//                    StatModifier m2 = it.next();
//                    if (modifier == m2) {
//                        it.remove();
//                    }
//                }
            }
        }
        if (newEquipment != null) {
            for (StatModifier modifier : newEquipment.getStatModifiers().values()) {
                statModifiers.get(modifier.getStat()).add(modifier);
            }
        }
        return prevEquipment;
    }

    public int getStatValue(Stat stat) {
        switch(stat) {
            case STRENGTH: return getModifiedStrength();
            case AGILITY: return getModifiedAgility();
            case STAMINA: return getModifiedStamina();
            case INTELLIGENCE: return getModifiedIntelligence();
            case WISDOM: return getModifiedWisdom();
            case CHARISMA: return getModifiedCharisma();
            default: return 0;
        }
    }


}
