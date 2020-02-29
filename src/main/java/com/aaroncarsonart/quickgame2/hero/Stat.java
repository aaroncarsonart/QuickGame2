package com.aaroncarsonart.quickgame2.hero;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public enum Stat {
    MAX_HEALTH,
    MAX_MANA,
    MAX_ENERGY,

    STRENGTH,
    STAMINA,
    AGILITY,
    INTELLIGENCE,
    WISDOM,
    CHARISMA,

    ATTACK,
    DEFENSE,
    ACCURACY,
    EVADE
    ;

    public IntSupplier getAccessor(Hero hero) {
        switch(this) {
            case MAX_HEALTH: return hero::getMaxHealth;
            case MAX_MANA: return hero::getMaxMana;
            case MAX_ENERGY: return hero::getMaxEnergy;

            case STRENGTH: return hero::getModifiedStrength;
            case STAMINA: return hero::getModifiedStamina;
            case AGILITY: return hero::getModifiedAgility;
            case INTELLIGENCE: return hero::getModifiedIntelligence;
            case WISDOM: return hero::getModifiedWisdom;
            case CHARISMA: return hero::getModifiedCharisma;

            case ATTACK: return hero::getAttack;
            case DEFENSE: return hero::getDefense;
            case ACCURACY: return hero::getAccuracy;
            case EVADE: return hero::getEvade;

            default:
        }
        return null;
    }

    public IntConsumer getSetter(Hero hero){
        switch(this) {
            case MAX_HEALTH: return hero::setMaxHealth;
            case MAX_MANA: return hero::setMaxMana;
            case MAX_ENERGY: return hero::setMaxEnergy;

            case STRENGTH: return hero::setStrength;
            case STAMINA: return hero::setStamina;
            case AGILITY: return hero::setAgility;
            case INTELLIGENCE: return hero::setIntelligence;
            case WISDOM: return hero::setWisdom;
            case CHARISMA: return hero::setCharisma;

            case ATTACK:
            case DEFENSE:
            case ACCURACY:
            case EVADE:

            default:
        }
        return null;
    }
}
