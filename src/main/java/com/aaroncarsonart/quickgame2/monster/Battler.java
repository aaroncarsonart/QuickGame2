package com.aaroncarsonart.quickgame2.monster;

import com.aaroncarsonart.quickgame2.Constants;

import java.util.Random;

public interface Battler {

    static Random rng = Constants.RNG;
    int getMaxHealth();
    int getHealth();
    void setHealth(int health);

    int getAttack();
    int getDefense();
    int getEvade();
    int getAccuracy();

    default int attack(Battler target) {
        int toHit = this.getAccuracy() + rng.nextInt(5);
        int evade = target.getEvade() / 2;
        if (toHit > evade) {
            int damage = Math.max(1, this.getAttack() + rng.nextInt(this.getAttack()) - target.getDefense() / 2);
            int newHealth = Math.max(0, target.getHealth() - damage);
            target.setHealth(newHealth);
            return damage;
        }
        return 0;
    }
}
